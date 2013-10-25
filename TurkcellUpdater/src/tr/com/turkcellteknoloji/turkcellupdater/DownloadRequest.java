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
import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.AsyncTask;

class DownloadRequest {

	private URI uri;

	URI getUri() {
		return uri;
	}

	void setUri(URI uri) {
		this.uri = uri;
	}

	private int[] expectedHttpStatusCodes = { 200, 201, 202, 204 };
	private String expectedContentType;
	private DownloadHandler downloadHandler;

	String getExpectedContentType() {
		return expectedContentType;
	}

	void setExpectedContentType(String expectedContentType) {
		this.expectedContentType = expectedContentType;
	}

	DownloadHandler getDownloadHandler() {
		return downloadHandler;
	}

	void setDownloadHandler(DownloadHandler downloadHandler) {
		this.downloadHandler = downloadHandler;
	}

	final int[] getExpectedHttpStatusCodes() {
		return expectedHttpStatusCodes;
	}

	final void setExpectedHttpStatusCodes(
			int... expectedHttpStatusCodes) {
		this.expectedHttpStatusCodes = expectedHttpStatusCodes;
	}

	HttpUriRequest createHttpRequest() throws Exception {
		return createHttpGetRequest();
	}

	protected void appendHeaders(HttpUriRequest request) {
	}

	private HttpGet createHttpGetRequest() {
		final HttpGet result = new HttpGet();
		result.setURI(getUri());
		appendHeaders(result);
		return result;
	}

	protected byte[] getByteArrayFromResponse(HttpResponse response)
			throws IOException {
		final HttpEntity entity = response.getEntity();
		if (entity == null) {
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

	AsyncTask<?, ?, ?> executeAsync(final HttpClient client)
			throws Exception {
		final Worker worker = new Worker(client);
		worker.execute();
		return worker;
	}

	protected void checkResponse(HttpResponse response) throws Exception {
		if (response == null) {
			throw new Exception("Response is null.");
		}
		checkResponseStatus(response);
		checkResponseContentType(response);
	}

	private void checkResponseStatus(HttpResponse response)
			throws Exception {
		final StatusLine statusLine = response.getStatusLine();

		if (statusLine == null) {
			throw new Exception("Status line is null.");
		}

		final int statusCode = statusLine.getStatusCode();
		final int[] expectedHttpStatusCodes = getExpectedHttpStatusCodes();
		if (expectedHttpStatusCodes != null) {
			if (!Utilities.isElementFound(expectedHttpStatusCodes, statusCode)) {
				throw new Exception("Unexpected status http code: " + statusCode);
			}
		}
	}

	private void checkResponseContentType(HttpResponse response)
			throws Exception {
		if (expectedContentType != null) {
			final Header contentTypeHeader = response
					.getFirstHeader("Content-Type");
			if (contentTypeHeader == null) {
				throw new Exception("Missing content-type header. '"
						+ expectedContentType + "' is expected");
			}

			final String contentType = contentTypeHeader.getValue();
			if (Utilities.isNullOrEmpty(contentType)) {
				throw new Exception("Missing content-type header value. '"
						+ expectedContentType + "' is expected");
			}

			if (!contentType.startsWith(expectedContentType)) {
				throw new Exception(
						"Unexpected content-type header value: '" + contentType
								+ "'. '" + expectedContentType
								+ "' is expected");
			}

		}
	}

	class Worker extends AsyncTask<Void, Integer, byte[]> {
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
		protected byte[] doInBackground(Void... params) {
			if (exception != null) {
				return null;
			}

			HttpResponse response = null;
			try {
				response = client.execute(request);

				checkResponse(response);

				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return null;
				}
				long totalLength = entity.getContentLength();
				long completedLenght = 0;

				if (totalLength > 0) {
					publishProgress(0);
				} else {
					publishProgress((Integer) null);
				}

				final InputStream is = entity.getContent();
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				try {

					final byte[] buffer = new byte[5120];
					int i = 0;
					while ((i = is.read(buffer)) > -1) {
						os.write(buffer, 0, i);

						completedLenght += i;

						if (totalLength > 0) {
							int percent = (int) ((100 * completedLenght) / totalLength);
							if (percent > -1 && percent < 101) {
								publishProgress(percent);
							} else {
								publishProgress((Integer) null);
							}
						}
					}
					publishProgress(100);

					return os.toByteArray();

				} finally {
					try {
						is.close();
					} catch (Exception e) {
						// Omit
					}

					try {
						os.close();
					} catch (Exception e) {
						// Omit
					}
				}

			} catch (Exception e) {

				exception = e;
				return null;

			} finally {
				// ensure that connection is released
				if (response != null) {
					final HttpEntity entity = response.getEntity();
					if (entity != null) {
						try {
							entity.consumeContent();
						} catch (IOException e1) {
							// Omit
						}
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			try {
				if (request != null) {
					request.abort();
				}
			} catch (Exception e) {
				// omitted
			}
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(byte[] result) {
			if (exception == null) {
				try {
					if (downloadHandler != null) {
						downloadHandler.onSuccess(result);
					}
					return;
				} catch (Exception e) {
					exception = e;
				}
			}

			if (downloadHandler == null) {
				exception.printStackTrace();
			} else {
				try {
					downloadHandler.onFail(exception);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			try {
				if (downloadHandler != null) {
					if (!Utilities.isNullOrEmpty(values)) {
						downloadHandler.onProgress(values[values.length - 1]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
