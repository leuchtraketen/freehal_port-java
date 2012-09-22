package net.freehal.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Ranking<O> {
	private MultiMap<Double, O> map = new MultiMap<Double, O>();
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
