package tr.com.turkcellteknoloji.turkcellupdater;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * Container for update message strings that are displayed in a update notification dialog.
 * @see #get(String)
 * @see #KEY_WHAT_IS_NEW
 * @see #KEY_MESSAGE
 * @see #KEY_WARNINGS
 * @author Ugur Ozmen
 */
public class UpdateDescription extends LocalizedStringMap {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Key for summary information describing update contents.
	 * @see #get(String)
	 */
	public final static String KEY_MESSAGE = "message";
	/**
	 * Key for text describing changes and new features of new version.
	 * @see #get(String)
	 */
	public final static String KEY_WHAT_IS_NEW = "whatIsNew";

	/**
	 * Key for warning text about the update. Any important issues that user should know before updating should be described here.
	 * @see #get(String)
	 */
	public final static String KEY_WARNINGS = "warnings";

	UpdateDescription(String languageCode, String message) {
		this(languageCode, message, null, null);
	}

	UpdateDescription(String languageCode, String message, String whatIsNew,
			String warnings) {
		super(languageCode, createMap(message, whatIsNew, warnings));
	}

	UpdateDescription(String languageCode, JSONObject jsonObject) {
		super(languageCode,jsonObject);
	}

	private static Map<String, String> createMap(String message, String whatIsNew,
			String warnings) {
		Map<String, String> result = new HashMap<String, String>();
		result.put(KEY_MESSAGE, message);
		result.put(KEY_WARNINGS, warnings);
		result.put(KEY_WHAT_IS_NEW, whatIsNew);
		return result;
	}
}

