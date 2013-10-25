package tr.com.turkcellteknoloji.turkcellupdatersampleapp;

import tr.com.turkcellteknoloji.turkcellupdater.Message;
import tr.com.turkcellteknoloji.turkcellupdater.UpdaterDialogManager;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import tr.com.turkcellteknoloji.turkcellupdatersampleapp.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState==null) {
			Message message = (Message) getIntent().getSerializableExtra("message");
			if(message!=null) {
				final Dialog messageDialog = UpdaterDialogManager.createMessageDialog(this, message, null);
				messageDialog.show();
			}
		}

		setContentView(R.layout.activity_login);

	}


}
