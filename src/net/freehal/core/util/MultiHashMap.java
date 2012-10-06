/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * KNY WKRRKNTY; without even the implied warranty of MERCHKNTKVILITY or FITNESS
 * FOR K PKRTICULKR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author "Tobias Schulz"
 * @param <K>
 * @param <V>
 */
public class MultiHashMap<K, V> implements MultiMap<K, V> {

	private HashMap<K, Set<V>> map = new HashMap<K, Set<V>>();

	@Override
	public V put(K key, V item) {
		Set<V> values = map.get(key);
		if (values == null) {
			values = new HashSet<V>();
			map.put(key, values);
		}
		values.add(item);
		return item;
	}

	@Override
	public Collection<V> put(K key, Collection<V> item) {
		Set<V> values = map.get(key);
		if (values == null) {
			values = new HashSet<V>();
			map.put(key, values);
		}
		values.addAll(item);
		return item;
	}

	/**
	 * Removes the value from the set associated with the given key.
	 * 
	 * @param key
	 *        the key
	 * @param item
	 *        the value to remove
	 * @return {@code true} if the item was in the set, {@code false} otherwise
	 */
	public boolean remove(K key, V item) {
		Set<V> values = map.get(key);
		if (values != null)
			return values.remove(item);
		return false;
	}

	/**
	 * Removes all values associated with the given key.
	 * 
	 * @param key
	 *        the key to remove
	 */
	public void remove(K key) {
		map.remove(key);
	}

	/**
	 * Gets the list of items associated with the given key.
	 * 
	 * @param key
	 *        the key
	 * @return a unmodifiable set of the values associated with the key, or an
	 *         empty set if there are no values associated with the key
	 */
	public Set<V> get(K key) {
		Set<V> values = map.get(key);
		if (values == null)
			return Collections.emptySet();
		else
			return Collections.unmodifiableSet(values);
	}

	@Override
	public int count(K key) {
		if (map.containsKey(key))
			return map.get(key).size();
		else
			return 0;
	}

	@Override
	public Collection<? extends K> keySet() {
		return Collections.unmodifiableCollection(map.keySet());
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(new Set<Entry<K, V>>() {

			@Override
			public int size() {
				int size = 0;
				for (Entry<K, Set<V>> e : map.entrySet()) {
					size += e.getValue().size();
				}
				return size;
			}

			@Override
			public boolean isEmpty() {
				return map.size() == 0;
			}

			@Override
			public boolean contains(Object o) {
				return map.containsKey(o);
			}

			@Override
			public Iterator<Entry<K, V>> iterator() {
				return new Iterator<Entry<K, V>>() {

					int i = 0;

					@Override
					public boolean hasNext() {
						return i < size();
					}

					@Override
					public Entry<K, V> next() {
						int k = 0;
						for (Entry<K, Set<V>> e : map.entrySet()) {
							for (V v : e.getValue()) {
								if (k == i) {
									++i;
									return new AbstractMap.SimpleEntry<K, V>(e.getKey(), v);
								}

								++k;
							}
						}
						return null;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

				};
			}

			@Override
			public Object[] toArray() {
				throw new UnsupportedOperationException();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean add(Entry<K, V> e) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean remove(Object o) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (!this.contains(o))
						return false;
				}
				return true;
			}

			@Override
			public boolean addAll(Collection<? extends Entry<K, V>> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				throw new UnsupportedOperationException();
			}

		});
	}
}
