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

import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

class VersionsMap {
	final String packageName;
	private final List<UpdateEntry> updateEntries;
	private final List<MessageEntry> messageEntries;

	VersionsMap(String packageName, List<UpdateEntry> updateEntries, List<MessageEntry> messageEntries) throws UpdaterException {
		super();
		this.packageName = Utilities.removeWhiteSpaces(packageName);
		this.updateEntries = updateEntries;
		this.messageEntries = messageEntries;

		validate();
	}

	VersionsMap(JSONObject jsonObject) throws UpdaterException {
		this.packageName = Utilities.removeWhiteSpaces(jsonObject.optString("packageName", null));
		this.updateEntries = new Vector<UpdateEntry>();

		final JSONArray updatesList = jsonObject.optJSONArray("updates");
		if(updatesList!=null) {
			for(int i = 0; i<updatesList.length(); i++) {
				final JSONObject o = updatesList.optJSONObject(i);
				if(o!=null) {
					try {
						UpdateEntry ue = new UpdateEntry(o);
						updateEntries.add(ue);
					} catch (Exception e) {
						Log.e("Error occured while processing update entry. Entry is omitted.", e);
					}
				}
			}
		}

		this.messageEntries = new Vector<MessageEntry>();

		final JSONArray messagesList = jsonObject.optJSONArray("messages");
		if(messagesList!=null) {
			for(int i = 0; i<messagesList.length(); i++) {
				final JSONObject o = messagesList.optJSONObject(i);
				if(o!=null) {
					try {
						MessageEntry me = new MessageEntry(o);
						messageEntries.add(me);
					} catch (Exception e) {
						Log.e("Error occured while processing message entry. Entry is omitted.", e);
					}
				}
			}
		}

		validate();
	}

	static boolean isVersionMapOfPackageId(String packageName, JSONObject jsonObject) {
		if(jsonObject == null) {
			return false;
		}

		packageName = Utilities.removeWhiteSpaces(packageName);
		if(packageName.length() < 1) {
			return false;
		}

		String s = jsonObject.optString("packageName", null);
		s = Utilities.removeWhiteSpaces(s);

		return packageName.equals(s);
	}

	Update getUpdate(Properties currentProperties) throws UpdaterException {
		if(updateEntries == null) {
			return null;
		}

		for (UpdateEntry updateEntry : updateEntries) {
			if(updateEntry==null) {
				continue;
			}
			try {
				if(updateEntry.shouldDisplay(currentProperties)) {
					return updateEntry.getUpdate(currentProperties);
				}
			} catch (Exception e) {
				Log.e("Error occured while searching update entry to display", e);
			}
		}
		return null;
	}

	Message getMessage(Properties currentProperties, MessageDisplayRecords records, Context context) {
		if(messageEntries==null) {
			return null;
		}
		for(MessageEntry entry:messageEntries) {
			if(entry==null) {
				continue;
			}
			try {
				if(entry.shouldDisplay(currentProperties, records, context)) {
					return entry.getMessageToDisplay(currentProperties, records);
				}
			} catch (Exception e) {
				Log.e("Error occured while searching message entry to display", e);
			}

		}
		return null;
	}

	private void validate() throws UpdaterException {
		if (packageName == null || packageName.length() < 1) {
			throw new UpdaterException(
					"'packageName' shoud not be a null or empty.");
		}
	}
}
