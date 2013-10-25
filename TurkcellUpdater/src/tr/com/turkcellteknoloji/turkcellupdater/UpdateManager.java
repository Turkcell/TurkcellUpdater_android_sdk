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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * Provides a auto update mechanism for Android applications
 * @author Ugur Ozmen
 */
public class UpdateManager {
	private final static int DOWNLOAD_PROGRESS_PERCENT = 95;


	/**
	 * Provides callback methods for update check results.<br>
	 * Conditionally one of 4 methods will be called at the end of a update check:
	 * <ul>
	 * <li>A new version is found, {@link UpdateCheckListener#onUpdateCheckCompleted(UpdateManager, Update)}</li>
	 * <li>A message should be displayed to user, {@link UpdateCheckListener#onUpdateCheckCompleted(UpdateManager, Message)}</li>
	 * <li>Completed successfully and no messages and no new versions found, {@link UpdateCheckListener#onUpdateCheckCompleted(UpdateManager)}</li>
	 * <li>Update check is fail due to an error</li>
	 * </ul>
	 *
	 * @see UpdateManager#checkUpdates(Context, URI, Properties, UpdateCheckListener)
	 * @author Ugur Ozmen
	 */
	public interface UpdateCheckListener {
		/**
		 * This method is called when update check is completed successfully and a newer version is found. Implementations should display {@link Update#description} to users.
		 * Implementations should not provide an option to cancel and continue to application if {@link Update#forceUpdate} is true.
		 *
		 * @param manager Update manager that has performed update check.
		 * @param update Information about next version found.
		 */
		void onUpdateCheckCompleted(UpdateManager manager, Update update);

		/**
		 * This method is called when update check is completed successfully and a message should be displayed to user.
		 * Implementations should not continue normal operation after this message is dismissed.
		 *
		 * @param manager Update manager that has performed update check.
		 * @param message Information delivered from server that should be displayed to user.
		 */
		void onUpdateCheckCompleted(UpdateManager manager, Message message);

		/**
		 * This method is called when update check is completed successfully and current version is the latest version.
		 *
		 * @param manager Update manager that has performed update check.
		 */
		void onUpdateCheckCompleted(UpdateManager manager);

		/**
		 * This method is called when update check is failed.
		 * @param manager Update manager that has performed update check.
		 * @param exception Failure reason.
		 */
		void onUpdateCheckFailed(UpdateManager manager, Exception exception);
	}

	/**
	 * Provides callback methods for update process events.
	 * @see UpdateManager#applyUpdate(Context, Update, UpdateListener)
	 * @author Ugur Ozmen
	 */
	public interface UpdateListener {

		/**
		 * Returns a percent value indicating update progress
		 * @param percent <code>null</code> if current state is interminable otherwise percent of completed progress
		 */
		void onUpdateProgress(Integer percent);

		/**
		 * Called when update is cancelled. Implementations should close application if {@link Update#forceUpdate} is true.
		 */
		void onUpdateCancelled();

		/**
		 * Called when update is successfully completed. Implementations should close application if {@link Update#forceUpdate} is true.
		 */
		void onUpdateCompleted();

		/**
		 * Indicates that update is failed due to a reason.
		 * @param exception Failure reason.
		 */
		void onUpdateFailed(Exception exception);
	}

	/**
	 * Creates an instance of {@link UpdateManager}.
	 */
	public UpdateManager() {
		Log.printProductInfo();
	}

	/**
	 * Starts an asynchronous operation for checking if a newer version of current application is available.
	 * @param context Current context.
	 * @param versionServerUri Location of update definitions.
	 * @param currentProperties properties of current application and device.
	 * @param postProperties <code>true</code> if current properties should post to server for server side processing.
	 * @param listener Callback listener.
	 * @throws UpdaterException if operation couldn't be started.
	 */
	public void checkUpdates(Context context, final URI versionServerUri, Properties currentProperties, boolean postProperties, UpdateCheckListener listener) throws UpdaterException {
		final DefaultHttpClient client = Utilities.createClient("TurkcellUpdater/1.0", false);
		final VersionMapRequest request = new VersionMapRequest(context, versionServerUri, currentProperties, postProperties, listener);
		try {
			request.executeAsync(client);
		} catch (Exception e) {
			throw new UpdaterException(e);
		}
	}

	/**
	 * Starts an asynchronous operation for installing newer version of application.
	 * @param context Current context.
	 * @param update Information about next version.
	 * @param listener Callback listener.
	 */
	public void applyUpdate(Context context, Update update, final UpdateListener listener) {
		if(update.targetGooglePlay) {
			openGooglePlayPage(context, update.targetPackageName, listener);
		} else if(update.targetWebsiteUrl !=null ) {
			openWebPage(context, update.targetWebsiteUrl, listener);
		} else if(update.targetPackageUrl != null){
			try {
				final DownloadRequest dr = new DownloadRequest();
				dr.setUri(update.targetPackageUrl.toURI());
				dr.setExpectedContentType("application/vnd.android.package-archive");
				dr.setDownloadHandler(new DownloadHandlerAdapter(context, listener));

				HttpClient client = Utilities.createClient("Turkcell Updater/1.0 ", false);
				dr.executeAsync(client );
			} catch (Exception e) {
				if(listener==null) {
					Log.e("Update failed", e);
				} else {
					listener.onUpdateFailed(e);
				}
			}
		}
	}

	private final class DownloadHandlerAdapter implements DownloadHandler {
		private final UpdateListener listener;
		private final Context context;
		private DownloadHandlerAdapter(Context context, UpdateListener listener) {
			this.listener = listener;
			this.context = context;
		}

