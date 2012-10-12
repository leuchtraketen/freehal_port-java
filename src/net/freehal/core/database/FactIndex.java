package net.freehal.core.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.freehal.core.database.Database.DatabaseComponent;
import net.freehal.core.pos.Taggers;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.xml.FactProvider;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlUtils;
import net.freehal.core.xml.XmlUtils.XmlStreamIterator;

public class FactIndex implements FactProvider, DatabaseComponent {

	private DatabaseComponent cacheUpdater;

	public FactIndex() {}

	@Override
	public Set<XmlFact> findFacts(XmlFact xfact) {
		LogUtils.i("find by fact: " + xfact);

		List<Word> words = xfact.getWords();
		List<Word> usefulWords = filterUsefulWords(words);

		return findFacts(usefulWords);
	}

	private List<Word> filterUsefulWords(List<Word> words) {
		List<Word> usefulWords = new ArrayList<Word>();
		for (Word word : words) {
			if (Taggers.getTagger().isIndexWord(word)) {
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

		return findFacts(new DirectoryUtils.Key(word));
	}

	private Set<XmlFact> findFacts(DirectoryUtils.Key key) {
		LogUtils.i("find by key: " + key);

		FreehalFile databaseFile = DirectoryUtils.getCacheFile("database", "index", key, null);

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

			final Iterable<String> xmlInput = databaseFile.readLines();
			final XmlStreamIterator xmlPre = new XmlUtils.XmlStreamIterator(xmlInput);

			XmlUtils.readXmlFacts(xmlPre, null, new XmlFactReciever() {
				@Override
				public void useXmlFact(XmlFact xfact, long start, FreehalFile filename,
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
	public void addToCache(XmlFact xfact) {
		cacheUpdater.addToCache(xfact);
	}

	@Override
	public void startUpdateCache() {
		cacheUpdater = new FactCacheUpdater();
		cacheUpdater.startUpdateCache();
	}

	@Override
	public void stopUpdateCache() {
		cacheUpdater.stopUpdateCache();
		cacheUpdater = null;
		System.gc();
	}

	public static void setMemoryLimit(int memoryLimit) {
		FactCacheUpdater.memoryLimit = memoryLimit;
	}

	/**
	 * A helper class for updating the fact cache.
	 * 
	 * @author "Tobias Schulz"
	 */
	private static class FactCacheUpdater implements Database.DatabaseComponent {

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
		public void addToCache(XmlFact xfact) {
			// LogUtils.d("update cache for this fact: " + xfact.printText());

			List<Word> words = xfact.getWords();
			for (Word w : words) {
				FreehalFile cacheFile = DirectoryUtils.getCacheFile("database", "index",
						new DirectoryUtils.Key(w), FreehalFiles.getFile(xfact.getFilename().getName()));
				if (!cacheFacts.get().containsKey(cacheFile)) {
					cacheFacts.get().put(cacheFile, new HashSet<String>());
				}
				cacheFacts.get().get(cacheFile).add(xfact.printXml());
			}
			++count;

			if (count % memoryLimit == 0) {
				if (count > memoryLimit)
					append = true;
				stopUpdateCache("-" + (count / memoryLimit));
				startUpdateCache();
			}
		}

		/**
		 * Initialize everything. Run this before add().
		 */
		@Override
		public void startUpdateCache() {
			cacheFacts.set(new HashMap<FreehalFile, Set<String>>());
		}

		/**
		 * Write the fact cache files
		 */
		@Override
		public void stopUpdateCache() {
			stopUpdateCache("");
		}

		private void stopUpdateCache(final String suffix) {
			List<FreehalFile> sorted = new ArrayList<FreehalFile>(cacheFacts.get().keySet());
			Collections.sort(sorted);
			for (FreehalFile cacheFile : sorted) {
				StringBuilder content = new StringBuilder();
				for (final String xfactXml : cacheFacts.get().get(cacheFile)) {
					content.append(xfactXml);
				}
				LogUtils.d("write cache file: " + cacheFile);

				cacheFile = FreehalFiles.getFile(cacheFile.getPath() + suffix);
				if (append)
					cacheFile.append(content.toString());
				else
					cacheFile.write(content.toString());
			}

			cacheFacts.set(null);
			System.gc();
		}
	}
}
