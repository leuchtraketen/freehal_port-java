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
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.freehal.core.util.RegexUtils;

public class DiskTagReader implements TagContainer {

	@SuppressWarnings("unused")
	private String name;
	private Set<FreehalFile> files;

	public DiskTagReader(String name) {
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
	public boolean add(FreehalFile filename) {
		files.add(filename);
		return true;
	}

	@Override
	public boolean containsKey(String word) {
		return get(word) != null;
	}

	@Override
	public Tags get(String word) {
		LogUtils.startProgress("search-in-pos");
		LogUtils.updateProgress("read part of speech file");

		final String search = word + "|";
		for (FreehalFile filename : files) {
			Iterable<String> lines = Storages.inLanguageDirectory(filename).readLines();
			for (String line : lines) {
				line = RegexUtils.trimRight(line, "\\s");

				LogUtils.updateProgress();

				if (line.startsWith(search)) {
					String[] parts = StringUtils.splitEscaped(line, "|");
					if (parts.length == 3) {
						LogUtils.stopProgress();
						return new Tags((Tags) null, parts[1], parts[2]);
					} else if (parts.length == 2) {
						LogUtils.stopProgress();
						return new Tags((Tags) null, parts[1], null);
					}
				}
			}
		}
		LogUtils.stopProgress();
		return null;
	}

	public static Factory<TagContainer> newFactory() {
		return new Factory<TagContainer>() {
			@Override
			public TagContainer newInstance(String... params) {
				if (params.length > 0)
					return new DiskTagReader(params[0]);
				else
					throw new IllegalArgumentException("no parameters given");
			}
		};
	}
}
