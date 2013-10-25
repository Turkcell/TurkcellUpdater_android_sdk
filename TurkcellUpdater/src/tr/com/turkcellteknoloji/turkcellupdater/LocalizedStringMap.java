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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;


/**
 * Provides base class for language specific set of Strings. Every String value has its own key.
 * @author Ugur Ozmen
 *
 */
abstract class LocalizedStringMap implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Two letter language code defining language of contents. <code>null</code> means no language is specified.
	 */
	public final String languageCode;
	final Hashtable<String,String> map ;

	LocalizedStringMap(String languageCode, Map<String, String> map) {
		super();
		this.languageCode = formatLanguageCode(languageCode);

		this.map = new Hashtable<String, String>(map);
	}

	LocalizedStringMap(String languageCode, JSONObject jsonObject) {
		super();
		this.languageCode = formatLanguageCode(languageCode);
		this.map = new Hashtable<String, String>();
		if(jsonObject!=null) {
			@SuppressWarnings("unchecked")
			final Iterator<String> keys = jsonObject.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				if(key!=null) {
					String value = jsonObject.optString(key, null);
					map.put(key, value);
				}
			}
		}

	}

	public String get(String key) {
		if(key==null) {
			return null;
		}
		return map.get(key);
	}

	public Set<String> getKeys() {
		return new HashSet<String>(map.keySet());
	}

	private static String formatLanguageCode(String languageCode) {
		languageCode = Utilities.normalize(languageCode);
		if (languageCode == null || languageCode.length() < 1
				|| languageCode.equals("*")) {
			return null;
		}
		return languageCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((languageCode == null) ? 0 : languageCode.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalizedStringMap other = (LocalizedStringMap) obj;
		if (languageCode == null) {
			if (other.languageCode != null)
				return false;
		} else if (!languageCode.equals(other.languageCode))
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

	static <T extends LocalizedStringMap> T select(List<T> list, String languageCode) {
		final String normalizedLanguageCode = Utilities.normalize(languageCode);
		T result = null;
		for (T t : list) {
			if (t != null) {
				final String normalizedLanguageCode2 = Utilities
						.normalize(t.languageCode);

				if (normalizedLanguageCode2.equals(normalizedLanguageCode)) {
					result = t;
					break;
				}

				if (normalizedLanguageCode2.equals("*")
						|| normalizedLanguageCode2.equals("")
						|| normalizedLanguageCode2
								.equals(normalizedLanguageCode)) {
					result = t;
				}
			}
		}

		return result;
	}

	@Override
	public String toString() {

		String mapAsString;

		if(map == null) {
			mapAsString = null;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			boolean first = true;
			for (String key:map.keySet()) {
				if(first) {
					first = false;
				} else {
					sb.append(',');
				}
				sb.append(key);
				sb.append("=");
				sb.append(map.get(key));
			}
			sb.append(']');
			mapAsString = sb.toString();
		}

		return "LocalizedStringMap [languageCode=" + languageCode + ", map="
				+ mapAsString + "]";
	}



}
