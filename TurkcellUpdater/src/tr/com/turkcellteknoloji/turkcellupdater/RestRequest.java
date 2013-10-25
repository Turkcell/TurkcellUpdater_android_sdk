/*******************************************************************************
 *
 *  Copyright (C) 2013 Turkcell
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *******************************************************************************/
package tr.com.turkcellteknoloji.turkcellupdater;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

class RestRequest {

	enum HttpMethod {
		GET,
		PUT,
		POST,
		DELETE
	}

	private HttpMethod httpMethod = HttpMethod.GET;
	private URI uri;
	protected URI getUri() {
		return uri;
	}

	protected void setUri(URI uri) {
		this.uri = uri;
	}

	private JSONObject inputJsonObject;

	private RestNoValueResultHandler noValueResultHandler;
	private RestJsonObjectResultHandler jsonObjectResultHandler;

	private int[] expectedHttpStatusCodes = {200, 201, 202, 204};

	private void setResultHandlerNone() {
		this.noValueResultHandler = null;
		this.jsonObjectResultHandler = null;
	}


	protected final void setResultHandler(RestNoValueResultHandler noValueResultHandler) {
		Utilities.checkArgumentNotNull("noValueResultHandler", noValueResultHandler);

		setResultHandlerNone();

		this.noValueResultHandler = noValueResultHandler;
	}

	protected final void setResultHandler(RestJsonObjectResultHandler jsonObjectResultHandler) {
		Utilities.checkArgumentNotNull("jsonObjectResultHandler", jsonObjectResultHandler);

		setResultHandlerNone();

		this.jsonObjectResultHandler = jsonObjectResultHandler;
	}

	private RestFailureHandler getFailureHandler() {
		if(noValueResultHandler != null) {
			return noValueResultHandler;
		}

		return jsonObjectResultHandler;
	}

	protected final void setInputNone() {
		this.inputJsonObject = null;
	}

	protected final void setInput(JSONObject inputJsonObject) {
		Utilities.checkArgumentNotNull("inputJsonObject", inputJsonObject);

		setInputNone();

		this.inputJsonObject = inputJsonObject;
	}

	protected final RestNoValueResultHandler getNoValueResultHandler() {
		return noValueResultHandler;
	}

	protected final RestJsonObjectResultHandler getJsonObjectResultHandler() {
		return jsonObjectResultHandler;
	}

	protected final JSONObject getInputJsonObject() {
		return inputJsonObject;
	}

	protected final int[] getExpectedHttpStatusCodes() {
		return expectedHttpStatusCodes;
	}

	protected final void setExpectedHttpStatusCodes(int expectedHttpStatusCode) {
		this.expectedHttpStatusCodes = new int[]{expectedHttpStatusCode};
	}

	protected final void setExpectedHttpStatusCodes(int... expectedHttpStatusCodes) {
		this.expectedHttpStatusCodes = expectedHttpStatusCodes;
	}

	protected final HttpMethod getHttpMethod() {
		return httpMethod;
	}

	protected final void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	protected HttpUriRequest createHttpRequest() throws Exception {
		final HttpMethod method = getHttpMethod();
		if(method==null) {
			throw new Exception("HTTP method should not be null");
		}

		final HttpUriRequest result;

		switch (method) {
		case GET:
			result = createHttpGetRequest();
			break;
		case POST:
			result = createHttpPostRequest();
			break;
		case PUT:
			result = createHttpPutRequest();
			break;
		case DELETE:
			result = createHttpDeleteRequest();
			break;
		default:
			throw new Exception("Unknown HTTP method:" + httpMethod.toString());
		}

		return result;
	}

	protected void appendHeaders(HttpUriRequest request) {
	}

	private HttpGet createHttpGetRequest() {
		final HttpGet result = new HttpGet();
		result.setURI(getUri());
		appendHeaders(result);
		return result;
	}

	private HttpPost createHttpPostRequest() throws Exception {
		final HttpPost result = new HttpPost();
		result.setURI(getUri());
		appendHeaders(result);
		result.setEntity(getRequestContents());
		return result;
	}

	private HttpPut createHttpPutRequest() throws Exception {
		final HttpPut result = new HttpPut();
		result.setURI(getUri());
		appendHeaders(result);
		result.setEntity(getRequestContents());
		return result;
	}

	private HttpDelete createHttpDeleteRequest() {
		final HttpDelete result = new HttpDelete();
		result.setURI(getUri());
		appendHeaders(result);
		return result;
	}


	/**
	 * Subclasses may override this method to place different object types to request body
	 * @return contents of request.
	 * @throws JsonConversionException
	 * @throws JSONException
	 */
	protected HttpEntity getRequestContents() throws Exception {
		try {
			final JSONObject jsonObject;
			if(inputJsonObject==null) {
				jsonObject = new JSONObject();
			} else {
				jsonObject = inputJsonObject;
			}

			final String string = jsonObject.toString();

			final byte[] bytes;
			bytes = string.getBytes("UTF-8");

			final ByteArrayEntity result = new ByteArrayEntity(bytes);
			result.setContentType("application/json; charset=UTF-8");

			return result;

		} catch (Exception e) {
			throw new Exception("Couldn't create request contents", e);
		}
	}

	protected String getExpectedResponseContentType() {
		return "application/json";
	}

