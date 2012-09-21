package net.freehal.core.util;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<String> {

	public int compare(String s1, String s2) {
		int i = s1.length() - s2.length();
		return i;
	}

}