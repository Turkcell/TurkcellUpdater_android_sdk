/*******************************************************************************
 * Copyright (C) 2013 Turkcell
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package tr.com.turkcellteknoloji.turkcellupdater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps information about displayed updates like last display date and display count.
 * <br>
 * Data kept by this class is backed by an {@link SharedPreferences} object named {@value #SHARED_PREFERENCES_NAME}
 * @author Ugur Ozmen
 *
 */
class UpdateDisplayRecords {

    static final String SHARED_PREFERENCES_NAME = "turkcell-updater-update-display-records";

    private final SharedPreferences sharedPreferences;

    /**
     * Creates a new instance
     * @param context
     */
    UpdateDisplayRecords(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns display count of update with given <code>id</code>
     * @param id ID of queried update
     * @return total display count.
     */
    int getUpdateDisplayCount(int id) {
        return sharedPreferences.getInt(id + "-display-count", 0);
    }

    /**
     * Returns last display time of update with given <code>id</code>
     * @param id ID of queried update
     * @return last display date or <code>null</code> if update is not displayed yet.
     */
    Date getUpdateLastDisplayDate(int id) {
        final long millis = sharedPreferences.getLong(id + "-last-display-date", 0);
        if (millis == 0) {
            return null;
        } else {
            return new Date(millis);
        }
    }

    /**
     * Increases display count of update with given <code>id</code> by one and stores current time as last display date for the update.
     * @param id ID of queried update
     */
    @SuppressLint("NewApi")
    void onUpdateDisplayed(int id) {
        onUpdateDisplayed(id, new Date());
    }

    /**
     * Test friendly version of {@link #onUpdateDisplayed(int)}.
     * <br>
     * <em><strong>Note:</strong> This method is should only be used for testing purposes.</em>
     * @param id
     * @param now
     */
    @SuppressLint("NewApi")
    @Deprecated
    void onUpdateDisplayed(int id, Date now) {
        final int updateDisplayCount = getUpdateDisplayCount(id);
        final Editor edit = sharedPreferences.edit();
        edit.putInt(id + "-display-count", updateDisplayCount + 1);
        edit.putLong(id + "-last-display-date", now.getTime());
        if (android.os.Build.VERSION.SDK_INT > 8) {
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
    void deleteUpdateRecords(int id) {
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
