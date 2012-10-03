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
package net.freehal.core.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.freehal.core.cache.DiskStorage;
import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlObj;
import net.freehal.core.xml.XmlText;
import net.freehal.core.xml.XmlUtils;
import net.freehal.core.xml.XmlUtils.XmlStreamIterator;

public class DiskDatabase implements DatabaseImpl {

	private Map<String, Integer> cacheFiles = new HashMap<String, Integer>();
	private Mutable<SynonymMap> synonymmap = new Mutable<SynonymMap>(new SynonymMap());

	public DiskDatabase() {
		readMetadata();
		synonymmap.get().read();
	}

	private void readMetadata() {
		Iterable<String> lines = FileUtils.readLines(DiskStorage.getDirectory("database", "meta"),
				FreehalFiles.create("files.csv"));

		for (String line : lines) {
			String[] csv = line.split(":");
			if (csv.length == 2) {
				try {
					cacheFiles.put(csv[0], Integer.valueOf(csv[1]));
				} catch (NumberFormatException e) {
					// ignore
				}
			}
		}
	}

	private void writeMetadata() {
		StringBuilder sb = new StringBuilder();
		for (String filename : cacheFiles.keySet()) {
			sb.append(filename).append(":").append(cacheFiles.get(filename)).append("\n");
		}
		FileUtils.write(DiskStorage.getDirectory("database", "meta"), FreehalFiles.create("files.csv"),
				sb.toString());
	}

	@Override
	public Set<XmlFact> findFacts(XmlFact xfact) {
		LogUtils.i("find by fact: " + xfact);

		xfact.insertSynonyms(synonymmap.get());
		xfact.toggle(FreehalConfig.getTagger());

		List<Word> words = xfact.getWords();
		List<Word> usefulWords = filterUsefulWords(words);

		return findFacts(usefulWords);
	}

	private List<Word> filterUsefulWords(List<Word> words) {
		List<Word> usefulWords = new ArrayList<Word>();
		for (Word word : words) {
			if (FreehalConfig.getTagger().isIndexWord(word)) {
				LogUtils.i("index word: " + word);
				usefulWords.add(word);
			} else
				LogUtils.i("no index word: " + word);

		}
		return usefulWords;
	}

	@Override
	public Set<XmlFact> findFacts(List<Word> words) {
		LogUtils.i("find by words: " + words);

		Set<XmlFact> found = new HashSet<XmlFact>();
		for (Word w : words) {
			found.addAll(findFacts(w));
		}

		LogUtils.i(found.size() + " facts found.");

		return found;
	}

	@Override
	public Set<XmlFact> findFacts(Word word) {
		LogUtils.i("find by word: " + word);

		return findFacts(new DiskStorage.Key(word));
	}

	private Set<XmlFact> findFacts(DiskStorage.Key key) {
		LogUtils.i("find by key: " + key);

		FreehalFile databaseFile = DiskStorage.getFile("database", "index", key, null);

		Set<XmlFact> found = findFacts(databaseFile);

		return found;
	}

	private Set<XmlFact> findFacts(FreehalFile databaseFile) {
		final Set<XmlFact> list = new HashSet<XmlFact>();

		if (databaseFile.isDirectory()) {
			LogUtils.i("find in directory: " + databaseFile);

			FreehalFile[] files = databaseFile.listFiles();
			for (FreehalFile file : files) {
				if (file.isFile() && file.getName().contains(".xml")) {
					list.addAll(findFacts(file));
				}
			}
		}

		else if (databaseFile.isFile()) {
			LogUtils.i("find in file: " + databaseFile);

			final Iterable<String> xmlInput = FileUtils.readLines(databaseFile);
			final XmlStreamIterator xmlPre = new XmlUtils.XmlStreamIterator(xmlInput);

			XmlUtils.readXmlFacts(xmlPre, null, new XmlFactReciever() {
				@Override
				public void useXmlFact(XmlFact xfact, int countFacts, long start, FreehalFile filename,
						int countFactsSoFar) {

					list.add(xfact);
					LogUtils.d("found fact: " + xfact);
				}
			});
		}

		Runtime.getRuntime().gc();

		return list;
	}

	@Override
	public void updateCache() {
		updateCache(FreehalFiles.create(""));
	}

