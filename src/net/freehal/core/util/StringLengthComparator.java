package net.freehal.core.util;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<String> {

	/**
	 * from big length to small length
	 */
	public int compare(String s1, String s2) {
		int i = s2.length() - s1.length();
		return i;
	}

}