	protected byte[] getByteArrayFromResponse(HttpResponse response) throws IOException {
		final HttpEntity entity = response.getEntity();
		if(entity==null) {
			return null;
		}

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			entity.writeTo(byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		} finally {
			try {
				entity.consumeContent();
			} catch (Exception e) {
				// Omit
			}
			try {
				byteArrayOutputStream.close();
			} catch (Exception e) {
			}
		}
	}

	protected String getStringFromResponse(HttpResponse response) throws IOException, UnsupportedEncodingException {
		final byte[] byteArray = getByteArrayFromResponse(response);
		if(byteArray==null) {
			return null;
		}
		return new String(byteArray, "UTF-8");
	}

	protected JSONObject getJsonObjectFromResponse(HttpResponse response)
			throws IOException, UnsupportedEncodingException, Exception {

		String json = getStringFromResponse(response);

		// see http://code.google.com/p/android/issues/detail?id=18508
		json = Utilities.removeUTF8BOM(json);

		JSONObject jsonObject = null;
		if(json!=null) {
			try {
				jsonObject = new JSONObject(json);
			} catch (JSONException e) {
				throw new Exception("Json result couldn't be parsed", e);
			}
		}
		return jsonObject;
	}

	protected void proccessJsonResponseContents(JSONObject jsonObject) throws Exception {
		if(jsonObjectResultHandler != null) {
			if(jsonObject==null) {
				throw new Exception("Json object is null");
			}
			jsonObjectResultHandler.onSuccess(jsonObject);

		} else if(noValueResultHandler!=null){
			noValueResultHandler.onSuccess();

		} else {
			throw new Exception("No result handlers found");

		}
	}

	void execute(final HttpClient client) throws Exception {
		try {
			final HttpResponse response;
			try {
				response = client.execute(createHttpRequest());
			} catch (ClientProtocolException e) {
				throw new Exception(e);
			}
			checkResponse(response);
			final JSONObject jsonObject = getJsonObjectFromResponse(response);
			proccessJsonResponseContents(jsonObject);

		} catch (Exception e) {
			final RestFailureHandler failureHandler = getFailureHandler();
			if(failureHandler==null) {
				e.printStackTrace();
			} else {
				try {
					failureHandler.onFail(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	AsyncTask<?, ?, ?> executeAsync(final HttpClient client) throws Exception {
		final Worker worker = new Worker(client);
		worker.execute();
		return worker;
	}

	protected void checkResponse(HttpResponse response) throws Exception {
		if(response == null) {
			throw new Exception("Response is null.");
		}
		checkResponseStatus(response);
		checkResponseContentType(response);
	}

	private void checkResponseStatus(HttpResponse response) throws Exception {
		final StatusLine statusLine = response.getStatusLine();

		if(statusLine == null) {
			throw new Exception("Status line is null.");
		}

		final int statusCode = statusLine.getStatusCode();
		final int[] expectedHttpStatusCodes = getExpectedHttpStatusCodes();
		if(expectedHttpStatusCodes!=null) {
			if(!Utilities.isElementFound(expectedHttpStatusCodes, statusCode)) {
				throw new Exception("Unexpected status code: " + statusCode);
			}
		}
	}

	private void checkResponseContentType(HttpResponse response) throws Exception {
		final String expectedResponseContentType = getExpectedResponseContentType();
		if(expectedResponseContentType!=null) {
			final Header contentTypeHeader = response.getFirstHeader("Content-Type");
			if(contentTypeHeader==null) {
				throw new Exception("Missing content-type header. '" + expectedResponseContentType + "' is expected");
			}

			final String contentType = contentTypeHeader.getValue();
			if(Utilities.isNullOrEmpty(contentType)) {
				throw new Exception("Missing content-type header value. '" + expectedResponseContentType + "' is expected");
			}

			if(!contentType.startsWith(expectedResponseContentType)) {
				throw new Exception("Unexpected content-type header value: '" + contentType + "'. '" + expectedResponseContentType + "' is expected");
			}

		}
	}

	class Worker extends AsyncTask<Void, Void, JSONObject> {
		final HttpClient client;
		private HttpUriRequest request;

		private Worker(HttpClient client) {
			super();
			this.client = client;
		}

		private volatile Exception exception;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				request = createHttpRequest();
			} catch (Exception e) {
				exception = e;
			}
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			if(exception!=null) {
				return null;
			}


			HttpResponse response = null;
			try {
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					throw new Exception(e);
				}


				checkResponse(response);

				return getJsonObjectFromResponse(response);
			} catch (Exception e) {

				// ensure that connection is released
				if(response != null) {
					final HttpEntity entity = response.getEntity();
					if(entity != null) {
						try {
							entity.consumeContent();
						} catch (IOException e1) {
							// omitted
						}
					}
				}

				exception = e;
				return null;
			}
		}

		@Override
		protected void onCancelled() {
			try {
				if(request!=null) {
					request.abort();
				}
			} catch (Exception e) {
				// omitted
			}
			super.onCancelled();
		}


		@Override
		protected void onPostExecute(JSONObject result) {
			if(exception==null) {
				try {
					proccessJsonResponseContents(result);
					return;
				} catch (Exception e) {
					exception = e;
				}
			}

			final RestFailureHandler failureHandler = getFailureHandler();
			if(failureHandler==null) {
				exception.printStackTrace();
			} else {
				try {
					failureHandler.onFail(exception);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}


		}



	}

}
