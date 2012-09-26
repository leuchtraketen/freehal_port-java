package net.freehal.core.database;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlUtils;

public class DiskDatabase implements DatabaseImpl {

	@Override
	public Set<XmlFact> findFacts(XmlFact xfact) {
		LogUtils.i("find by fact: " + xfact);

		xfact.insertSynonyms(this);
		xfact.toggle(FreehalConfig.getTagger());

		List<Word> words = xfact.getWords();
		List<Word> usefulWords = filterUsefulWords(words);

		return findFacts(usefulWords);
	}

	private List<Word> filterUsefulWords(List<Word> words) {
		// TODO Automatisch generierter Methodenstub
		return words;
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

		return findFacts(new Key(word));
	}

	private Set<XmlFact> findFacts(Key key) {
		LogUtils.i("find by key: " + key);

		File databaseFile = getFile("index", key, null);

		Set<XmlFact> found = findFacts(databaseFile);

		return found;
	}

	private Set<XmlFact> findFacts(File databaseFile) {
		final Set<XmlFact> list = new HashSet<XmlFact>();

		if (databaseFile.isDirectory()) {
			LogUtils.i("find in directory: " + databaseFile);

			File[] files = databaseFile.listFiles();
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".xml")) {
					list.addAll(findFacts(file));
				}
			}
		}

		else if (databaseFile.isFile()) {
			LogUtils.i("find in file: " + databaseFile);

			final String xmlInput = FileUtils.read(databaseFile);
			final String xmlPre = XmlUtils.orderTags(xmlInput);

			XmlUtils.readXmlFacts(xmlPre, null, new XmlFactReciever() {
				@Override
				public void useXmlFact(XmlFact xfact, int countFacts,
						long start, File filename, int countFactsSoFar) {

					list.add(xfact);
					LogUtils.d("found fact: " + xfact);
				}
			});
		}

		return list;
	}

	private File getFile(final String type, final Key key, final File filename) {
		StringBuilder keyPath = new StringBuilder();
		keyPath.append(key.getKey(0)).append("/").append(key.getKey(1))
				.append("/").append(key.getKey(2)).append("/")
				.append(key.getKey(3));

		File directory = new File(FreehalConfig.getCacheDirectory(),
				"database/" + type + "/" + keyPath);
		directory.mkdirs();

		if (filename == null)
			return directory;
		else
			return new File(directory.getPath(), filename.getPath());
	}

	@Override
	public void updateCache() {
		updateCache(new File(""));
	}

	@Override
	public void updateCache(File databaseFile) {
		if (!databaseFile.isAbsolute()) {
			databaseFile = new File(FreehalConfig.getLanguageDirectory()
					.getPath(), databaseFile.getPath());
		}
		LogUtils.i("update cache for this: " + databaseFile);

		if (databaseFile.isDirectory()) {
			LogUtils.i("update cache for this directory: " + databaseFile);

			File[] files = databaseFile.listFiles();
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".xml")) {
					this.updateCache(file);
				}
			}
		}

		else if (databaseFile.isFile()) {
			LogUtils.i("update cache for this file: " + databaseFile);

			final String xmlInput = FileUtils.read(databaseFile);
			final String xmlPre = XmlUtils.orderTags(xmlInput);

			final Mutable<Map<File, Set<XmlFact>>> cache = new Mutable<Map<File, Set<XmlFact>>>(
					new HashMap<File, Set<XmlFact>>());

			XmlUtils.readXmlFacts(xmlPre, databaseFile, new XmlFactReciever() {
				@Override
				public void useXmlFact(XmlFact xfact, int countFacts,
						long start, File filename, int countFactsSoFar) {

					updateCache(xfact, cache);
				}
			});

			for (File cacheFile : cache.get().keySet()) {
				StringBuilder content = new StringBuilder();
				for (XmlFact xfact : cache.get().get(cacheFile)) {
					content.append(xfact.printXml());
				}
				LogUtils.d("write cache file : " + cacheFile);

				FileUtils.write(cacheFile, content.toString());
			}
		}
	}

	protected void updateCache(XmlFact xfact,
			Mutable<Map<File, Set<XmlFact>>> cache) {
		LogUtils.d("update cache for this fact: " + xfact.printText());

		List<Word> words = xfact.getWords();
		for (Word w : words) {
			File cacheFile = getFile("index", new Key(w), new File(xfact
					.getFilename().getName()));
			if (!cache.get().containsKey(cacheFile)) {
				cache.get().put(cacheFile, new HashSet<XmlFact>());
			}
			cache.get().get(cacheFile).add(xfact);
		}
	}
}

class Key {

	private Word word;
	private String key;

	public Key(Word word) {
		this.word = word;
		init();
	}

	public Key(String word) {
		this.word = new Word(word, null);
		init();
	}

	private void init() {
		if (word.getWord().length() >= 4)
			key = word.getWord().substring(0, 4);
		else
			key = word.getWord();
		while (key.length() < 4)
			key += "_";
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public String getKey() {
		return key;
	}

	public char getKey(int index) {
		if (index >= 4 || index < 0)
			return 0;
		else
			return key.charAt(index);
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "{key=\"" + key + "\",word=" + word + "}";
	}
}
