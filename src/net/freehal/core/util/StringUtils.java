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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.regex.Pattern;

public class StringUtils {

	public static String replace(final String text, final String find,
			final String replacement) {
		return text.replace(Pattern.quote(find), replacement);
	}

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

	public static String trim(String str) {
		return str.trim();
	}

	public static String ucfirst(String string) {
		return string.substring(0, 1).toUpperCase()+string.substring(1);
	}

	public static String toAscii(String wordString) {
		// TODO Automatisch generierter Methodenstub
		return wordString;
	}
	
	public static String asString(Exception ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
