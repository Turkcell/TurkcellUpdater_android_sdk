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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import android.content.Context;

class MessageEntry extends FilteredEntry {
	final List<MessageDescription> messageDescriptions;

	final int id;

	final String targetPackageName;
	final URL targetWebsiteUrl;
	final boolean targetGooglePlay;

	final int displayPeriodInHours;
	final Date displayAfterDate;
	final Date displayBeforeDate;
	final int maxDisplayCount;

	MessageEntry(List<Filter> filters, int id,
			List<MessageDescription> messageDescriptions,
			int displayPeriodInHours, Date displayAfterDate,
			Date displayBeforeDate, int maxDisplayCount,
			String targetPackageName,
			URL targetWebsiteUrl, boolean targetGooglePlay)
			throws UpdaterException {
		super(filters);
		this.messageDescriptions = messageDescriptions;
		this.displayPeriodInHours = displayPeriodInHours;
		this.displayAfterDate = displayAfterDate;
		this.displayBeforeDate = displayBeforeDate;
		this.maxDisplayCount = maxDisplayCount;
		this.targetPackageName = targetPackageName;
		this.targetGooglePlay = targetGooglePlay;
		this.targetWebsiteUrl = targetWebsiteUrl;
		this.id = id==0 ? generateId() : id;

		validate();
	}

	MessageEntry(JSONObject jsonObject) throws UpdaterException {
		super(jsonObject);
		this.messageDescriptions = createMessageDescriptions(jsonObject);
		this.displayPeriodInHours = jsonObject
				.optInt("displayPeriodInHours", 0);
		this.displayAfterDate = getDate(jsonObject, "displayAfterDate");
		this.displayBeforeDate = getDate(jsonObject, "displayBeforeDate");
		this.maxDisplayCount = jsonObject.optInt("maxDisplayCount",
				Integer.MAX_VALUE);

		this.targetWebsiteUrl = getUrl(jsonObject, "targetWebsiteUrl");

		this.targetPackageName = Utilities.removeWhiteSpaces(jsonObject
				.optString("targetPackageName"));

		this.targetGooglePlay = jsonObject.optBoolean("targetGooglePlay");

		int i = jsonObject.optInt("id", 0);
		this.id = i==0 ? generateId() : i;

		validate();
	}

	private int generateId() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (targetGooglePlay ? 1231 : 1237);
		result = prime
				* result
				+ ((targetPackageName == null) ? 0 : targetPackageName
						.hashCode());
		result = prime
				* result
				+ ((targetWebsiteUrl == null) ? 0 : targetWebsiteUrl.hashCode());

		result = prime
				* result
				+ ((messageDescriptions == null) ? 0 : messageDescriptions.hashCode());
		return result;
	}

	private static URL getUrl(JSONObject jsonObject, String key)
			throws UpdaterException {
		String spec = Utilities.removeWhiteSpaces(jsonObject.optString(key));
		if ("".equals(spec)) {
			return null;
		}

		try {
			return new URL(spec);
		} catch (MalformedURLException e) {
			throw new UpdaterException("'" + key + "' url is malformatted", e);
		}
	}

	private static Date getDate(JSONObject jsonObject, String name) {
		if (jsonObject == null || name == null) {
			return null;
		}

		final String s = jsonObject.optString(name, null);
		if (s == null) {
			return null;
		}
		return Utilities.parseIsoDate(s);
	}

	private static List<MessageDescription> createMessageDescriptions(
			JSONObject jsonObject) {
		final List<MessageDescription> result = new Vector<MessageDescription>();
		final JSONObject udsObject = jsonObject.optJSONObject("descriptions");

		if (udsObject != null) {
			Iterator<?> languages = udsObject.keys();
			while (languages.hasNext()) {
				String languageCode = languages.next().toString();
				final JSONObject o = udsObject.optJSONObject(languageCode);
				if (o != null) {
					MessageDescription ud = new MessageDescription(
							languageCode, o);
					result.add(ud);
				} else {
					final String s = udsObject.optString(languageCode, null);
					if (s != null) {
						MessageDescription ud = new MessageDescription(
								languageCode, s);
						result.add(ud);
					}
				}

			}
		}

		return result;
	}

	boolean shouldDisplay(Properties properties, MessageDisplayRecords records, Context context) {
		final Date now = new Date();
		return shouldDisplay(properties, records, context, now);
	}

	/**
	 * Test friendly version of {@link #shouldDisplay(Properties, MessageDisplayRecords, Context)}<br>
	 * <em><strong>Note:</strong> This method is should only be used for testing purposes.</em>
	 * @param properties
	 * @param records
	 * @param context
	 * @param now
	 * @return
	 */
	@Deprecated
	boolean shouldDisplay(Properties properties, MessageDisplayRecords records, Context context, final Date now) {
		// check filters
		if (!isMatches(properties)) {
			return false;
		}

		// check if it is early to display message
		if (displayAfterDate != null) {
			if (displayAfterDate.after(now)) {
				return false;
			}
		}

		// check if it is late to display message
		if (displayBeforeDate != null) {
			if (displayBeforeDate.before(now)) {
				return false;
			}
		}

		// check if it is displayed more than specified count
		if (maxDisplayCount < Integer.MAX_VALUE) {
			final int count = records.getMessageDisplayCount(id);
			if(count >= maxDisplayCount) {
				return false;
			}
		}

		// check if it is displayed earlier than specified period
		if (displayPeriodInHours > 0) {
			final Date messageLastDisplayDate = records.getMessageLastDisplayDate(id);

			// check if message displayed before
			if(messageLastDisplayDate!=null) {
				final Date date = Utilities.addHours(now, -displayPeriodInHours);
				if(messageLastDisplayDate.after(date)) {
					return false;
				}
			}
		}

		// check if target application is already installed
		if(!Utilities.isNull(targetPackageName)) {
			if(Utilities.isPackageInstalled(context, targetPackageName)) {
				return false;
			}
		}

		return true;
	}

	Message getMessageToDisplay(Properties properties, MessageDisplayRecords records) {
		return getMessageToDisplay(properties, records, new Date());
	}

	/**
	 * Test friendly version of {@link #getMessageToDisplay(Properties, MessageDisplayRecords)}.
	 * <br>
	 * <em><strong>Note:</strong> This method is should only be used for testing purposes.</em>
	 * @param properties
	 * @param records
	 * @param now
	 * @return
	 */
	@Deprecated
	Message getMessageToDisplay(Properties properties, MessageDisplayRecords records, Date now) {
		String languageCode = null;
		if(properties!=null) {
			final String s = properties.getValue(Properties.KEY_DEVICE_LANGUAGE);
			if(!Utilities.isNullOrEmpty(s)) {
				languageCode = s;
			}

		}

		final MessageDescription description = LocalizedStringMap.select(messageDescriptions, languageCode);
		records.onMessageDisplayed(id, now);

		return new Message(description, targetWebsiteUrl, targetGooglePlay, targetPackageName);
	}

	private void validate() throws UpdaterException {
		if (targetGooglePlay && Utilities.isNullOrEmpty(targetPackageName)) {
			throw new UpdaterException(
					"'targetPackageName' shoud be not be empty if target is Google Play");
		}
	}



}
