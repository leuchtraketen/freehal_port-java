package net.freehal.core.pos;

import java.io.File;
import java.util.Map;

public interface TagContainer extends Iterable<Map.Entry<String, Tags>> {
	public void add(String word, Tags tags);

	public boolean containsKey(String word);

	public Tags get(String word);

	public void add(File filename);
}
