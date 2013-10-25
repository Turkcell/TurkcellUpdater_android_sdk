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

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Keeps information about displayed messages like last display date and display count.
 * <br>
 * Data kept by this class is backed by an {@link SharedPreferences} object named {@value #SHARED_PREFERENCES_NAME}
 * @author Ugur Ozmen
 *
 */
class MessageDisplayRecords {
	static final String SHARED_PREFERENCES_NAME = "turkcell-updater-message-display-records";

	private final SharedPreferences sharedPreferences;

	/**
	 * Creates a new instance
	 * @param context
	 */
	MessageDisplayRecords(Context context) {
		sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * Returns display count of message with given <code>id</code>
	 * @param id ID of queried message
	 * @return total display count.
	 */
	int getMessageDisplayCount(int id) {
		return sharedPreferences.getInt(id + "-display-count", 0);
	}

	/**
	 * Returns last display time of message with given <code>id</code>
	 * @param id ID of queried message
	 * @return last display date or <code>null</code> if message is not displayed yet.
	 */
	Date getMessageLastDisplayDate(int id) {
		final long millis = sharedPreferences.getLong(id + "-last-display-date", 0);
		if(millis == 0) {
			return null;
		} else {
			return new Date(millis);
		}
	}

	/**
	 * Increases display count of message with given <code>id</code> by one and stores current time as last display date for the message.
	 * @param id ID of queried message
	 */
	@SuppressLint("NewApi")
	void onMessageDisplayed(int id) {
		onMessageDisplayed(id, new Date());
	}

	/**
	 * Test friendly version of {@link #onMessageDisplayed(int)}.
	 * <br>
	 * <em><strong>Note:</strong> This method is should only be used for testing purposes.</em>
	 * @param id
	 * @param date
	 */
	@SuppressLint("NewApi")
	@Deprecated
	void onMessageDisplayed(int id, Date now) {
		final int messageDisplayCount = getMessageDisplayCount(id);
		final Editor edit = sharedPreferences.edit();
		edit.putInt(id + "-display-count", messageDisplayCount + 1);
		edit.putLong(id + "-last-display-date", now.getTime());
		if(android.os.Build.VERSION.SDK_INT>8) {
			edit.apply();
		} else {
			edit.commit();
		}
	}

	/**
	 * Deletes record for given id.
	 * <br>
	 * <em><strong>Note:</strong> This method is should only be used for testing purposes.</em>
	 * @param id
	 */
	@Deprecated
	void deleteMessageRecords(int id) {
		final Editor edit = sharedPreferences.edit();
		edit.remove(id + "-display-count");
		edit.remove(id + "-last-display-date");
		edit.commit();
	}

	/**
	 * Deletes all records.
	 * <br>
	 * <em><strong>Note:</strong> This method is should only be used for testing purposes.</em>
	 */
	@Deprecated
	void deleteAllRecords() {
		final Map<String, ?> all = sharedPreferences.getAll();
		Set<String> keys = new HashSet<String>(all.keySet());
		final Editor edit = sharedPreferences.edit();
		for (String key : keys) {
			edit.remove(key);
		}
		edit.commit();
	}
}
