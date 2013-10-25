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
 * Checks if given value matches with filtering rule.<br>
 * <li>Rules are sequences of rule parts joined with ","</li>
 * <li>Both rule parts and values are converted to lower case and trimmed before
 * comparison</li>
 * <li>Order of rule parts doesn't change the result, example: "!b,a" is same with "a,!b"</li>
 * <li><code>"*"</code>, <code>null</code> or empty string matches with any value including
 * <code>null</code></li>
 * <li><code>"''"</code> matches with <code>null</code> or empty string</li>
 * <li><code>"!''"</code> matches with any value except <code>null</code> or empty string</li>
 * <li><code>"![rule part]"</code> excludes any value matches with [rule].</li>
 * <li><code>"[value]"</code> matches with any value equals to [value]</li>
 * <li><code>"[prefix]*"</code> matches with any value starting with [prefix]</li>
 * <li><code>"*[suffix]"</code> matches with any value ending with [suffix]</li>
 * <li><code>"[prefix]*[suffix]"</code> matches with any value starting with [prefix] and
 * ending with [suffix]</li>
 * <li><code>"&gt;[integer]"</code> matches with any value greater than [integer]</li>
 * <li><code>"&gt;=[integer]"</code> matches with any value greater than or equals to [integer]</li>
 * <li><code>"&lt;[integer]"</code> matches with any value lesser than [integer]</li>
 * <li><code>"&lt;=[integer]"</code> matches with any value lesser than or equals to [integer]</li>
 * <li><code>"&lt;&gt;[integer]"</code> matches with any value not equals to [integer]</li>
 *
 * @author Ugur Ozmen
 *
 */
class Filter {
	final String name;
	final String rule;

	Filter(String name, String rule) {
		this.name = name;
		this.rule = rule;
	}

	boolean isMatchesWith(String value) {
		return isMatchesWith(value, rule);
	}

	private static boolean isMatchesWith(String value, final String rule) {
		value = Utilities.normalize(value);
		if (rule == null) {
			return true;
		}

		// should match with any value excluding filtered ones if rule has no include filters
		// example: "a" should match with "!b,!c" rule
		boolean onlyExcludeFiltersFound = true;

		// since exclude filters has higher priority over include filters,
		// we should not immediately return true when value matches with an include filter.
		// example: "abc" should not match with "a*c,!*b*" rule
		boolean matchedWithAnIncludeFilter = false;

		String[] ruleParts = rule.split(",");
		for (int i = 0; i < ruleParts.length; i++) {
			final String part = Utilities.normalize(ruleParts[i]);
			if (part.length()<1) {
				// omit empty rules
				continue;
			}

			if(part.startsWith("!")) {
				// Exclude rule
				if(part.length()>1) {
					// omit empty rules
					if(isFilterPartMatches(part.substring(1), value)) {
						return false;
					}
				}
			} else {
				// Include rule
				onlyExcludeFiltersFound = false;
				if(!matchedWithAnIncludeFilter) {
					if (isFilterPartMatches(part, value)) {
						matchedWithAnIncludeFilter = true;
					}
				}
			}

		}

		return onlyExcludeFiltersFound || matchedWithAnIncludeFilter;
	}

	private static boolean isFilterPartMatches(String rulePart, String value) {
		if (rulePart.equals("''")) {
			return value.equals("");
		}
		if (rulePart.startsWith("<>")) {
			Integer valueAsInteger = Utilities.tryParseInteger(value);
			Integer ref = Utilities.tryParseInteger(rulePart.substring(2).trim());
			if(valueAsInteger == null || ref == null) {
				return false;
			}
			return valueAsInteger.intValue() != ref.intValue();
		}
		if (rulePart.startsWith("<=")) {
			Integer valueAsInteger = Utilities.tryParseInteger(value);
			Integer ref = Utilities.tryParseInteger(rulePart.substring(2).trim());
			if(valueAsInteger == null || ref == null) {
				return false;
			}
			return valueAsInteger <= ref;
		}
		if (rulePart.startsWith(">=")) {
			Integer valueAsInteger = Utilities.tryParseInteger(value);
			Integer ref = Utilities.tryParseInteger(rulePart.substring(2).trim());
			if(valueAsInteger == null || ref == null) {
				return false;
			}
			return valueAsInteger >= ref;
		}
		if (rulePart.startsWith("<")) {
			Integer valueAsInteger = Utilities.tryParseInteger(value);
			Integer ref = Utilities.tryParseInteger(rulePart.substring(1).trim());
			if(valueAsInteger == null || ref == null) {
				return false;
			}
			return valueAsInteger < ref;
		}
		if (rulePart.startsWith(">")) {
			Integer valueAsInteger = Utilities.tryParseInteger(value);
			Integer ref = Utilities.tryParseInteger(rulePart.substring(1).trim());
			if(valueAsInteger == null || ref == null) {
				return false;
			}
			return valueAsInteger > ref;
		}

		if (rulePart.indexOf("*") > -1) {
			final String regex = rulePart.replace("?", ".").replace("*", ".*");
			return value.matches(regex);
		}

		return rulePart.equals(value);
	}




}