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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.freehal.core.util.ArrayUtils.Concatenator;

/**
 * An utility class for non-regex string operations.
 * 
 * @author "Tobias Schulz"
 */
public class StringUtils {

	private StringUtils() {}

	/**
	 * Replaces each substring of the string in the first argument that matches
	 * the second argument with the given replacement without using regular
	 * expressions.
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
		// return text.replace(Pattern.quote(find), replacement);
		return text.replace(find, replacement);
	}

	/**
	 * Join all string in the given {@link java.lang.Iterable} using the given
	 * delimiter.
	 * 
	 * @param delimiter
	 *        the delimiter
	 * @param s
	 *        the string to join
	 * @return the resultng string
	 */
	public static String join(final String delimiter, final Iterable<String> s) {
		if (s == null)
			return "";
		Iterator<String> iter = s.iterator();
		StringBuilder builder = new StringBuilder();
		builder.append(iter.next().toString());
		while (iter.hasNext()) {
			builder.append(delimiter).append(iter.next().toString());
		}
		return builder.toString();
	}

	/**
	 * Join all string in the given string array using the given delimiter.
	 * 
	 * @param delimiter
	 *        the delimiter
	 * @param s
	 *        the string to join
	 * @return the resultng string
	 */
	public static String join(String delimiter, String[] s) {
		if (s == null || s.length == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(s[0].toString());
		for (int i = 1; i < s.length; ++i) {
			builder.append(delimiter).append(s[i].toString());
		}
		return builder.toString();
	}

	/**
	 * Split the given string by the given delimiter with escaping it.
	 * 
	 * @param text
	 *        the string to split
	 * @param splitBy
	 *        the delimiter
	 * @return the parts after splitting
	 */
	public static String[] splitEscaped(String text, String splitBy) {
		String[] parts = text.split("(?<!\\\\)" + Pattern.quote(splitBy));
		for (String part : parts)
			part = part.replace(Pattern.quote("\\" + splitBy), "|");
		return parts;
	}

	/**
	 * Trim the given string.
	 * 
	 * @param str
	 *        the string to trim
	 * @return the resulting string
	 */
	public static String trim(String str) {
		return str.trim();
	}

	/**
	 * Replace the first character of the given string by it's upper case
	 * equivalent, like Perl's {@code ucfirst}.
	 * 
	 * @param string
	 *        the given string
	 * @return the resulting string
	 */
	public static String ucfirst(String string) {
		if (string.length() >= 2)
			return string.substring(0, 1).toUpperCase() + string.substring(1);
		else if (string.length() == 1)
			return string.substring(0, 1).toUpperCase();
		else
			return string;
	}

	public static String removeSubstring(String text, List<String> substrings) {
		for (String substring : substrings) {
			text = removeSubstring(text, substring);
		}
		return text;
	}

	public static String removeSubstring(String text, String substring) {
		return text.replaceFirst(Pattern.quote(substring), "");
	}

	/**
	 * Delete all non-ASCII characters in the given string.
	 * 
	 * @param string
	 *        the string
	 * @return the resulting string
	 */
	public static String toAscii(String string) {
		// TODO Automatisch generierter Methodenstub
		return string;
	}

	/**
	 * Use a {@link java.io.PrintWriter} and
	 * {@link Exception#printStackTrace(PrintWriter)} to get a string
	 * representation of the given regular expression.
	 * 
	 * @param ex
	 *        the regular expression
	 * @return it's string representation as it would have been printed to a
	 *         console
	 */
	public static String asString(Exception ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	public static class StringConcatenator implements Concatenator<String> {
		private String delimiter;

		public StringConcatenator(String delimiter) {
			this.delimiter = delimiter;
		}

		@Override
		public String concat(String a, String b) {
			return a + delimiter + b;
		}
	}
}
