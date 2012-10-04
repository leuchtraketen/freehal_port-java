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
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Ranking<O> {
	private MultiMap<Double, O> map = new MultiHashMap<Double, O>();
	private List<Double> keys;

	public Ranking() {

	}

	private void buildKeys() {
		if (keys == null) {
			keys = new ArrayList<Double>(map.keySet());
			Collections.sort(keys);
		}
	}

	public void insert(O obj, Double ranking) {
		if (ranking > 0) {
			map.multiPut(ranking, obj);
			keys = null;
		}
	}

	public Set<O> get(int index) {
		buildKeys();
		return map.get(keys.get(index));
	}

	public Double rank(int index) {
		buildKeys();
		return keys.get(index);
	}

	public int size() {
		return map.size();
	}

	public List<O> getBest() {
		buildKeys();
		List<O> best = new ArrayList<O>(map.get(keys.get(keys.size() - 1)));
		Collections.shuffle(best);
		return best;
	}

	public O getBestOne() {
		return size() > 0 ? getBest().get(0) : null;
	}

}
