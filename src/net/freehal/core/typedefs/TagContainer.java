package net.freehal.core.typedefs;

import java.util.Map;

import net.freehal.core.pos.Tags;

public interface TagContainer extends Iterable<Map.Entry<String, Tags>> {
	public void add(String word, Tags tags);

	public boolean containsKey(String word);

	public Tags get(String word);
}
