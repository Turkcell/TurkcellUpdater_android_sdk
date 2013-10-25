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

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION_CODES;
import android.provider.Settings.Secure;
import android.util.Base64;

/**
 * Represents Device and application properties.
 * Following property values are automatically retrieved:
 * <ul>
 * <li>{@link #KEY_APP_INSTALLER_PACKAGE_NAME}</li>
 * <li>{@link #KEY_APP_PACKAGE_NAME}</li>
 * <li>{@link #KEY_APP_VERSION_CODE}</li>
 * <li>{@link #KEY_APP_VERSION_NAME}</li>
 * <li>{@link #KEY_DEVICE_API_LEVEL}</li>
 * <li>{@link #KEY_DEVICE_BRAND}</li>
 * <li>{@link #KEY_DEVICE_IS_TABLET}</li>
 * <li>{@link #KEY_DEVICE_LANGUAGE}</li>
 * <li>{@link #KEY_DEVICE_MCC}</li>
 * <li>{@link #KEY_DEVICE_MNC}</li>
 * <li>{@link #KEY_DEVICE_MODEL}</li>
 * <li>{@link #KEY_DEVICE_PRODUCT}</li>
 * </ul><br>
 * These values can be overrided by calling {@link #setValue(String, String)} method.
 * Also it is possible to introduce new key-value pairs using {@link #KEY_COSTUM_PREFIX}.
 * @author Ugur Ozmen
 */
public class Properties {
	private final Map<String, String> map;

	/**
	 * Package name of application.<br>
	 * Example value: "com.sample.app"
	 * <br>
	 * <strong>Overriding value of this key is not recommended.</strong>
	 */
	public static final String KEY_APP_PACKAGE_NAME = "appPackageName";

	/**
	 * Version name of application which is defined in AndroidManifest.xml typically in Major.Minor.Revision or Major.Minor format.<br>
	 * Example value: "1.0.0".
	 */
	public static final String KEY_APP_VERSION_NAME = "appVersionName";

	/**
	 * An integer version code of application which is defined in AndroidManifest.xml.<br>
	 * Example value: "10".
	 */
	public static final String KEY_APP_VERSION_CODE = "appVersionCode";

	/**
	 * Package name of application installed current application. Maybe <code>null</code> if not specified.<br>
	 * Example value: "com.android.vending" of Google play.
	 */
	public static final String KEY_APP_INSTALLER_PACKAGE_NAME = "appInstallerPackageName";

	/**
	 * Version code of Android OS. See {@link VERSION_CODES}.<br>
	 * Example value: "10" for Android 2.3.3.
	 */
	public static final String KEY_DEVICE_API_LEVEL = "deviceApiLevel";

	/**
	 * Name of operating system of device.<br>
	 * Value: "Android".
	 * <br>
	 * <strong>Overriding value of this key is not recommended.</strong>
	 */
	public static final String KEY_DEVICE_OS_NAME = "deviceOsName";

	/**
	 * Version name of operating system of device.<br>
	 * Example value: "2.3.3".
	 */
	public static final String KEY_DEVICE_OS_VERSION = "deviceOsVersion";

	/**
	 * Brand name of device.<br>
	 * Example value: "htc_europe" for HTC Wildfire S.
	 */
	public static final String KEY_DEVICE_BRAND = "deviceBrand";

	/**
	 * Model name of device.<br>
	 * Example value: "HTC Wildfire S A510e" for HTC Wildfire S.
	 */
	public static final String KEY_DEVICE_MODEL = "deviceModel";

	/**
	 * Product name of the device.<br>
	 * Example value: "htc_marvel" for HTC Wildfire S.
	 */
	public static final String KEY_DEVICE_PRODUCT = "deviceProduct";

	/**
	 * "true" if devices is a tablet, otherwise "false". Since there is no clear evidence to determine if an Android device
	 * is tablet or not, devices with minimum screen size wider than 600 dpi are considered as tablets.<br>
	 * Example values: "true", "false".
	 */
	public static final String KEY_DEVICE_IS_TABLET = "deviceIsTablet";

	/**
	 * Two letter language code of device
	 * (see: <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1</a>).<br>
	 * Example values: "en", "tr", "fr".
	 */
	public static final String KEY_DEVICE_LANGUAGE = "deviceLanguage";

