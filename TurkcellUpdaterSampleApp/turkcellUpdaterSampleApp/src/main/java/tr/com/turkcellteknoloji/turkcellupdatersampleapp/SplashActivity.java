package tr.com.turkcellteknoloji.turkcellupdatersampleapp;

import java.io.Serializable;

import tr.com.turkcellteknoloji.turkcellupdater.Message;
import tr.com.turkcellteknoloji.turkcellupdater.UpdaterDialogManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import tr.com.turkcellteknoloji.turkcellupdatersampleapp.R;

public class SplashActivity extends Activity implements UpdaterDialogManager.UpdaterUiListener {
	Message message;

	private EditText serverAddressEditText;

	private CheckBox postPropertiesCheckBox;

	private Button startUpdateCheckButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_NoTitleBar);
		setContentView(R.layout.activity_splash);
		serverAddressEditText = (EditText) findViewById(R.id.serverAddressEditText);
		postPropertiesCheckBox = (CheckBox) findViewById(R.id.postPropertiesCheckbox);
		startUpdateCheckButton = (Button) findViewById(R.id.startUpdateCheckButton);
		startUpdateCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				UpdaterDialogManager updaterUI = new UpdaterDialogManager(serverAddressEditText.getText().toString());
				updaterUI.setPostProperties(postPropertiesCheckBox.isChecked());
				updaterUI.setPostProperties(true);
				updaterUI.startUpdateCheck(SplashActivity.this, SplashActivity.this);
			}
		});

	}

	@Override
	public void onExitApplication() {
		finish();
	}

	@Override
	public void onUpdateCheckCompleted() {
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("message", (Serializable) message);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onDisplayMessage(Message message) {
		// To automatically display message:
		// return false;
		this.message = message;
		return true;
	}
}
