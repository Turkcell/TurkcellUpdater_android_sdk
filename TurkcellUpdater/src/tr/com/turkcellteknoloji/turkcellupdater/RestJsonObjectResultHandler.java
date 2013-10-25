package tr.com.turkcellteknoloji.turkcellupdater;

import org.json.JSONObject;

interface RestJsonObjectResultHandler extends RestFailureHandler {
	void onSuccess(JSONObject jsonObject);
}
