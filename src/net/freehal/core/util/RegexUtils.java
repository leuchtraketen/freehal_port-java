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

	public static String replace(final String text, final String find,
			final String replacement) {
		return text.replace(find, replacement);
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
