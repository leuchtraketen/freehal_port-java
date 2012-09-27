/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.freehal.core.cache.DiskStorage;
import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlObj;
import net.freehal.core.xml.XmlText;
import net.freehal.core.xml.XmlUtils;

public class DiskDatabase implements DatabaseImpl {

	private Map<String, Integer> cacheFiles = new HashMap<String, Integer>();
	private SynonymMap synonymmap = new SynonymMap();

	public DiskDatabase() {
		readMetadata();
		synonymmap.read();
	}

	private void readMetadata() {
		List<String> lines = FileUtils.readLines(DiskStorage.getDirectory(
				"database", "meta"), new File("files.csv"));

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
			sb.append(filename).append(":").append(cacheFiles.get(filename))
					.append("\n");
		}
		FileUtils.write(DiskStorage.getDirectory("database", "meta"), new File(
				"files.csv"), sb.toString());
	}

	@Override
	public Set<XmlFact> findFacts(XmlFact xfact) {
		LogUtils.i("find by fact: " + xfact);

		xfact.insertSynonyms(synonymmap);
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

		File databaseFile = DiskStorage.getFile("database", "index", key, null);

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

		Runtime.getRuntime().gc();

		return list;
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

		if (databaseFile.isDirectory()) {
			LogUtils.i("update cache (directory): " + databaseFile);

			File[] files = databaseFile.listFiles();
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".xml")) {
					this.updateCache(file);
				}
			}
		}

		else if (databaseFile.isFile()) {
			final String xmlInput = FileUtils.read(databaseFile);

			if (cacheFiles.containsKey(databaseFile.getName())
					&& cacheFiles.get(databaseFile.getName()) == xmlInput
							.hashCode()) {
				LogUtils.i("cache is up to date (file): " + databaseFile);

			} else {
				LogUtils.i("update cache (file): " + databaseFile);

				// order the xml data
				final String xmlPre = XmlUtils.orderTags(xmlInput);

				// the XmlFactReciever will store the data which need to be
				// written to files in this hashmaps
				final Mutable<Map<File, Set<XmlFact>>> cacheFacts = new Mutable<Map<File, Set<XmlFact>>>(
						new HashMap<File, Set<XmlFact>>());

				// read the xml data and build XmlFact objects
				XmlUtils.readXmlFacts(xmlPre, databaseFile,
						new XmlFactReciever() {
							@Override
							public void useXmlFact(XmlFact xfact,
									int countFacts, long start, File filename,
									int countFactsSoFar) {

								updateCacheFacts(xfact, cacheFacts);
								updateCacheSynonyms(xfact);
							}
						});

				// write the fact cache files
				for (File cacheFile : cacheFacts.get().keySet()) {
					StringBuilder content = new StringBuilder();
					for (XmlFact xfact : cacheFacts.get().get(cacheFile)) {
						content.append(xfact.printXml());
					}
					LogUtils.d("write cache file : " + cacheFile);

					FileUtils.write(cacheFile, content.toString());
				}

				// write the synonym cache files
				synonymmap.write();

				// ... and mark this database file as done!
				cacheFiles.put(databaseFile.getName(), xmlInput.hashCode());
				writeMetadata();
			}
		}
	}

	protected void updateCacheSynonyms(XmlFact xfact) {
		// does this fact contain a synonym?
		if (xfact.part("verb").matches(XmlText.fromText("=")) == 1) {
			final XmlObj subject = xfact.part("subject");
			final XmlObj object = xfact.part("object");
			double countSubject = subject.countWords();
			double countObject = object.countWords();

			if (countSubject > 0 && countObject > 0) {
				if ((countSubject == 1 || subject.matches(XmlText
						.fromText("(a)")) == 0)
						&& (countObject == 1 || object.matches(XmlText
								.fromText("(a)")) == 0)) {

					// it does!
					synonymmap.add(Word.join(" ", subject.getWords()),
							Word.join(" ", object.getWords()));
				}
			}
		}
	}

	protected void updateCacheFacts(XmlFact xfact,
			Mutable<Map<File, Set<XmlFact>>> cache) {
		LogUtils.d("update cache for this fact: " + xfact.printText());

		List<Word> words = xfact.getWords();
		for (Word w : words) {
			File cacheFile = DiskStorage.getFile("database", "index",
					new DiskStorage.Key(w), new File(xfact.getFilename()
							.getName()));
			if (!cache.get().containsKey(cacheFile)) {
				cache.get().put(cacheFile, new HashSet<XmlFact>());
			}
			cache.get().get(cacheFile).add(xfact);
		}
	}
}
