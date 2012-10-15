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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.freehal.core.database.DirectoryUtils;
import net.freehal.core.pos.StandardTagger;
import net.freehal.core.pos.Tags;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.MultiHashMap;
import net.freehal.core.util.MultiMap;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;
import net.freehal.core.xml.Word;

public class DiskTagCachedReader extends MemoryTagContainer implements TagContainer {

	@SuppressWarnings("unused")
	private String name;

	private static final int maxCacheSize = 1000;
	private int currentCacheSize = 0;
	private MultiMap<String, Word> cache = null;
	private FreehalFile cacheForFile = null;

	public DiskTagCachedReader(String name) {
		this.name = name;
	}

	@Override
	public Iterator<Entry<String, Tags>> iterator() {
		return new HashMap<String, Tags>().entrySet().iterator();
	}

	@Override
	public void add(String word, Tags tags) {
		add(word, tags, FreehalFiles.getFile("unknown.pos"));
	}

	@Override
	public void add(String wordText, Tags tags, FreehalFile from) {
		final Word word = new Word(wordText, tags);

		if (cache == null) {
			final FreehalFile file = getCacheFile(wordText, from);
			StandardTagger.writeTagsTo(file, word);
		} else {
			cache.put(word.substring(0, 2), word);
			++currentCacheSize;
			if (currentCacheSize > maxCacheSize)
				writeCache();
		}
	}

	private FreehalFile getCacheDir(String word) {
		return DirectoryUtils.getCacheDirectory("tagger", "tags", new DirectoryUtils.Key(word, 2));
	}

	private FreehalFile getCacheFile(String word, FreehalFile from) {
		return getCacheDir(word).getChild(from.getName());
	}

	private FreehalFile getMetaFile(FreehalFile filename) {
		return DirectoryUtils.getCacheDirectory("tagger", "meta").getChild(filename.getName());
	}

	@Override
	public boolean add(FreehalFile filename) {
		final String savedSize = getMetaFile(filename).read();
		final String size = filename.length() + "";

		// if the file has changed
		if (savedSize == null || !savedSize.equals(size)) {
			// delete old cache files
			deleteIn(DirectoryUtils.getCacheDirectory("tagger", "tags"), filename.getName());

			// for logging
			// LogUtils.startProgress(0, 1, 2);

			// create cache in memory
			cache = new MultiHashMap<String, Word>();
			cacheForFile = filename;

			// temporarily filter annoying log messages
			LogUtils.addTemporaryFilter("StandardTagger", "i");

			// read the file (calls #add(String, Tags, FreehalFile))
			boolean result = super.add(filename);

			// for logging
			// LogUtils.updateProgress();

			// write the cache
			if (cache.size() > 0)
				writeCache();
			cache = null;
			cacheForFile = null;

			// reset temporary log filters
			LogUtils.resetTemporaryFilters();

			// write the file size
			getMetaFile(filename).write(size);

			// for logging
			// LogUtils.stopProgress();

			return result;

		} else
			return false;
	}

	private void writeCache() {
		for (String keyString : cache.keySet()) {
			StandardTagger.writeTagsTo(this.getCacheFile(keyString, cacheForFile), cache.get(keyString));
		}
		cache.clear();
		currentCacheSize = 0;
	}

	private void deleteIn(FreehalFile directory, String nameToDelete) {
		FreehalFile[] children = directory.listFiles();
		for (FreehalFile child : children) {
			if (child.isFile() && child.getName().equals(nameToDelete))
				child.delete();
			else if (child.isDirectory())
				deleteIn(child, nameToDelete);
		}
	}

	@Override
	public boolean containsKey(String word) {
		return get(word) != null;
	}

	@Override
	public Tags get(String word) {
		FreehalFile[] files = getCacheDir(word).listFiles();

		final String search = word + "|";
		for (FreehalFile filename : files) {
			Iterable<String> lines = Storages.inLanguageDirectory(filename).readLines();
			for (String line : lines) {
				line = RegexUtils.trimRight(line, "\\s");

				if (line.startsWith(search)) {
					String[] parts = StringUtils.splitEscaped(line, "|");

					if (parts.length == 3)
						return new Tags((Tags) null, parts[1], parts[2]);
					else if (parts.length == 2)
						return new Tags((Tags) null, parts[1], null);
				}
			}
		}
		return null;
	}

	public static Factory<TagContainer, String> newFactory() {
		return new Factory<TagContainer, String>() {
			@Override
			public TagContainer newInstance(String b) {
				return new DiskTagCachedReader(b);
			}
		};
	}
}
