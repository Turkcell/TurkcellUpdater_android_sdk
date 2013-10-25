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

/**
 * Static runtime configuration for this library.
 * @author Ugur Ozmen
 */
class Configuration {

	public final static String VERSION_NAME = "1.0";
	public final static String PRODUCT_NAME = "TurkcellUpdater";

	/**
	 * Library compliance level. This value should be increased after non-backward-compatible changes are introduced to this library.
	 */
	final static int UPDATER_LEVEL = 3;

	/**
	 * Setting this value <code>true</code> activates debug behavior. This value always should be <code>false</code> in production versions.
	 */
	final static boolean DEBUG = BuildConfig.DEBUG;

	/**
	 * Setting this value to <code>null</code> disables MIME type checking for JSON files.
	 */
	final static String EXPECTED_JSON_MIME_TYPE = DEBUG ? null : "application/json";
}
