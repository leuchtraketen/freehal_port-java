package net.freehal.core.typedefs;

import java.util.HashMap;
import java.util.Iterator;

import net.freehal.core.pos.Tags;

public class TagMap extends HashMap<String, Tags> implements TagContainer {

	private static final long serialVersionUID = 1519345148659396083L;

	@Override
	public void add(String word, Tags tags) {
		put(word, tags);
	}

	@Override
	public boolean containsKey(String word) {
		return super.containsKey(word);
	}

	@Override
	public Iterator<java.util.Map.Entry<String, Tags>> iterator() {
		return super.entrySet().iterator();
	}

	@Override
	public Tags get(String word) {
		return super.get(word);
	}

}
