package net.freehal.core.typedefs;

import java.util.AbstractMap;
import java.util.ArrayList;
import net.freehal.core.pos.Tags;
import java.util.Map;

public class TagList extends ArrayList<Map.Entry<String, Tags>> implements
		TagContainer {

	private static final long serialVersionUID = 7111858739911455030L;

	@Override
	public void add(String word, Tags tags) {
		super.add(new AbstractMap.SimpleEntry<String, Tags>(word, tags));
	}

	@Override
	public boolean containsKey(String word) {
		for (Map.Entry<String, Tags> entry : this) {
			if (entry.getKey().equals(word))
				return true;
		}
		return false;
	}

	@Override
	public Tags get(String word) {
		for (Map.Entry<String, Tags> entry : this) {
			if (entry.getKey().equals(word))
				return entry.getValue();
		}
		return null;
	}

}
