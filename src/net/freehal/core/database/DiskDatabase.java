package net.freehal.core.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlUtils;

public class DiskDatabase implements DatabaseImpl {

	@Override
	public List<XmlFact> findFacts(XmlFact xfact) {
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
	public List<XmlFact> findFacts(List<Word> words) {
		LogUtils.i("find by words: " + words);

		List<XmlFact> found = new ArrayList<XmlFact>();
		for (Word w : words) {
			found.addAll(findFacts(w));
		}

		LogUtils.i(found.size() + " facts found.");

		return found;
	}

	@Override
	public List<XmlFact> findFacts(Word word) {
		LogUtils.i("find by word: " + word);

		return findFacts(new Key(word));
	}

	private List<XmlFact> findFacts(Key key) {
		LogUtils.i("find by key: " + key);

		File databaseFile = getFile("index", key, null);

		List<XmlFact> found = findFacts(databaseFile);

		return found;
	}

	private List<XmlFact> findFacts(File databaseFile) {
		final List<XmlFact> list = new ArrayList<XmlFact>();

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
			LogUtils.d("database file:");
			LogUtils.d(xmlInput);
			final String xmlPre = XmlUtils.orderTags(xmlInput);
			LogUtils.d("xml parsing string:");
			LogUtils.d(xmlPre);

			XmlUtils.readXmlFacts(null, xmlPre, null, new XmlFactReciever() {
				@Override
				public void useXmlFact(DatabaseImpl d, XmlFact xfact,
						int countFacts, long start, File filename,
						int countFactsSoFar) {

					list.add(xfact);
					LogUtils.i("found fact: " + xfact);
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
}

class Key {

	private Word word;
	private String key;

	public Key(Word word) {
		this.word = word;
		init();
	}

	public Key(String word) {
		this.word = new Word(word);
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
