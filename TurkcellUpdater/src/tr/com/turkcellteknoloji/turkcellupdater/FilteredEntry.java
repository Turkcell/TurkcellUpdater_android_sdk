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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

/**
 * Base class for information that can be filtered by a list of {@link Filter}s
 * @author Ugur Ozmen
 */
class FilteredEntry {

	final List<Filter> filters;

	FilteredEntry(List<Filter> filters)
			throws UpdaterException {
		super();
		this.filters = filters;
	}

	FilteredEntry(JSONObject jsonObject) throws UpdaterException {
		this.filters = createFilters(jsonObject);
	}

	private static List<Filter> createFilters(JSONObject jsonObject) {
		final List<Filter> result = new Vector<Filter>();
		final JSONObject filtersObject = jsonObject.optJSONObject("filters");

		if (filtersObject != null) {
			Iterator<?> names = filtersObject.keys();
			while (names.hasNext()) {
				String name = names.next().toString();
				String rule = filtersObject.optString(name);
				Filter filter = new Filter(name, rule);
				result.add(filter);
			}
		}

		return result;
	}

	boolean isMatches(Properties properties) {
		if (filters != null) {

			for (Filter filter : filters) {
				if (filter != null) {
					if(properties==null) {
						return false;
					}

					final String value = properties.getValue(filter.name);

					if (!filter.isMatchesWith(value)) {
						return false;
					}
				}
			}
		}

		return true;
	}

}
