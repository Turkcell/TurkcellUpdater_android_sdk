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
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;

/**
 * Static utility methods
 *
 * @author Ugur Ozmen
 */
class Utilities {
	static boolean isNull(Object o) {
		return o == null;
	}

	static boolean isNullOrEmpty(String s) {
		return isNull(s) || s.length() < 1;
	}

	static boolean isNullOrEmpty(int[] array) {
		return isNull(array) || array.length < 1;
	}

	static boolean isNullOrEmpty(byte[] array) {
		return isNull(array) || array.length < 1;
	}

	static boolean isNullOrEmpty(short[] array) {
		return isNull(array) || array.length < 1;
	}

	static boolean isNullOrEmpty(float[] array) {
		return isNull(array) || array.length < 1;
	}

	static boolean isNullOrEmpty(double[] array) {
		return isNull(array) || array.length < 1;
	}

	static boolean isNullOrEmpty(long[] array) {
		return isNull(array) || array.length < 1;
	}

	static boolean isNullOrEmpty(Object[] array) {
		return isNull(array) || array.length < 1;
	}

	static boolean isEmpty(String s) {
		return (!isNull(s)) && s.length() < 1;
	}

	static boolean isEmpty(int[] array) {
		return (!isNull(array)) && array.length < 1;
	}

	static boolean isEmpty(byte[] array) {
		return (!isNull(array)) && array.length < 1;
	}

	static boolean isEmpty(short[] array) {
		return (!isNull(array)) && array.length < 1;
	}

	static boolean isEmpty(float[] array) {
		return (!isNull(array)) && array.length < 1;
	}

	static boolean isEmpty(double[] array) {
		return (!isNull(array)) && array.length < 1;
	}

	static boolean isEmpty(long[] array) {
		return (!isNull(array)) && array.length < 1;
	}

	static boolean isEmpty(Object[] array) {
		return (!isNull(array)) && array.length < 1;
	}

	static void checkArgumentNotNull(String argumentName, Object argument) {
		if (isNull(argument)) {
			throw new IllegalArgumentException("'" + argumentName
					+ "' should not be null.");
		}
	}

