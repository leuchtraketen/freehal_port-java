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

import net.freehal.core.pos.Tags;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.freehal.core.util.RegexUtils;

public class TagContainerDisk implements TagContainer {

	@SuppressWarnings("unused")
	private String name;
	private Set<FreehalFile> files;

	public TagContainerDisk(String name) {
		this.name = name;
		files = new HashSet<FreehalFile>();
	}

	@Override
	public Iterator<Entry<String, Tags>> iterator() {
		return new HashMap<String, Tags>().entrySet().iterator();
	}

	@Override
	public void add(String word, Tags tags) {
		// ignore (we don't have a memory cache)
	}

	@Override
	public boolean containsKey(String word) {
		return get(word) != null;
	}

	@Override
	public Tags get(String word) {
		final String search = word + ":";
		for (FreehalFile filename : files) {
			Tags tags = null;
			Iterable<String> lines = Storages.inLanguageDirectory(filename).readLines();
			for (String line : lines) {
				line = RegexUtils.trimRight(line, "\\s");

				if (line.equals(search)) {
					tags = new Tags((Tags) null, null, null);

				} else if (tags != null) {
					if (line.startsWith(" ")) {
						line = RegexUtils.trim(line, ":,;\\s");
						if (line.startsWith("type")) {
							line = line.substring(4);
							line = RegexUtils.trimLeft(line, ":\\s");
							line = Tags.getUniqueCategory(line);
							tags = new Tags(tags, line, null, word);
						} else if (line.startsWith("genus")) {
							line = line.substring(5);
							line = RegexUtils.trimLeft(line, ":\\s");
							tags = new Tags(tags, null, line, word);
						}
					} else if (line.endsWith(":")) {
						return tags;
					}
				}
			}
			if (tags != null)
				return tags;
		}
		return null;
	}

	@Override
	public boolean add(FreehalFile filename) {
		files.add(filename);
		return true;
	}

}
