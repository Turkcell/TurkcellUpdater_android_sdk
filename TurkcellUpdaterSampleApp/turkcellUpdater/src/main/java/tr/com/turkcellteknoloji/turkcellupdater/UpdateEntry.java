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

import android.content.Context;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

class UpdateEntry extends FilteredEntry {

    final List<UpdateDescription> updateDescriptions;

    final int id;

    final int targetVersionCode;
    final String targetPackageName;

    final URL targetPackageUrl;
    final URL targetWebsiteUrl;
    final boolean targetGooglePlay;
    final int displayPeriodInHours;
    final int maxDisplayCount;
    final boolean forceUpdate;
    final boolean forceExit;

    UpdateEntry(List<Filter> filters, int id, List<UpdateDescription> updateDescriptions, int displayPeriodInHours, int maxDisplayCount, int targetVersionCode, String targetPackageName, URL targetPackageUrl, URL targetWebsiteUrl, boolean targetGooglePlay, boolean forceUpdate, boolean forceExit) throws UpdaterException {
        super(filters);
        this.updateDescriptions = updateDescriptions;
        this.targetVersionCode = targetVersionCode;
        this.targetPackageName = targetPackageName;
        this.targetPackageUrl = targetPackageUrl;
        this.targetWebsiteUrl = targetWebsiteUrl;
        this.targetGooglePlay = targetGooglePlay;
        this.forceUpdate = forceUpdate;
        this.forceExit = forceExit;
        this.displayPeriodInHours = displayPeriodInHours;
        this.maxDisplayCount = maxDisplayCount;
        this.id = id == 0 ? generateId() : id;
        validate();
    }

    UpdateEntry(JSONObject jsonObject) throws UpdaterException {
        super(jsonObject);
        this.targetVersionCode = jsonObject.optInt("targetVersionCode", -1);
        this.forceUpdate = jsonObject.optBoolean("forceUpdate");
        this.forceExit = jsonObject.optBoolean("forceExit");
        this.updateDescriptions = createUpdateDescritions(jsonObject);
        this.targetPackageUrl = getUrl(jsonObject, "targetPackageUrl");
        this.targetWebsiteUrl = getUrl(jsonObject, "targetWebsiteUrl");
        this.targetPackageName = Utilities.removeWhiteSpaces(jsonObject.optString("targetPackageName"));
        this.targetGooglePlay = jsonObject.optBoolean("targetGooglePlay");
        this.displayPeriodInHours = jsonObject.optInt("displayPeriodInHours", 0);
        this.maxDisplayCount = jsonObject.optInt("maxDisplayCount", Integer.MAX_VALUE);
        int i = jsonObject.optInt("id", 0);
        this.id = i == 0 ? generateId() : i;
        validate();
    }

    private int generateId() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (targetGooglePlay ? 1231 : 1237);
        result = prime * result + ((targetPackageName == null) ? 0 : targetPackageName.hashCode());
        result = prime * result + ((targetWebsiteUrl == null) ? 0 : targetWebsiteUrl.hashCode());
        result = prime * result + ((updateDescriptions == null) ? 0 : updateDescriptions.hashCode());
        return result;
    }

    private static URL getUrl(JSONObject jsonObject, String key) throws UpdaterException {
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

    private static List<UpdateDescription> createUpdateDescritions(JSONObject jsonObject) {
        final List<UpdateDescription> result = new Vector<UpdateDescription>();
        final JSONObject udsObject = jsonObject.optJSONObject("descriptions");
        if (udsObject != null) {
            Iterator<?> languages = udsObject.keys();
            while (languages.hasNext()) {
                String languageCode = languages.next().toString();
                final JSONObject o = udsObject.optJSONObject(languageCode);
                if (o != null) {
                    UpdateDescription ud = new UpdateDescription(languageCode, o);
                    result.add(ud);
                } else {
                    final String s = udsObject.optString(languageCode, null);
                    if (s != null) {
                        UpdateDescription ud = new UpdateDescription(languageCode, s);
                        result.add(ud);
                    }
                }
            }
        }
        return result;
    }

    Update getUpdate(Properties properties, UpdateDisplayRecords records) throws UpdaterException {
        Date now = new Date();
        String languageCode = null;
        if (properties != null) {
            final String s = properties.getValue(Properties.KEY_DEVICE_LANGUAGE);
            if (!Utilities.isNullOrEmpty(s)) {
                languageCode = s;
            }
        }
        String packageName = this.targetPackageName;
        if (Utilities.isNullOrEmpty(packageName) && properties != null) {
            packageName = properties.getValue(Properties.KEY_APP_PACKAGE_NAME);
        }
        if (Utilities.isNullOrEmpty(packageName)) {
            throw new UpdaterException("'packageName' property should not be null or empty.");
        }
        if (!forceUpdate && !forceExit) {
            if (maxDisplayCount < Integer.MAX_VALUE) {
                final int count = records.getUpdateDisplayCount(id);
                if (count >= maxDisplayCount) {
                    return null;
                }
            }
            // check if it is displayed earlier than specified period
            if (displayPeriodInHours > 0) {
                final Date updateLastDisplayDate = records.getUpdateLastDisplayDate(id);
                // check if message displayed before
                if (updateLastDisplayDate != null) {
                    final Date date = Utilities.addHours(now, -displayPeriodInHours);
                    if (updateLastDisplayDate.after(date)) {
                        return null;
                    }
                }
            }
        }
        final UpdateDescription updateDescription = LocalizedStringMap.select(updateDescriptions, languageCode);
        records.onUpdateDisplayed(id, now);
        return new Update(updateDescription, targetPackageUrl, targetWebsiteUrl, targetGooglePlay, targetVersionCode, packageName, forceUpdate, forceExit);
    }

    private void validate() throws UpdaterException {
        if (forceExit) {
            return;
        }
        if (targetVersionCode < 0) {
            throw new UpdaterException("'targetVersionCode' shoud be a positive number. Current value: " + targetVersionCode);
        }
        if (targetPackageUrl == null && targetWebsiteUrl == null && !targetGooglePlay) {
            throw new UpdaterException("At least one of 'targetWebsiteUrl' and 'targetPackageUrl' shoud not be a null or 'targetGooglePlay' should be true.");
        }
    }

    boolean shouldDisplay(Properties properties, UpdateDisplayRecords records, Context context) {
        if (isMatches(properties)) {
            final Integer currentVersionCode = Utilities.tryParseInteger(properties.getValue(Properties.KEY_APP_VERSION_CODE));
            if (currentVersionCode != null && targetVersionCode > currentVersionCode.intValue()) {
                if (forceUpdate || forceExit) {
                    return true;
                } else {
                    boolean rtn = true;
                    // check if it is displayed more than specified count
                    if (maxDisplayCount < Integer.MAX_VALUE) {
                        final int count = records.getUpdateDisplayCount(id);
                        if (count >= maxDisplayCount) {
                            rtn = false;
                        }
                    }
                    // check if it is displayed earlier than specified period
                    if (displayPeriodInHours > 0) {
                        final Date updateLastDisplayDate = records.getUpdateLastDisplayDate(id);
                        // check if update displayed before
                        if (updateLastDisplayDate != null) {
                            Date now = new Date();
                            final Date date = Utilities.addHours(now, -displayPeriodInHours);
                            if (updateLastDisplayDate.after(date)) {
                                rtn = false;
                            }
                        }
                    }
                    return rtn;
                }
            }
        }
        return false;
    }
}