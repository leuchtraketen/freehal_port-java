/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.pos.storage;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.freehal.core.pos.Tags;

public class TagListMemory extends TagContainerMemory {

	private List<Map.Entry<String, Tags>> list = new ArrayList<Map.Entry<String, Tags>>();

	@Override
	public void add(String word, Tags tags) {
		list.add(new AbstractMap.SimpleEntry<String, Tags>(word, tags));
	}

	@Override
	public boolean containsKey(String word) {
		for (Map.Entry<String, Tags> entry : list) {
			if (entry.getKey().equals(word))
				return true;
		}
		return false;
	}

	@Override
	public Tags get(String word) {
		for (Map.Entry<String, Tags> entry : list) {
			if (entry.getKey().equals(word))
				return entry.getValue();
		}
		return null;
	}

	@Override
	public Iterator<Entry<String, Tags>> iterator() {
		return list.iterator();
	}
}
