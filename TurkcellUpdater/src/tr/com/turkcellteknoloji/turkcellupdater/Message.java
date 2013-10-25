package tr.com.turkcellteknoloji.turkcellupdater;

import java.io.Serializable;
import java.net.URL;

/**
 * Provides information about an deployable application package
 * @author Ugur Ozmen
 *
 */
public class Message implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * User representable message contents.
	 */
	public final MessageDescription description;

	/**
	 * URL of web page to display user. <code>null</code> if undefined.
	 */
	public final URL targetWebsiteUrl;

	/**
	 * <code>true</code> if user should directed to Google Play product page.
	 */
	public final boolean targetGooglePlay;


	/**
	 * Package name of referred package
	 */
	public final String targetPackageName;

	Message(MessageDescription description,
			URL targetWebsiteUrl, boolean targetGooglePlay,
			String targetPackageName) {
		super();
		this.description = description;
		this.targetGooglePlay = targetGooglePlay;
		this.targetWebsiteUrl = targetWebsiteUrl;
		this.targetPackageName = targetPackageName;
	}

	@Override
	public String toString() {
		return "Message [description=" + description + ", targetWebsiteUrl="
				+ targetWebsiteUrl + ", targetGooglePlay=" + targetGooglePlay
				+ ", targetPackageName=" + targetPackageName + "]";
	}





}
