package tr.com.turkcellteknoloji.turkcellupdater;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;


class UpdateEntry extends FilteredEntry {
	final List<UpdateDescription> updateDescriptions;

	final int targetVersionCode;
	final String targetPackageName;

	final URL targetPackageUrl;
	final URL targetWebsiteUrl;
	final boolean targetGooglePlay;

	final boolean forceUpdate;
	final boolean forceExit;

	UpdateEntry(List<Filter> filters,
			List<UpdateDescription> updateDescriptions, int targetVersionCode,
			String targetPackageName, URL targetPackageUrl,
			URL targetWebsiteUrl, boolean targetGooglePlay, boolean forceUpdate, boolean forceExit)
			throws UpdaterException {
		super(filters);
		this.updateDescriptions = updateDescriptions;
		this.targetVersionCode = targetVersionCode;
		this.targetPackageName = targetPackageName;
		this.targetPackageUrl = targetPackageUrl;
		this.targetWebsiteUrl = targetWebsiteUrl;
		this.targetGooglePlay = targetGooglePlay;
		this.forceUpdate = forceUpdate;
		this.forceExit = forceExit;

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

		this.targetPackageName = Utilities.removeWhiteSpaces(jsonObject
				.optString("targetPackageName"));

		this.targetGooglePlay = jsonObject.optBoolean("targetGooglePlay");

		validate();
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

	private static List<UpdateDescription> createUpdateDescritions(
			JSONObject jsonObject) {
		final List<UpdateDescription> result = new Vector<UpdateDescription>();
		final JSONObject udsObject = jsonObject
				.optJSONObject("descriptions");

		if (udsObject != null) {
			Iterator<?> languages = udsObject.keys();
			while (languages.hasNext()) {
				String languageCode = languages.next().toString();
				final JSONObject o = udsObject.optJSONObject(languageCode);
				if (o != null) {
					UpdateDescription ud = new UpdateDescription(languageCode,
							o);
					result.add(ud);
				} else {
					final String s = udsObject.optString(languageCode, null);
					if (s != null) {
						UpdateDescription ud = new UpdateDescription(
								languageCode, s);
						result.add(ud);
					}
				}

			}
		}

		return result;
	}

	Update getUpdate(Properties properties) throws UpdaterException {
		String languageCode = null;
		if(properties!=null) {
			final String s = properties.getValue(Properties.KEY_DEVICE_LANGUAGE);
			if(!Utilities.isNullOrEmpty(s)) {
				languageCode = s;
			}

		}

		String packageName = this.targetPackageName;
		if(Utilities.isNullOrEmpty(packageName) && properties!=null) {
			packageName = properties.getValue(Properties.KEY_APP_PACKAGE_NAME);
		}
		if(Utilities.isNullOrEmpty(packageName)) {
			throw new UpdaterException("'packageName' property should not be null or empty.");
		}

		final UpdateDescription updateDescription = LocalizedStringMap.select(updateDescriptions, languageCode);
		return new Update(updateDescription,
				targetPackageUrl, targetWebsiteUrl, targetGooglePlay, targetVersionCode,
				packageName, forceUpdate, forceExit);
	}

	private void validate() throws UpdaterException {
		if(forceExit) {
			return;
		}


		if (targetVersionCode < 0) {
			throw new UpdaterException(
					"'targetVersionCode' shoud be a positive number. Current value: "
							+ targetVersionCode);
		}

		if (targetPackageUrl == null && targetWebsiteUrl == null && !targetGooglePlay) {
			throw new UpdaterException(
					"At least one of 'targetWebsiteUrl' and 'targetPackageUrl' shoud not be a null or 'targetGooglePlay' should be true.");
		}
	}

	boolean shouldDisplay(Properties properties) {
		if(isMatches(properties)) {

			final Integer currentVersionCode = Utilities.tryParseInteger(properties.getValue(Properties.KEY_APP_VERSION_CODE));
			if(currentVersionCode!=null && targetVersionCode != currentVersionCode.intValue()) {
				return true;
			}
			final String currentPackageName = properties.getValue(Properties.KEY_APP_PACKAGE_NAME);
			if(!Utilities.isNullOrEmpty(currentPackageName) && !Utilities.isNullOrEmpty(targetPackageName)) {
				return !currentPackageName.equals(targetPackageName);
			}
		}

		return false;
	}
}