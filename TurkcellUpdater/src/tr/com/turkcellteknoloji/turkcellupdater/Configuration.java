package tr.com.turkcellteknoloji.turkcellupdater;

/**
 * Static runtime configuration for this library.
 * @author Ugur Ozmen
 */
class Configuration {

	public final static String VERSION_NAME = "1.0";
	public final static String PRODUCT_NAME = "TurkcellUpdater";

	/**
	 * Library compliance level. This value should be increased after non-backward-compatible changes are introduced to this library.
	 */
	final static int UPDATER_LEVEL = 3;

	/**
	 * Setting this value <code>true</code> activates debug behavior. This value always should be <code>false</code> in production versions.
	 */
	final static boolean DEBUG = BuildConfig.DEBUG;

	/**
	 * Setting this value to <code>null</code> disables MIME type checking for JSON files.
	 */
	final static String EXPECTED_JSON_MIME_TYPE = DEBUG ? null : "application/json";
}
