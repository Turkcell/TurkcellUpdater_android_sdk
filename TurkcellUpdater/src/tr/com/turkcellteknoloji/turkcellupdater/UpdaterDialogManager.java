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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.HttpClient;

import tr.com.turkcellteknoloji.turkcellupdater.UpdateManager.UpdateCheckListener;
import tr.com.turkcellteknoloji.turkcellupdater.UpdateManager.UpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Provides a mechanism for checking updates and displaying notification dialogs
 * to user when needed.<br>
 * Usage example:<br>
 *
 * <pre>
 * <code>
 * package com.example.app;
 *
 * import tr.com.turkcellteknoloji.turkcellupdater.Message;
 * import tr.com.turkcellteknoloji.turkcellupdater.UpdaterDialogManager;
 * import android.app.Activity;
 * import android.os.Bundle;
 *
 * public class SplashActivity extends Activity implements UpdaterDialogManager.UpdaterUiListener {
 * 	private static final String UPDATE_SERVER_URL
 * 			= "http://example.com/updater-configuration";
 *
 * 	{@literal @}Override
 * 	protected void onCreate(Bundle savedInstanceState) {
 * 		super.onCreate(savedInstanceState);
 * 		setContentView(R.layout.splah);
 *
 * 		UpdaterDialogManager updaterUI = new UpdaterDialogManager(UPDATE_SERVER_URL);
 * 		updaterUI.startUpdateCheck(this, this);
 * 	}
 *
 * 	{@literal @}Override
 * 	public void onExitApplication() {
 * 		finish();
 * 	}
 *
 * 	{@literal @}Override
 * 	public void onUpdateCheckCompleted() {
 * 		// TODO: Add your application initialization step here
 * 	}
 *
 * 	{@literal @}Override
 * 	public boolean onDisplayMessage(Message message) {
 * 		// TODO: return true if you want to define your own message handling mechanism.
 * 		return false;
 * 	}
 * }
 * </code>
 * </pre>
 *
 * @author Ugur Ozmen
 * @see #startUpdateCheck(Activity, UpdaterUiListener)
 *
 */
