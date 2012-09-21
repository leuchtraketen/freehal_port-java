package net.freehal.core.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiMap<A, B> extends HashMap<A, Set<B>> {
	private static final long serialVersionUID = -1345542044852818287L;

	private Set<Map.Entry<A, B>> cacheEntrySet;

	public B multiPut(A a, B b) {
		// clear entry set cache
		cacheEntrySet = null;

		if (!containsKey(a)) {
			put(a, new HashSet<B>());
		}
		Set<B> values = get(a);
		values.add(b);
		put(a, values);
		return b;
	}

	public Set<B> put(A a, Set<B> bs) {
		// clear entry set cache
		cacheEntrySet = null;

		return super.put(a, bs);
	}

	public Set<java.util.Map.Entry<A, B>> multiEntrySet() {
		if (cacheEntrySet == null) {
			cacheEntrySet = new HashSet<Map.Entry<A, B>>();
			Set<Map.Entry<A, Set<B>>> set1 = entrySet();
			for (Map.Entry<A, Set<B>> entry : set1) {
				for (B b : entry.getValue()) {
					cacheEntrySet.add(new AbstractMap.SimpleEntry<A, B>(entry
							.getKey(), b));
				}
			}
		}
		return cacheEntrySet;
	}

	public int count(A a) {
		if (this.containsKey(a)) {
			return this.get(a).size();
		} else {
			return 0;
		}
	}
}
