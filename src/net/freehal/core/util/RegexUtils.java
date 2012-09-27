/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
	public static boolean find(final String text, final String find) {
		Pattern pattern = Pattern.compile(find);
		Matcher m = pattern.matcher(text);
		return (m.find());
	}

	public static boolean ifind(final String text, final String find) {
		return find(text, "(?i)" + find);
	}

	@SuppressWarnings("unused")
	public static String replace(final String text, final String find,
			final String replacement) {
		
		if (0 == 1) {
			String result = text.replaceAll(find, replacement);
			if (!text.equals(result) && result.length() < 1024) {
				StackTraceElement ste = Thread.currentThread().getStackTrace()[2];

				LogUtils.d(new StringBuilder().append("replace(\"")
						.append(text).append("\", \"").append(find)
						.append("\", \"").append(replacement).append("\")=\"")
						.append(result).append("\" in ")
						.append(ste.getClassName()).append(".")
						.append(ste.getMethodName()).append(":")
						.append(ste.getLineNumber()).toString());
			}
			return result;
		}
		
		return text.replaceAll(find, replacement);
	}

	public static String ireplace(final String str, final String string,
			final String string2) {
		return replace(str, "(?i)" + string, string2);
	}

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

	public static List<String> imatch(final String str, final String string) {
		return match(str, "(?i)" + string);
	}

	public static String trim(final String str, final String chars) {
		return str.replaceAll("[" + chars + "]+$", "").replaceAll(
				"^[" + chars + "]+", "");
	}

	public static String trimLeft(final String str, final String chars) {
		return str.replaceAll("^[" + chars + "]+", "");
	}

	public static String trimRight(final String str, final String chars) {
		return str.replaceAll("[" + chars + "]+$", "");
	}
}
