package tr.com.turkcellteknoloji.turkcellupdater;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * Container for texts that are displayed on message dialog.
 * @see #get(String)
 * @see #KEY_TITLE
 * @see #KEY_MESSAGE
 * @see #KEY_IMAGE_URL
 * @author Ugur Ozmen
 */
public class MessageDescription extends LocalizedStringMap implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Key for dialog title
	 * @see #get(String)
	 */
	public final static String KEY_TITLE = "title";

	/**
	 * Key for text displayed inside dialog
	 * @see #get(String)
	 */
	public final static String KEY_MESSAGE = "message";

	/**
	 * Key for url of image that displayed in dialog
	 * @see #get(String)
	 */
	public final static String KEY_IMAGE_URL = "imageUrl";

	MessageDescription(String languageCode, String title, String message, String imageUrl) {
		super(languageCode, createMap(title, message, imageUrl));
	}

	MessageDescription(String languageCode, JSONObject jsonObject) {
		super(languageCode,jsonObject);
	}

	MessageDescription(String languageCode, String message) {
		super(languageCode,createMap(message));
	}

	private static Map<String, String> createMap(String title, String message, String imageUrl) {
		Map<String, String> result = createMap(message);
		result.put(KEY_TITLE, title);
		result.put(KEY_IMAGE_URL, imageUrl);
		return result;
	}
	private static Map<String, String> createMap(String message) {
		Map<String, String> result = new HashMap<String, String>();
		result.put(KEY_MESSAGE, message);
		return result;
	}


}

