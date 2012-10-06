/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class for regular expression-based string operations.
 * 
 * @author "Tobias Schulz"
 */
public class RegexUtils {

	private RegexUtils() {}

	/**
	 * Search the given regular expression in the given text.
	 * 
	 * @param text
	 *        the text
	 * @param find
	 *        the regular expression
	 * @return {@code true} if the given text contains the given regular
	 *         expression, {@code false} otherwise
	 */
	public static boolean find(final String text, final String find) {
		Pattern pattern = Pattern.compile(find);
		Matcher m = pattern.matcher(text);
		return (m.find());
	}

	/**
	 * Like {@link #find(String, String)}, but case-insensitive.
	 */
	public static boolean ifind(final String text, final String find) {
		return find(text, "(?i)" + find);
	}

	/**
	 * Replaces each substring of the string in the first argument that matches
	 * the regular expression in the second argument with the given replacement.
	 * 
	 * @param text
	 *        the text
	 * @param find
	 *        the string to find
	 * @param replacement
	 *        the replacement
	 * @return the resulting string
	 */
	public static String replace(final String text, final String find, final String replacement) {
		return text.replaceAll(find, replacement);
	}

	/**
	 * Like {@link #replace(String, String, String)}, but case-insensitive.
	 */
	public static String ireplace(final String text, final String find, final String replacement) {
		return replace(text, "(?i)" + find, replacement);
	}

	/**
	 * Search the given regular expression in the given text and return all
	 * capturing groups.
	 * 
	 * @param text
	 *        the text
	 * @param find
	 *        the regular expression
	 * @return a list of capturing groups
	 */
	public static List<String> match(final String text, final String find) {
		Pattern pattern = Pattern.compile(find);
		Matcher m = pattern.matcher(text);
		List<String> list = null;
		if (m.find()) {
			list = new ArrayList<String>();
			for (int i = 1; i <= m.groupCount(); ++i) {
				list.add(m.group(i));
			}
		}
		return list;
	}

	/**
	 * Like {@link #match(String, String)}, but case-insensitive.
	 */
	public static List<String> imatch(final String str, final String string) {
		return match(str, "(?i)" + string);
	}

	/**
	 * Remove all characters in the second argument (the order doesn't matter)
	 * at the beginning and at the end of the first string.
	 * 
	 * @param str
	 *        the string to search the characters in
	 * @param chars
	 *        the characters to remove
	 * @return the resulting string
	 */
	public static String trim(final String str, final String chars) {
		return str.replaceAll("[" + chars + "]+$", "").replaceAll("^[" + chars + "]+", "");
	}

	/**
	 * Remove all characters in the second argument (the order doesn't matter)
	 * at the beginning of the first string.
	 * 
	 * @param str
	 *        the string to search the characters in
	 * @param chars
	 *        the characters to remove
	 * @return the resulting string
	 */
	public static String trimLeft(final String str, final String chars) {
		return str.replaceAll("^[" + chars + "]+", "");
	}

	/**
	 * Remove all characters in the second argument (the order doesn't matter)
	 * at the end of the first string.
	 * 
	 * @param str
	 *        the string to search the characters in
	 * @param chars
	 *        the characters to remove
	 * @return the resulting string
	 */
	public static String trimRight(final String str, final String chars) {
		return str.replaceAll("[" + chars + "]+$", "");
	}
}