	@Override
	public void updateCache(FreehalFile databaseFile) {
		if (!databaseFile.isAbsolute()) {
			databaseFile = FreehalFiles.create(FreehalConfig.getLanguageDirectory().getPath(),
					databaseFile.getPath());
		}

		if (databaseFile.isDirectory()) {
			LogUtils.i("update cache (directory): " + databaseFile);

			FreehalFile[] files = databaseFile.listFiles();
			for (FreehalFile file : files) {
				LogUtils.i("file:" + file);
				if (file.isFile() && file.getName().contains(".xml")) {
					this.updateCache(file);
				}
			}
		}

		else if (databaseFile.isFile()) {
			if (cacheFiles.containsKey(databaseFile.getName())
					&& cacheFiles.get(databaseFile.getName()) == (int) databaseFile.length()) {
				LogUtils.i("cache is up to date (file): " + databaseFile);

			} else {
				LogUtils.i("update cache (file): " + databaseFile);

				final Iterable<String> xmlInput = FileUtils.readLines(databaseFile);

				// order the xml data
				final XmlStreamIterator xmlPre = new XmlUtils.XmlStreamIterator(xmlInput);

				// a separate scope for garbage collector!
				{
					// we use a helper class for updating the facts cache
					final List<CacheUpdater> updaters = new ArrayList<CacheUpdater>();
					updaters.add(new FactCacheUpdater());
					updaters.add(new SynonymCacheUpdater(synonymmap));

					// update the caches
					for (CacheUpdater updater : updaters) {
						updater.start();
					}

					// don't print all these checks whether a fact is a synonym
					// or
					// not...
					LogUtils.addTemporaryFilter("xml", "debug");

					// read the xml data and build XmlFact objects
					XmlUtils.readXmlFacts(xmlPre, databaseFile, new XmlFactReciever() {
						@Override
						public void useXmlFact(XmlFact xfact, int countFacts, long start,
								FreehalFile filename, int countFactsSoFar) {

							// update the caches
							for (CacheUpdater updater : updaters) {
								updater.add(xfact);
							}
						}
					});

					// reset the temporary log filter from above!
					LogUtils.resetTemporaryFilters();

					// update the caches
					for (CacheUpdater updater : updaters) {
						updater.stop();
					}
				}
				System.gc();

				LogUtils.i("updated cache (file): " + databaseFile);

				// ... and mark this database file as done!
				cacheFiles.put(databaseFile.getName(), (int) databaseFile.length());
				writeMetadata();
			}
		}
	}

	public static void setMemoryLimit(int memoryLimit) {
		FactCacheUpdater.memoryLimit = memoryLimit;
	}

	/**
	 * A helper class for updating the synonym cache.
	 * 
	 * @author "Tobias Schulz"
	 */
	protected static class SynonymCacheUpdater implements CacheUpdater {

		private Mutable<SynonymMap> synonymmap;

		public SynonymCacheUpdater(Mutable<SynonymMap> synonymmap) {
			this.synonymmap = synonymmap;
		}

		/**
		 * Add a fact to cache.
		 * 
		 * @param xfact
		 *        the fact to add
		 */
		public void add(XmlFact xfact) {
			// does this fact contain a synonym?
			if (xfact.part("verb").matches(XmlText.fromText("=")) == 1) {
				final XmlObj subject = xfact.part("subject");
				final XmlObj object = xfact.part("object");
				double countSubject = subject.countWords();
				double countObject = object.countWords();

				if (countSubject > 0 && countObject > 0) {
					if ((countSubject == 1 || subject.matches(XmlText.fromText("(a)")) == 0)
							&& (countObject == 1 || object.matches(XmlText.fromText("(a)")) == 0)) {

						// it does!
						synonymmap.get().add(Word.join(" ", subject.getWords()),
								Word.join(" ", object.getWords()));
					}
				}
			}
		}

		@Override
		public void stop() {
			// write the synonym cache files
			synonymmap.get().write();
			System.gc();
		}

		@Override
		public void start() {}
	}

	/**
	 * A helper class for updating the fact cache.
	 * 
	 * @author "Tobias Schulz"
	 */
	protected static class FactCacheUpdater implements CacheUpdater {

		/**
		 * the XmlFactReciever will store the data which need to be written to
		 * files in this hashmap
		 */
		final Mutable<Map<FreehalFile, Set<String>>> cacheFacts = new Mutable<Map<FreehalFile, Set<String>>>(
				new HashMap<FreehalFile, Set<String>>());

		/**
		 * The max count of facts to cache in memory.
		 */
		public static int memoryLimit = 500;

		int count = 0;
		boolean append = false;

		/**
		 * Add a fact to cache.
		 * 
		 * @param xfact
		 *        the fact to add
		 */
		@Override
		public void add(XmlFact xfact) {
			// LogUtils.d("update cache for this fact: " + xfact.printText());

			List<Word> words = xfact.getWords();
			for (Word w : words) {
				FreehalFile cacheFile = DiskStorage.getFile("database", "index", new DiskStorage.Key(w),
						FreehalFiles.create(xfact.getFilename().getName()));
				if (!cacheFacts.get().containsKey(cacheFile)) {
					cacheFacts.get().put(cacheFile, new HashSet<String>());
				}
				cacheFacts.get().get(cacheFile).add(xfact.printXml());
			}
			++count;

			if (count % memoryLimit == 0) {
				if (count > memoryLimit)
					append = true;
				stop("-" + (count / memoryLimit));
				start();
			}
		}

		/**
		 * Initialize everything. Run this before add().
		 */
		@Override
		public void start() {
			cacheFacts.set(new HashMap<FreehalFile, Set<String>>());
		}

		/**
		 * Write the fact cache files
		 */
		@Override
		public void stop() {
			stop("");
		}

		private void stop(final String suffix) {
			List<FreehalFile> sorted = new ArrayList<FreehalFile>(cacheFacts.get().keySet());
			Collections.sort(sorted);
			for (FreehalFile cacheFile : sorted) {
				StringBuilder content = new StringBuilder();
				for (final String xfactXml : cacheFacts.get().get(cacheFile)) {
					content.append(xfactXml);
				}
				LogUtils.d("write cache file: " + cacheFile);

				cacheFile = FreehalFiles.create(cacheFile.getPath() + suffix);
				if (append)
					FileUtils.append(cacheFile, content.toString());
				else
					FileUtils.write(cacheFile, content.toString());
			}

			cacheFacts.set(null);
			System.gc();
		}
	}

	protected interface CacheUpdater {

		void add(XmlFact xfact);

		void stop();

		void start();
	}
}
