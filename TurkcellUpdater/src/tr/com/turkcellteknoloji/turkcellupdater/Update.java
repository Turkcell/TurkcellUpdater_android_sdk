package tr.com.turkcellteknoloji.turkcellupdater;

import java.io.Serializable;
import java.net.URL;

/**
 * Provides information about an deployable application package
 * @author Ugur Ozmen
 *
 */
public class Update implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * User representable information about version.
	 */
	public final UpdateDescription description;

	/**
	 * URL of .apk file. <code>null</code> if undefined.
	 */
	public final URL targetPackageUrl;

	/**
	 * URL of web page to display user to download this version. <code>null</code> if undefined.
	 */
	public final URL targetWebsiteUrl;

	/**
	 * <code>true</code> if update should be performed through Google Play product page.
	 */
	public final boolean targetGooglePlay;


	/**
	 * Version code of referred package.
	 */
	public final int targetVersionCode;

	/**
	 * Package name of referred package
	 */
	public final String targetPackageName;

	/**
	 * <code>true</code> if current application should not resume without installing this package
	 */
	public final boolean forceUpdate;

	/**
	 * Exits application after displaying message.
	 */
	public final boolean forceExit;


	Update(UpdateDescription description, URL targetPackageUrl,
			URL targetWebsiteUrl, boolean targetGooglePlay, int targetVersionCode,
			String targetPackageName, boolean forceUpdate,
			boolean forceExit) {
		super();
		this.description = description;
		this.targetGooglePlay = targetGooglePlay;
		this.targetPackageUrl = targetPackageUrl;
		this.targetWebsiteUrl = targetWebsiteUrl;
		this.targetVersionCode = targetVersionCode;
		this.targetPackageName = targetPackageName;
		this.forceUpdate = forceUpdate;
		this.forceExit = forceExit;
	}


	@Override
	public String toString() {
		return "Update [description=" + description + ", targetPackageUrl="
				+ targetPackageUrl + ", targetWebsiteUrl=" + targetWebsiteUrl
				+ ", targetGooglePlay=" + targetGooglePlay
				+ ", targetVersionCode=" + targetVersionCode
				+ ", targetPackageName=" + targetPackageName + ", forceUpdate="
				+ forceUpdate + ", forceExit=" + forceExit + "]";
	}


}