		@Override
		public void onSuccess(byte[] result) {
			try {
				installPackage(context, result, listener);
			} catch (Exception e) {
				if(listener!=null) {
					listener.onUpdateFailed(e);
				}
			}
		}

		@Override
		public void onProgress(Integer percent) {
			if(listener!=null) {

				if(percent!=null) {
					percent *= DOWNLOAD_PROGRESS_PERCENT;
					percent /= 100;
				}

				listener.onUpdateProgress(percent);
			}
		}

		@Override
		public void onFail(Exception ex) {
			if(listener==null) {
				Log.e("Update failed", ex);
			} else {
				listener.onUpdateFailed(ex);
			}
		}

		@Override
		public void onCancelled() {
			if(listener!=null) {
				listener.onUpdateCancelled();
			}
		}
	}
	private void installPackage(final Context context, final byte[] apkContents, final UpdateListener listener) {
		final AsyncTask<Void, Void, File> task = new AsyncTask<Void, Void, File>(){
			volatile Exception exception;

			@SuppressLint("WorldReadableFiles")
			@Override
			protected File doInBackground(Void... params) {
				try {
					if(Utilities.isNullOrEmpty(apkContents)) {
						throw new NullPointerException("apkContents");
					}

					String tempFileName = "nextversion.apk";
					File tempFile = context.getFileStreamPath(tempFileName);
					if(tempFile.exists()) {
						tempFile.delete();
					}


					FileOutputStream fout = context.openFileOutput(tempFileName,Context.MODE_WORLD_READABLE);
					try {
						fout.write(apkContents);
						fout.flush();
					} finally {
						try {
							fout.close();
						} catch (Exception e) {
							// omit
						}
					}


					return tempFile;


				} catch (Exception e) {
					exception = e;
					Log.e("Couldn't save apk", e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(File result) {
				if(exception==null) {
					try {
						Uri data = Uri.fromFile(result);
						String type = "application/vnd.android.package-archive";
						Intent promptInstall = new Intent(Intent.ACTION_VIEW);
						promptInstall.setDataAndType(data, type);
						context.startActivity(promptInstall);
						if(listener!=null) {
							listener.onUpdateCompleted();
						}
						return;
					} catch (Exception e) {
						exception = e;
					}
				}

				if(listener==null) {
					Log.e("Update failed", exception);
				} else {
					listener.onUpdateFailed(exception);
				}

			}

		};
		task.execute();
	}

	private void openGooglePlayPage(Context context, String packageName, final UpdateListener listener) {
		if(listener!=null) {
			listener.onUpdateProgress(null);
		}

		try {
			try {
			    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+packageName)));
			}
		} catch (Exception e) {
			if(listener==null) {
				Log.e("open google play page failed", e);
			} else {
				listener.onUpdateFailed(e);
			}
		}

		if(listener!=null) {
			listener.onUpdateProgress(100);
			listener.onUpdateCompleted();
		}
	}

	private void openWebPage(Context context, URL url, final UpdateListener listener) {
		if(listener!=null) {
			listener.onUpdateProgress(null);
		}

		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.toExternalForm())));
		} catch (Exception e) {
			if(listener==null) {
				Log.e("open web page failed", e);
			} else {
				listener.onUpdateFailed(e);
			}
		}

		if(listener!=null) {
			listener.onUpdateProgress(100);
			listener.onUpdateCompleted();
		}
	}

	private class VersionMapRequest extends RestRequest {

		@Override
		protected String getExpectedResponseContentType() {
			return Configuration.EXPECTED_JSON_MIME_TYPE;
		}

		public VersionMapRequest(final Context context, final URI versionServerUri, final Properties currentProperties, boolean postProperties, final UpdateCheckListener listener) {
			if(postProperties && currentProperties!=null) {
				setHttpMethod(HttpMethod.POST);
				setInput(new JSONObject(currentProperties.toMap()));
			} else {
				setHttpMethod(HttpMethod.GET);
				setInputNone();
			}
			setUri(versionServerUri);
			setResultHandler(new RestJsonObjectResultHandler() {
				@Override
				public void onFail(Exception e) {
					Log.d("Couldn't read update configuration file", e);
					listener.onUpdateCheckFailed(UpdateManager.this, e);
				}

				@Override
				public void onSuccess(JSONObject jsonObject) {
					try {
						final String packageName = currentProperties.getValue(Properties.KEY_APP_PACKAGE_NAME);
						if(jsonObject!=null && jsonObject.has("errorMessage")) {
							Log.e("Remote error: " + jsonObject.optString("errorMessage"));
						}

						if(VersionsMap.isVersionMapOfPackageId(packageName, jsonObject)) {
							VersionsMap map = new VersionsMap(jsonObject);
							final Update update = map.getUpdate(currentProperties);
							if(update != null) {
								Log.i("Update found: " + update);
								listener.onUpdateCheckCompleted(UpdateManager.this, update);
							} else {
								final MessageDisplayRecords records = new MessageDisplayRecords(context);
								final Message message = map.getMessage(currentProperties, records, context);
								if(message == null) {
									Log.i("No update or message found.");
									listener.onUpdateCheckCompleted(UpdateManager.this);
								} else {
									Log.i("Message found: " + message);
									listener.onUpdateCheckCompleted(UpdateManager.this, message);
								}
							}
						} else {
							throw new UpdaterException("Configuration file packagename should be: " + packageName);
						}
					} catch (Exception e) {
						Log.d("Couldn't process update configuration file", e);
						listener.onUpdateCheckFailed(UpdateManager.this, e);
					}

				}
			});
		}
	}

}