public class UpdaterDialogManager implements UpdateCheckListener,
		UpdateListener {

	/**
	 * Provide callback methods for update checking process.
	 *
	 * @author Ugur Ozmen
	 * @see UpdaterDialogManager#startUpdateCheck(Activity, UpdaterUiListener)
	 */
	public interface UpdaterUiListener {
		/**
		 * Called when application should exit immediately. Typically this
		 * method is called in one of following conditions:
		 * <ul>
		 * <li>New version is found and ready to install. Application should be
		 * closed in order to launch new version.</li>
		 * <li>User refused to install a mandatory update. see:
		 * {@link Update#forceUpdate}</li>
		 * </ul>
		 */
		void onExitApplication();

		/**
		 * Update check is completed. Application should continue initialization
		 * process.
		 */
		void onUpdateCheckCompleted();

		/**
		 * Called when a message should be displayed to user.<br>
		 * This call always will be followed by
		 * {@link #onUpdateCheckCompleted()} call. if this method returns
		 * <true> message will be handled by application and displayed to user
		 * later by calling
		 * {@link UpdaterDialogManager#createMessageDialog(Activity, Message, DialogInterface.OnDismissListener)}
		 * . method<br>
		 * if this method returns <false> message will be automatically
		 * displayed to user immediately.<br>
		 *
		 * @param message
		 *            Message that will be displayed to user.
		 * @return <code>true</code> if message is handled by application it
		 *         self.
		 */
		boolean onDisplayMessage(Message message);
	}

	private final String updateServerUrl;

	private Activity activity;
	private UpdateManager updateManager;
	private ProgressDialog updateProgressDialog;
	private Update update;
	private UpdaterUiListener listener;

	private boolean postProperties;

	/**
	 * @return <code>true</code> if current properties should post to server for
	 *         server side processing.
	 */
	public boolean doesPostProperties() {
		return postProperties;
	}

	/**
	 *
	 * @param postProperties <code>true</code> if current properties should
	 * post to server for server side processing.
	 */
	public void setPostProperties(boolean postProperties) {
		this.postProperties = postProperties;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param updateServerUrl
	 *            Location of update instructions.
	 */
	public UpdaterDialogManager(String updateServerUrl) {
		super();
		this.updateServerUrl = updateServerUrl;
		updateManager = new UpdateManager();
	}

	/**
	 * @return previously set listener
	 * @see #setListener(UpdaterUiListener)
	 * @see #startUpdateCheck(Activity, UpdaterUiListener)
	 */
	public UpdaterUiListener getListener() {
		return listener;
	}

	/**
	 * Sets a listener for update results
	 *
	 * @param listener
	 * @see #getListener()
	 */
	public void setListener(UpdaterUiListener listener) {
		this.listener = listener;
	}

	/**
	 * @return previously set activity
	 * @see #setActivity(Activity)
	 * @see #startUpdateCheck(Activity, UpdaterUiListener)
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * Sets an activity as parent of dialogs.
	 *
	 * @param activity
	 * @see #getListener()
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Starts update check. <br>
	 * {@link #setActivity(Activity)} and
	 * {@link #setListener(UpdaterUiListener)} methods should be called before
	 * calling this method.
	 */
	public void startUpdateCheck() {
		startUpdateCheck(null);
	}

	/**
	 * Starts update check. Default properties are will be used.
	 *
	 * @param activity
	 *            Parent activity.
	 * @param listener
	 *            Callback listener.
	 */
	public void startUpdateCheck(Activity activity, UpdaterUiListener listener) {
		startUpdateCheck(null, activity, listener);
	}

	/**
	 * Starts update check.
	 *
	 * @param properties
	 *            Current properties.
	 * @param activity
	 *            Parent activity.
	 * @param listener
	 *            Callback listener.
	 */
	public void startUpdateCheck(Properties properties, Activity activity,
			UpdaterUiListener listener) {
		setActivity(activity);
		setListener(listener);
		startUpdateCheck(properties);
	}

	/**
	 * Starts update check with specified properties.<br>
	 * {@link #setActivity(Activity)} and
	 * {@link #setListener(UpdaterUiListener)} methods should be called before
	 * calling this method.
	 */
	public void startUpdateCheck(Properties properties) {
		if (activity == null) {
			throw new IllegalStateException("'activity' is null");
		}

		if (listener == null) {
			throw new IllegalStateException("'listener' is null");
		}

		try {
			URI versionServerUri = new URI(updateServerUrl);
			Properties currentProperties = properties == null ? new Properties(
					activity) : properties;
			updateManager.checkUpdates(activity, versionServerUri,
					currentProperties, postProperties, this);
		} catch (Exception e) {
			Log.e("update check failed", e);
			onCompleted();
		}
	}

	private void onCompleted() {
		if (listener != null) {
			listener.onUpdateCheckCompleted();
		}
	}

	private void onExit() {
		if (listener != null) {
			listener.onExitApplication();
		}
	}

	@Override
	public void onUpdateCheckCompleted(UpdateManager manager,
			final Update update) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		if (update.forceExit) {
			builder.setTitle(R.string.service_is_not_available);
		} else if (update.forceUpdate) {
			builder.setTitle(R.string.update_required);
		} else {
			builder.setTitle(R.string.update_found);
		}

		final View dialogContentsView = createUpdatesFoundDialogContentsView(update);
		builder.setView(dialogContentsView);

		initializeUpdatesFoundDialogButtons(builder, update);
		builder.setCancelable(false);
		final AlertDialog alertDialog = builder.create();

		alertDialog.show();
	}

	private static boolean isAlreadyInstalled(Context context, Update update) {
		if (context == null || update == null) {
			return false;
		}

		if (Utilities.isNullOrEmpty(update.targetPackageName)) {
			return false;
		}

		final String currentPackageName = context.getPackageName();
		final String normalizedCurrentPackageName = Utilities
				.normalize(currentPackageName);
		final String normalizedTargetPackageName = Utilities
				.normalize(update.targetPackageName);
		if (normalizedCurrentPackageName.equals(normalizedTargetPackageName)) {
			return false;
		}
		if (Utilities.isPackageInstalled(context, update.targetPackageName,
				update.targetVersionCode)) {
			return true;
		}
		return false;
	}

	private void initializeUpdatesFoundDialogButtons(
			final AlertDialog.Builder builder, final Update update) {

		if (!update.forceExit) {

			Intent launchIntent = null;
			try {
				if (isAlreadyInstalled(activity, update)) {
					launchIntent = activity
							.getPackageManager()
							.getLaunchIntentForPackage(update.targetPackageName);
				}
			} catch (Exception e) {
				Log.e("Couldn't get launch intent for application: "
						+ update.targetPackageName, e);
			}

			if (launchIntent != null) {
				final Intent i = launchIntent;
				builder.setPositiveButton(R.string.launch,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								activity.startActivity(i);
								onExit();
							}
						});
			} else {
				builder.setPositiveButton(R.string.install,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								updateProgressDialog = new ProgressDialog(
										activity);
								updateProgressDialog.setMax(100);
								updateProgressDialog
										.setTitle(getActivity()
												.getString(
														R.string.downloading_new_version));
								updateProgressDialog.setCancelable(false);
								updateProgressDialog
										.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								updateProgressDialog.setIndeterminate(false);
								UpdaterDialogManager.this.update = update;
								updateProgressDialog.show();
								updateManager.applyUpdate(activity, update,
										UpdaterDialogManager.this);
							}
						});
			}
		}

		if (update.forceExit || update.forceUpdate) {
			builder.setNegativeButton(R.string.exit_application,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onExit();
						}
					});

		} else {
			builder.setNegativeButton(R.string.remind_me_later,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onCompleted();
						}
					});

		}
	}

	@SuppressLint("NewApi")
	private View createUpdatesFoundDialogContentsView(Update update) {
		Context context = activity;

		final AlertDialog.Builder builder;

		// Workaround for dialog theme problems
		if(android.os.Build.VERSION.SDK_INT>10) {
			builder = new AlertDialog.Builder(context);
			context = builder.getContext();
		} else {
			context = new ContextThemeWrapper(context, android.R.style.Theme_Dialog);
			builder = new AlertDialog.Builder(context);
		}

		builder.setTitle("Send feedback");

		final LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogContentsView = inflater.inflate(R.layout.updater_dialog_update_found, null, false);

		final TextView messageTextView = (TextView) dialogContentsView
				.findViewById(R.id.dialog_update_found_message);
		final TextView warningTextView = (TextView) dialogContentsView
				.findViewById(R.id.dialog_update_found_warning);
		final TextView whatIsNewTextView = (TextView) dialogContentsView
				.findViewById(R.id.dialog_update_found_what_is_new);

		String warnings = null;
		String message = null;
		String whatIsNew = null;

		if (update.description != null) {
			warnings = update.description.get(UpdateDescription.KEY_WARNINGS);
			message = update.description.get(UpdateDescription.KEY_MESSAGE);
			whatIsNew = update.description
					.get(UpdateDescription.KEY_WHAT_IS_NEW);
		}

		if (Utilities.isNullOrEmpty(message)) {
			messageTextView.setVisibility(View.GONE);
		} else {
			messageTextView.setText(message);
		}

		if (Utilities.isNullOrEmpty(warnings)) {
			warningTextView.setVisibility(View.GONE);
		} else {
			warningTextView.setText(warnings);
		}

		if (Utilities.isNullOrEmpty(whatIsNew)) {
			whatIsNewTextView.setVisibility(View.GONE);
		} else {
			whatIsNewTextView.setText(whatIsNew);
		}
		return dialogContentsView;
	}

	@Override
	public void onUpdateCheckFailed(UpdateManager manager, Exception exception) {
		Log.e("update check failed", exception);
		onCompleted();
	}

	@Override
	public void onUpdateProgress(Integer percent) {
		if (updateProgressDialog == null) {
			return;
		}
		Log.v("Download percent: "
				+ (percent == null ? "?" : percent.toString()));
		if (percent == null) {
			updateProgressDialog.setIndeterminate(true);
		} else {
			updateProgressDialog.setIndeterminate(false);
			updateProgressDialog.setProgress(percent);
		}
	}

	@Override
	public void onUpdateCancelled() {
		if (updateProgressDialog != null) {
			updateProgressDialog.dismiss();
		}

		if (update == null || !update.forceUpdate) {
			onCompleted();
			return;
		} else {
			onExit();
		}

	}

	@Override
	public void onUpdateCompleted() {
		if (updateProgressDialog != null) {
			updateProgressDialog.dismiss();
		}
		onExit();
	}

	@Override
	public void onUpdateFailed(Exception exception) {
		Log.e("update check failed", exception);
		if (updateProgressDialog != null) {
			updateProgressDialog.dismiss();
		}

		AlertDialog.Builder builder = new Builder(activity);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(R.string.error_occured);
		builder.setMessage(R.string.update_couldn_t_be_completed);
		builder.setCancelable(false);
		if (update == null || !update.forceUpdate) {
			builder.setNeutralButton(R.string.continue_, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onCompleted();
				}
			});

		} else {
			builder.setNeutralButton(R.string.exit_application,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onExit();
						}
					});

		}
		builder.create().show();

	}

	@Override
	public void onUpdateCheckCompleted(UpdateManager manager, Message message) {
		if (listener == null || !listener.onDisplayMessage(message)) {
			displayMessage(message);
		} else {
			onCompleted();
		}
	}

	@Override
	public void onUpdateCheckCompleted(UpdateManager manager) {
		onCompleted();
		return;
	}

	private void displayMessage(Message message) {
		final Dialog dialog = createMessageDialog(activity, message,
				new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						onCompleted();
					}
				});
		dialog.show();
	}

	/**
	 * Creates a dialog for given message.
	 *
	 * @param activity
	 *            Parent activity.
	 * @param message
	 *            Message contents
	 * @param dismissListener
	 *            Listener that will be called when dialog is closed or
	 *            cancelled.
	 * @return Created dialog.
	 */
	public static Dialog createMessageDialog(Activity activity,
			Message message, OnDismissListener dismissListener) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		final String title = message.description == null ? null
				: message.description.get(MessageDescription.KEY_TITLE);
		if (!Utilities.isNullOrEmpty(title)) {
			builder.setTitle(title);
		}

		final View dialogContentsView = createMessageDialogContentsView(
				activity, message.description);
		builder.setView(dialogContentsView);

		initializeMessageDialogButtons(activity, builder, message);
		builder.setCancelable(true);

		final AlertDialog dialog = builder.create();

		if (Utilities.isNullOrEmpty(title)) {
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		}

		dialog.setOnDismissListener(dismissListener);
		return dialog;
	}

	private static void initializeMessageDialogButtons(final Activity activity,
			Builder builder, final Message message) {

		final boolean viewButtonTargetGooglePlay;
		boolean viewButtonEnabled = false;
		if (message != null) {
			if (message.targetGooglePlay
					&& !Utilities.isNullOrEmpty(message.targetPackageName)) {
				viewButtonTargetGooglePlay = true;
				viewButtonEnabled = true;
			} else if (message.targetWebsiteUrl != null) {
				viewButtonEnabled = true;
				viewButtonTargetGooglePlay = false;
			} else {
				viewButtonTargetGooglePlay = false;
			}
		} else {
			viewButtonTargetGooglePlay = false;
		}

		if (!viewButtonEnabled) {
			builder.setNeutralButton(R.string.close, null);
		} else {
			builder.setNegativeButton(R.string.close, null);

			OnClickListener onClickListener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (viewButtonTargetGooglePlay) {
						openGooglePlayPage(activity, message.targetPackageName);
					} else {
						openWebPage(activity, message.targetWebsiteUrl);
					}

				}
			};

			builder.setPositiveButton(R.string.view, onClickListener);

		}

	}

	private static void openGooglePlayPage(Context context, String packageName) {
		try {
			try {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("market://details?id=" + packageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://play.google.com/store/apps/details?id="
								+ packageName)));
			}
		} catch (Exception e) {
			Log.e("open google play page failed", e);
		}
	}

	private static void openWebPage(Context context, URL url) {
		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url
					.toExternalForm())));
		} catch (Exception e) {
			Log.e("open web page failed", e);
		}
	}

	@SuppressLint("NewApi")
	private static View createMessageDialogContentsView(Activity activity,
			MessageDescription messageDescription) {

		Context context = activity;

		final AlertDialog.Builder builder;

		// Workaround for dialog theme problems
		if(android.os.Build.VERSION.SDK_INT>10) {
			builder = new AlertDialog.Builder(context);
			context = builder.getContext();
		} else {
			context = new ContextThemeWrapper(context, android.R.style.Theme_Dialog);
			builder = new AlertDialog.Builder(context);
		}

		builder.setTitle("Send feedback");

		final LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogContentsView = inflater.inflate(R.layout.updater_dialog_message, null, false);
		final TextView textView = (TextView) dialogContentsView
				.findViewById(R.id.dialog_update_message_text);
		final ImageView imageView = (ImageView) dialogContentsView
				.findViewById(R.id.dialog_update_message_image);
		final ViewSwitcher switcher = (ViewSwitcher) dialogContentsView
				.findViewById(R.id.dialog_update_message_switcher);

		String messageText = null;
		String imageUrl = null;

		if (messageDescription != null) {
			messageText = messageDescription
					.get(MessageDescription.KEY_MESSAGE);
			imageUrl = messageDescription.get(MessageDescription.KEY_IMAGE_URL);
		}

		if (Utilities.isNullOrEmpty(messageText)) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setText(messageText);
		}

		if (Utilities.isNullOrEmpty(imageUrl)) {
			switcher.setVisibility(View.GONE);
		} else {
			URI uri;
			try {
				uri = new URI(imageUrl);
			} catch (URISyntaxException e) {
				uri = null;
			}

			if (uri != null) {
				DownloadRequest request = new DownloadRequest();
				request.setUri(uri);
				request.setDownloadHandler(new DownloadHandler() {

					@Override
					public void onSuccess(byte[] result) {
						// Load image from byte array
						final Bitmap bitmap = BitmapFactory.decodeByteArray(
								result, 0, result.length);
						imageView.setImageBitmap(bitmap);

						// Hide progress bar and display image
						if (switcher != null) {
							switcher.setDisplayedChild(1);
						}
					}

					@Override
					public void onProgress(Integer percent) {

					}

					@Override
					public void onFail(Exception ex) {
						Log.e("Message image couldn't be loaded", ex);
					}

					@Override
					public void onCancelled() {

					}
				});
				HttpClient client = Utilities.createClient(
						"Turkcell Updater/1.0 ", false);
				try {
					request.executeAsync(client);
				} catch (Exception e) {
					Log.e("Message image couldn't be loaded", e);
				}

			} else {
				switcher.setVisibility(View.GONE);
			}

		}

		return dialogContentsView;
	}
}