package net.freehal.core.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArrayUtils<A> {

	public static <A> List<A> concatMatrix(List<List<A>> _lists, Concatenator<A> concatenator) {
		String out = "[";
		for (List<A> list : _lists) {
			out += "[";
			for (A a : list) {
				out += "'" + a + "',";
			}
			out += "],";
		}
		out += "]";
		LogUtils.e("input: " + out);

		LinkedList<List<A>> lists = new LinkedList<List<A>>();
		lists.addAll(_lists);

		List<A> finalList = new ArrayList<A>();
		while (lists.size() > 0) {
			finalList = concatLists(finalList, lists.removeFirst(), concatenator);
		}

		out = "[";
		for (A a : finalList) {
			out += "'" + a + "',";
		}
		out += "]";
		LogUtils.e("output: " + out);

		return finalList;
	}

	private static <A> List<A> concatLists(List<A> list1, List<A> list2, Concatenator<A> concatenator) {
		if (list1.size() > 0 && list2.size() > 0) {
			List<A> list = new ArrayList<A>();
			for (A e1 : list1) {
				for (A e2 : list2) {
					list.add(concatenator.concat(e1, e2));
				}
			}

			return list;
		} else if (list1.size() > 0)
			return list1;
		else
			return list2;
	}

	public static interface Concatenator<A> {
		public A concat(A a, A b);
	}

	public static <A> List<A> partOfList(List<A> list, int i, int j) {
		if (i <= 0)
			i = 0;
		if (j <= 0)
			j = list.size();

		List<A> newList = new ArrayList<A>();
		for (; i < j; ++i) {
			newList.add(list.get(i));
		}
		return newList;
	}
}
