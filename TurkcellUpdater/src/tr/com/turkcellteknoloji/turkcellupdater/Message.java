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