	/**
	 * "x-" is prefix for application defined keys of arbitrary properties.<br>
	 * Applications may define and add own custom property key-value pairs for application specific filters.<br>
	 * Example values: "x-foo", "x-bar".
	 */
	public static final String KEY_COSTUM_PREFIX = "x-";

	/**
	 * Mobile country code of device. See <a href="http://en.wikipedia.org/wiki/Mobile_country_code">Mobile country code</a><br>
	 * Example value: "286" for Turkey.
	 */
	public static final String KEY_DEVICE_MCC = "deviceMcc";

	/**
	 * Mobile network code of device. See <a href="http://en.wikipedia.org/wiki/Mobile_country_code">Mobile country code</a><br>
	 * Example value: "1" for Turkcell.
	 */
	public static final String KEY_DEVICE_MNC = "deviceMnc";

	/**
	 * A unique  alphanumeric identifier for device.<br>
	 * Example value: "5e5aC2coeO0UuPY/nH/C3DdelqE4MuTkywh2aB9PT84"
	 */
	public static final String KEY_DEVICE_ID = "deviceId";

	/**
	 * An integer number that is used to define updater version used by application.<br>
	 * Example value: "1" for initial version of updater sdk.
	 * <br>
	 * <strong>Overriding value of this key is not recommended.</strong>
	 */
	public static final String KEY_UPDATER_LEVEL = "updaterLevel";

	/**
	 * Creates an instance and automatically retrives current device and application properties.
	 * To override property values, use {@link #setValue(String, String)} method.
	 * @param context current context.
	 */
	public Properties(Context context) {
		this(new HashMap<String, String>());
		autoFetch(context);
	}

	Properties(Map<String, String> map) {
		this.map = map;
	}

	void autoFetch(Context context) {
		final PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return;
		}
		String packageName = packageInfo.packageName;

		setValue(KEY_APP_PACKAGE_NAME, packageName);
		setValue(KEY_APP_VERSION_CODE, Integer.toString(packageInfo.versionCode));
		setValue(KEY_APP_VERSION_NAME, packageInfo.versionName);
		setValue(KEY_APP_INSTALLER_PACKAGE_NAME, context.getPackageManager().getInstallerPackageName(packageName));
		setValue(KEY_DEVICE_BRAND, android.os.Build.BRAND);
		setValue(KEY_DEVICE_PRODUCT, android.os.Build.PRODUCT);
		setValue(KEY_DEVICE_MODEL, android.os.Build.MODEL);
		setValue(KEY_DEVICE_API_LEVEL, Integer.toString(android.os.Build.VERSION.SDK_INT));
		setValue(KEY_DEVICE_IS_TABLET, Boolean.toString(Utilities.isTablet(context)));
		setValue(KEY_DEVICE_LANGUAGE, context.getResources().getConfiguration().locale.getLanguage());
		setValue(KEY_DEVICE_MCC, Integer.toString(context.getResources().getConfiguration().mcc));
		setValue(KEY_DEVICE_MNC, Integer.toString(context.getResources().getConfiguration().mnc));
		setValue(KEY_UPDATER_LEVEL, Integer.toString(Configuration.UPDATER_LEVEL));
		setValue(KEY_DEVICE_OS_NAME, "android");
		setValue(KEY_DEVICE_OS_VERSION, android.os.Build.VERSION.RELEASE);

		String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		if(!Utilities.isNullOrEmpty(androidId)) {
			try {
				final MessageDigest digest = MessageDigest.getInstance("SHA-256");
				digest.reset();
				androidId = "7c1094d7ea9c4da11d17" + androidId;
				final byte[] ba = digest.digest(androidId.getBytes("UTF-8"));
				androidId = Base64.encodeToString(ba, Base64.NO_CLOSE | Base64.NO_PADDING | Base64.NO_WRAP);
				setValue(KEY_DEVICE_ID, androidId);
			} catch (Exception e) {
				Log.e("couldn't calculate device id hash", e);
			}
		}



	}

	/**
	 * Returns value of given <code>key</code>
	 * @param key
	 * @return value if key is found, otherwise <code>null</code>
	 */
	public String getValue(String key) {
		return map.get(key);
	}

	/**
	 * Adds or overrides settings
	 *
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		map.put(key, value);
	}

	/**
	 * Returns a {@link Map} containing property keys and values.
	 * @return map containing property keys and values.
	 */
	public Map<String, String> toMap() {
		return new HashMap<String, String>(map);
	}

}
