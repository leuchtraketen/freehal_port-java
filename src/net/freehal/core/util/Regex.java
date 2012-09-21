package net.freehal.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
	public static boolean find(String text, String find) {
		Pattern pattern = Pattern.compile(find);
		Matcher m = pattern.matcher(text);
		return (m.find());
	}

	public static List<String> match(String text, String find) {
		Pattern pattern = Pattern.compile(find);
		Matcher m = pattern.matcher(text);
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= m.groupCount(); ++i) {
			list.add(m.group(i));
		}
		return list;
	}
}
