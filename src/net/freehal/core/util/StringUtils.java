package net.freehal.core.util;

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
}