	static int getIndexOfElement(int[] array, int element) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == element) {
					return i;
				}
			}

		}
		return -1;
	}

	static boolean isElementFound(int[] array, int element) {
		return getIndexOfElement(array, element) > -1;
	}

	static String getDigits(String s) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				buffer.append(s.charAt(i));
			}
		}
		return buffer.toString();
	}

	static Date getStartOfToday() {
		return getStartOfDate(new Date());
	}

	static Date getStartOfDate(Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	static Date addDays(Date date, int numberOfDays) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, numberOfDays);
		return calendar.getTime();
	}

	static Date addHours(Date date, int numberOfHours) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, numberOfHours);
		return calendar.getTime();
	}

	static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	@SuppressLint("NewApi")
	static boolean isTablet(Context context) {
		if (android.os.Build.VERSION.SDK_INT > 12) {
			return context.getResources().getConfiguration().smallestScreenWidthDp > 600;
		} else if (android.os.Build.VERSION.SDK_INT > 10) {
			int size = context.getResources().getConfiguration().screenLayout
					& Configuration.SCREENLAYOUT_SIZE_MASK;
			return (size == Configuration.SCREENLAYOUT_SIZE_LARGE)
					|| (size == Configuration.SCREENLAYOUT_SIZE_XLARGE);
		} else {
			return false;
		}
	}

	static String normalize(String string) {
		if (string == null) {
			return "";
		}

		return removeWhiteSpaces(string.toLowerCase(Locale.ENGLISH));
	}

	static String removeWhiteSpaces(String string) {
		if (string == null) {
			return "";
		}

		final int length = string.length();
		if (length < 1) {
			return string;
		}

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			final char c = string.charAt(i);
			if (!Character.isWhitespace(c)) {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	static Integer tryParseInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	static Drawable getDrawableByName(Context context, String name) {
		final Resources resources = context.getResources();
		final int id = getResourceIdByName(context, "drawable", name);
		if (id == 0) {
			return null;
		}
		return resources.getDrawable(id);
	}

	static View inflateLayoutByName(Context context, String name) {
		final int id = getResourceIdByName(context, "layout", name);
		if (id == 0) {
			return null;
		}

		final View view = LayoutInflater.from(context).inflate(id, null);
		return view;
	}

	static int getResourceIdByName(Context context, String type, String name) {
		final Resources resources = context.getResources();
		final int id = resources.getIdentifier(name, type,
				context.getPackageName());
		return id;
	}

	static DefaultHttpClient createClient(String userAgent,
			boolean acceptAllSslCertificates) {

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);

		// to make connection pool more fault tolerant
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
			public int getMaxForRoute(HttpRoute route) {
				return 10;
			}
		});

		HttpConnectionParams.setSoTimeout(params, 20 * 1000);

		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpClientParams.setRedirecting(params, false);

		HttpProtocolParams.setUserAgent(params, userAgent);

		SSLSocketFactory sslSocketFactory = null;

		if (acceptAllSslCertificates) {
			try {
				sslSocketFactory = new AcceptAllSocketFactory();
			} catch (Exception e) {
				// omitted
			}
		}

		if (sslSocketFactory == null) {
			sslSocketFactory = SSLSocketFactory.getSocketFactory();
		}

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
				params, schemeRegistry);

		final DefaultHttpClient client = new DefaultHttpClient(manager, params);

		return client;
	}

	private static class AcceptAllSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		private static KeyStore getTrustedKeyStore() {
			try {
				KeyStore trusted = KeyStore.getInstance(KeyStore
						.getDefaultType());

				trusted.load(null, null);

				return trusted;
			} catch (Exception e) {
				throw new AssertionError(e);
			}
		}

		AcceptAllSocketFactory() throws NoSuchAlgorithmException,
				KeyManagementException, KeyStoreException,
				UnrecoverableKeyException {
			super(getTrustedKeyStore());
			setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					Log.i("connection", "checkClientTrusted: " + authType
							+ " chain: " + Arrays.asList(chain));
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					Log.i("connection", "checkServerTrusted: " + authType
							+ " chain: " + Arrays.asList(chain));
				}

				public X509Certificate[] getAcceptedIssuers() {
					Log.i("connection", "getAcceptedIssuers: ");
					return null;
				}

			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}

	}

	static byte[] compress(String string) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes("UTF-8"));
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}

	static Date parseIsoDate(String string) {
		final String[] formats = new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
				"yyyy-MM-dd HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ssZ",
				"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mmZ", "yyyy-MM-dd HH:mm",
				"yyyy-MM-dd", };

		for (String format : formats) {
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					format, Locale.US);
			try {
				return simpleDateFormat.parse(string);
			} catch (ParseException e) {
				continue;
			}
		}

		return null;
	}

	static boolean isPackageInstalled(Context context, String packageName) {

		checkArgumentNotNull("context", context);
		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			throw new IllegalStateException("packageManager is null.");
		}
		ApplicationInfo ai = null;
		try {
			ai = packageManager.getApplicationInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			return false;
		}

		return ai != null;
	}

	static boolean isPackageInstalled(Context context, String packageName,
			int minVersionCode) {
		checkArgumentNotNull("context", context);
		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			throw new IllegalStateException("packageManager is null.");
		}
		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			return false;
		}

		return pi != null && pi.versionCode >= minVersionCode;
	}

	static void pipe(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
		int i;
		while ((i = is.read(buffer)) > -1) {
			os.write(buffer, 0, i);
		}
	}

	/**
	 * Copies contents of the stream to a byte array and closes stream.
	 *
	 * @param is
	 *            source input stream
	 * @return created byte array
	 * @throws IOException
	 */
	static byte[] getAsByteArray(InputStream is) throws IOException {
		if (is == null) {
			return null;
		}
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			Utilities.pipe(is, baos);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			baos.close();
		}

		return baos.toByteArray();
	}

	@SuppressLint("NewApi")
	static int getScreenOrientation(Activity activity) {
		if (Build.VERSION.SDK_INT < 8) {
			switch (activity.getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			case Configuration.ORIENTATION_LANDSCAPE:
				return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			default:
				return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
		}

		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int orientation;
		// if the device's natural orientation is portrait:
		if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
				&& height > width
				|| (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
				&& width > height) {
			switch (rotation) {
			case Surface.ROTATION_0:
				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
			case Surface.ROTATION_90:
				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				break;
			case Surface.ROTATION_180:
				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				break;
			case Surface.ROTATION_270:
				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				break;
			default:
				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
			}
		}
		// if the device's natural orientation is landscape or if the device
		// is square:
		else {
			switch (rotation) {
			case Surface.ROTATION_0:
				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				break;
			case Surface.ROTATION_90:
				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
			case Surface.ROTATION_180:
				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				break;
			case Surface.ROTATION_270:
				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				break;
			default:
				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				break;
			}
		}

		return orientation;
	}

	static int lockScreenOrientation(Activity activity) {
		int result = activity.getRequestedOrientation();
		final int screenOrientation = getScreenOrientation(activity);
		activity.setRequestedOrientation(screenOrientation);
		return result;
	}

	static void unlockScreenOrientation(Activity activity,
			int previousActivityOrientation) {
		activity.setRequestedOrientation(previousActivityOrientation);
	}

	static int dpToPixels(Context context, int dp) {
		Resources r = context.getResources();
		return (int) Math.ceil(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}

	// FEFF because this is the Unicode char represented by the UTF-8 byte order
	// mark (EF BB BF).
	private static final String UTF8_BOM = "\uFEFF";

	static String removeUTF8BOM(String s) {
		if (s.startsWith(UTF8_BOM)) {
			s = s.substring(1);
		}
		return s;
	}
}
