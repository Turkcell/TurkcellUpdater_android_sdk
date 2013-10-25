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

import android.annotation.SuppressLint;
import android.os.Build;

/**
 * Provides a logging interface that can filter message debug messages when {@link Configuration#DEBUG} is <code>false</code>.
 * <br>
 * All log messages sent using this class will be tagged with {@value #TAG} string.
 * @author Ugur Ozmen
 */
class Log {
	final static String TAG = Configuration.PRODUCT_NAME;


	static void printProductInfo() {
		if(Configuration.DEBUG) {
			e("Debug version of " + Configuration.PRODUCT_NAME + " should not be used in production environment.");
		}

		i("version: " + Configuration.VERSION_NAME);
	}

	static void d(String msg) {
		if(Configuration.DEBUG) {
			android.util.Log.d(TAG, msg);
		}
	}
	static void d(String msg, Throwable tr) {
		if(Configuration.DEBUG) {
			android.util.Log.d(TAG, msg, tr);
		}
	}
	static void e(String msg) {
		android.util.Log.e(TAG, msg);
	}
	static void e(String msg, Throwable tr) {
		android.util.Log.e(TAG, msg, tr);
	}
	static void i(String msg) {
		if(Configuration.DEBUG) {
			android.util.Log.i(TAG, msg);
		}
	}
	static void i(String msg, Throwable tr) {
		if(Configuration.DEBUG) {
			android.util.Log.i(TAG, msg, tr);
		}
	}
	static void v(String msg) {
		if(Configuration.DEBUG) {
			android.util.Log.v(TAG, msg);
		}
	}
	static void v(String msg, Throwable tr) {
		if(Configuration.DEBUG) {
			android.util.Log.v(TAG, msg, tr);
		}
	}
	static void w(String msg) {
		if(Configuration.DEBUG) {
			android.util.Log.w(TAG, msg);
		}
	}
	static void w(String msg, Throwable tr) {
		if(Configuration.DEBUG) {
			android.util.Log.w(TAG, msg, tr);
		}
	}
	static void w(Throwable tr) {
		if(Configuration.DEBUG) {
			android.util.Log.w(TAG, tr);
		}
	}

	@SuppressLint("NewApi")
	static void wtf(String msg) {
		if(Build.VERSION.SDK_INT>8) {
			android.util.Log.wtf(TAG, msg);
		} else {
			android.util.Log.e(TAG, msg);
		}
	}

	@SuppressLint("NewApi")
	static void wtf(String msg, Throwable tr) {
		if(Build.VERSION.SDK_INT>8) {
			android.util.Log.wtf(TAG, msg, tr);
		} else {
			android.util.Log.e(TAG, msg, tr);
		}
	}

	@SuppressLint("NewApi")
	static void wtf(Throwable tr) {
		if(Build.VERSION.SDK_INT>8) {
			android.util.Log.wtf(TAG, tr);
		} else {
			android.util.Log.e(TAG, "wtf", tr);
		}
	}

}
