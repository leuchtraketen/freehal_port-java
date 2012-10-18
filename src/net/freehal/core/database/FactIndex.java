package net.freehal.core.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.freehal.core.database.Database.DatabaseComponent;
import net.freehal.core.pos.Taggers;
import net.freehal.core.storage.KeyValueDatabase;
import net.freehal.core.storage.KeyValueTransaction;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.FactProvider;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlUtils;
import net.freehal.core.xml.XmlUtils.XmlStreamIterator;

public class FactIndex implements FactProvider, DatabaseComponent {

	private DatabaseComponent cacheUpdater;
	private KeyValueDatabase<Iterable<XmlFact>> factsCache;

	public FactIndex(KeyValueDatabase<Iterable<XmlFact>> factsCache) {
		this.factsCache = factsCache;
	}

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

		// FreehalFile databaseFile = DirectoryUtils.getCacheFile("database",
		// "index", key, null);
		// Set<XmlFact> found = findFacts(databaseFile);

		KeyValueTransaction<Iterable<XmlFact>> transaction = factsCache.transaction();

		Iterable<XmlFact> result = transaction.get(key.getKey());
		Set<XmlFact> found = new HashSet<XmlFact>();
		if (result != null) {
			for (XmlFact xfact : result) {
				found.add(xfact);
			}
		}

		transaction.finish();

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
				public void useXmlFact(XmlFact xfact, long start, FreehalFile filename, int countFactsSoFar) {

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
	public void startUpdateCache(FreehalFile databaseFile) {
		cacheUpdater = new FactCacheUpdater(factsCache);
		cacheUpdater.startUpdateCache(databaseFile);
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

		private KeyValueDatabase<Iterable<XmlFact>> factsCache;
		private KeyValueTransaction<Iterable<XmlFact>> transaction;

		public FactCacheUpdater(KeyValueDatabase<Iterable<XmlFact>> factsCache) {
			this.factsCache = factsCache;
		}

		/**
		 * the XmlFactReciever will store the data which need to be written to
		 * cache in this hashmap
		 */
		private Map<String, Set<XmlFact>> cache = null;
		private FreehalFile databaseFile = null;

		/**
		 * The max count of facts to cache in memory.
		 */
		public static int memoryLimit = 500;

		private int count = 0;

		/**
		 * Add a fact to cache.
		 * 
		 * @param xfact
		 *        the fact to add
		 */
		@Override
		public void addToCache(XmlFact xfact) {
			// LogUtils.d("update cache for this fact: " + xfact.printText());

			if (cache != null) {
				List<Word> words = xfact.getWords();
				for (Word w : words) {
					final String key = new DirectoryUtils.Key(w).getKey();
					if (!cache.containsKey(key)) {
						cache.put(key, new HashSet<XmlFact>());
					}
					cache.get(key).add(xfact);
				}
				++count;

				if (count % memoryLimit == 0 && count >= memoryLimit) {
					stopUpdateCache();
					startUpdateCache(databaseFile);
				}
			}
		}

		/**
		 * Initialize everything. Run this before add().
		 */
		@Override
		public void startUpdateCache(FreehalFile databaseFile) {
			this.databaseFile = databaseFile;
			transaction = factsCache.transaction();
			cache = new HashMap<String, Set<XmlFact>>();
		}

		/**
		 * Write the fact cache files
		 */
		@Override
		public void stopUpdateCache() {
			if (cache != null && transaction != null) {
				final int suffix = (count / memoryLimit) + (count % memoryLimit == 0 ? 0 : 1);
				final String dbname = databaseFile.getName() + "-" + suffix;

				transaction.remove(null, dbname);

				for (String key : cache.keySet()) {
					LogUtils.d("write cache: dbname=" + dbname + ", key: " + key);
					transaction.set(key, cache.get(key), dbname);
				}

				transaction.finish();
				transaction = null;
				cache = null;
				System.gc();
			}
		}
	}
}
