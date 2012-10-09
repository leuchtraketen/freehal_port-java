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
package net.freehal.core.pos;

import net.freehal.core.pos.storage.TagContainer;
import net.freehal.core.pos.storage.TagListMemory;
import net.freehal.core.pos.storage.TaggerCache;
import net.freehal.core.pos.storage.TaggerCacheDisk;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;
import net.freehal.core.xml.Word;

/**
 * An abstract language-independent tagger implementation.
 * 
 * @author "Tobias Schulz"
 */
public abstract class StandardTagger implements Tagger {

	private TagContainer staticTags;
	private TagContainer regexTags;
	private Map<String, String> togglemap;

	/**
	 * Construct a new tagger instance by using a given tagger cache.
	 * 
	 * @param storage
	 *        the cache to use
	 */
	public StandardTagger(TaggerCache storage) {
		staticTags = storage.newContainer("staticTags");
		regexTags = new TagListMemory();
		togglemap = new HashMap<String, String>();
	}

	/**
	 * Construct a new tagger instance by using an instance of
	 * {@link TaggerCacheDisk} as tagger cache.
	 */
	public StandardTagger() {
		staticTags = new TaggerCacheDisk().newContainer("staticTags");
		regexTags = new TagListMemory();
		togglemap = new HashMap<String, String>();
	}

	@Override
	public Tags getPartOfSpeech(final String _word) {
		String word = StringUtils.toAscii(_word);
		if (word.length() == 0) {
			return null;
		}
		LogUtils.i("get part of speech: " + word);

		if (word.contains("?"))
			throw new IllegalArgumentException("");

		Tags tags = null;

		if (tags == null)
			tags = this.getBuiltinTags(word);
		if (tags == null)
			tags = this.getBuiltinTags(StringUtils.ucfirst(word));
		if (tags == null)
			tags = this.getBuiltinTags(word.toLowerCase());

		if (tags == null)
			tags = this.getStaticTags(word);
		if (tags == null)
			tags = this.getStaticTags(StringUtils.ucfirst(word));
		if (tags == null)
			tags = this.getStaticTags(word.toLowerCase());

		if (tags == null) {
			if (word.equals(",") || word.equals(";")) {
				tags = new Tags("komma", null);
				LogUtils.i("  builtin: " + tags);
			}
			List<String> matched = null;
			if ((matched = RegexUtils.imatch(word, "[{]{3}(.*?)[}]{3}")) != null) {
				tags = new Tags(matched.get(0), null);
				LogUtils.i("  predefined: " + tags);
			}
		}
		if (tags == null) {
			if (this.isName(word)) {
				tags = new Tags("n", null);
				LogUtils.i("  is name: " + tags);
			}
		}

		if (tags == null)
			tags = this.getRegexTags(word);

		if (tags == null) {
			if (RegexUtils.find(word, "[ABCDEFGHIJKLMNOPQRSTUVWXYZ]")) {
				tags = new Tags("n", null);
				LogUtils.i("  is upper case: " + tags);
			}
		}

		if (tags == null)
			tags = guess(word);
		if (tags == null)
			tags = askUser(word);

		if (tags == null)
			LogUtils.i("  not found: " + tags);

		return tags;
	}

	protected abstract Tags getBuiltinTags(String word);

	private Tags askUser(String word) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	private Tags guess(String word) {
		int wordLength = word.length();
		boolean wordIsLower = word.equals(word.toLowerCase());
		LogUtils.i("  guess: " + word);

		Map<String, Integer> ends = new HashMap<String, Integer>();
		if (wordLength > 1)
			ends.put(word.substring(wordLength - 1), 2);
		if (wordLength > 2)
			ends.put(word.substring(wordLength - 2), 6);
		if (wordLength > 3)
			ends.put(word.substring(wordLength - 3), 12);
		if (wordLength > 4)
			ends.put(word.substring(wordLength - 4), 8);
		if (wordLength > 5)
			ends.put(word.substring(wordLength - 5), 10);
		if (wordLength > 6)
			ends.put(word.substring(wordLength - 6), 12);
		Map<String, Integer> begins = new HashMap<String, Integer>();
		if (wordLength > 1)
			begins.put(word.substring(0, 1), 1);
		if (wordLength > 2)
			begins.put(word.substring(0, 2), 2);
		if (wordLength > 3)
			begins.put(word.substring(0, 3), 3);

		Map<String, Long> posToRating = new HashMap<String, Long>();
		Map<String, Long> posToCount = new HashMap<String, Long>();

		for (Map.Entry<String, Tags> entry : staticTags) {
			final String valWord = entry.getKey();
			final String valType = entry.getValue().getCategory();

			if (valType != null) {
				if (!valType.equals(Tags.NOUN) && !valType.equals(Tags.ADJECTIVE)
						&& !valType.equals(Tags.VERB))
					continue;
				boolean valWordIsLower = valWord.equals(valWord.toLowerCase());

				if ((wordIsLower && !valWordIsLower) || (!wordIsLower && valWordIsLower))
					continue;

				long rating = posToRating.containsKey(valType) ? posToRating.get(valType) : 0;
				long count = posToCount.containsKey(valType) ? posToCount.get(valType) : 0;
				for (Map.Entry<String, Integer> end : ends.entrySet()) {
					if (valWord.endsWith(end.getKey())) {
						rating += end.getValue();
						count += 1;
					}
				}
				for (Map.Entry<String, Integer> begin : begins.entrySet()) {
					if (valWord.startsWith(begin.getKey())) {
						rating += begin.getValue();
						count += 1;
					}
				}
				posToRating.put(valType, rating);
				// posToRating.put(" + valType + ", " + rating + ");");
				posToCount.put(valType, count);
			}
		}

		String bestCategory = null;
		long bestScore = 0;

		for (Map.Entry<String, Long> iter : posToRating.entrySet()) {
			final String category = iter.getKey();
			long score = iter.getValue();
			long count = posToCount.get(iter.getKey());
			LogUtils.i("  -> part of speech: '" + category + "', rating: " + score + ", count: " + count);

			score *= score;
			score = 100 * score / count;

			if (score > bestScore || bestScore == 0) {
				bestScore = score;
				bestCategory = category;
			}

			LogUtils.i("  -> part of speech: '" + category + "', rating: " + score + ", count: " + count);
		}

		Tags tags = null;
		if (bestCategory != null) {
			tags = new Tags(bestCategory, null);
			staticTags.add(word, tags);
			writeTagsTo(FreehalFiles.getFile("guessed.pos"), new Word(word, tags));
			LogUtils.i("  guessed: " + tags);
		}

		return tags;
	}

	/**
	 * To be implemented in a language-specific tagger class!
	 */
	public abstract boolean isName(final String word);

	public static void writeTagsTo(FreehalFile filename, Word word) {
		if (word.hasTags()) {
			StringBuilder toAppend = new StringBuilder();

			toAppend.append(word.getWord()).append(":\n");
			toAppend.append(word.getTags().toTagsFormat());

			LogUtils.i("write part of speech file: " + filename);
			LogUtils.i(toAppend.toString());

			Storages.inLanguageDirectory(filename).append(toAppend.toString());
		}
	}

	@Override
	public void readTagsFrom(FreehalFile filename) {
		staticTags.add(filename);
	}

	@Override
	public void readRegexFrom(FreehalFile filename) {
		regexTags.add(filename);
	}

	@Override
	public void readToggleWordsFrom(FreehalFile filename) {
		LogUtils.i("read verbs file: " + filename);

		Iterable<String> lines = Storages.inLanguageDirectory(filename).readLines();

		for (String line : lines) {
			line = line.trim();
			String[] verbs = line.split("[,]+");
			if (verbs.length >= 2) {
				togglemap.put(verbs[0], verbs[1]);
				togglemap.put(verbs[1], verbs[0]);
			}
		}
	}

	private Tags getStaticTags(final String word) {
		if (staticTags.containsKey(word)) {
			LogUtils.i("  found (static): " + staticTags.get(word));
			return staticTags.get(word);
		} else
			return null;
	}

	private Tags getRegexTags(final String word) {
		for (Map.Entry<String, Tags> entry : regexTags) {
			final String regex = entry.getKey();
			final Tags tags = entry.getValue();

			if (RegexUtils.ifind(word, regex)) {
				LogUtils.i("  found (static): " + tags);
				return tags;
			}
		}
		return null;
	}

	@Override
	public boolean isIndexWord(final Word word) {
		if (!word.hasTags()) {
			word.setTags(this);
		}

		if (word.hasTags())
			return word.getTags().isCategory("n") || word.getTags().isCategory("adj");
		else
			return false;
	}

	@Override
	public Word toggle(Word word) {
		if (togglemap.containsKey(word.getWord())) {
			return new Word(togglemap.get(word.getWord()), word.getTags());
		}

		if (!word.hasTags()) {
			word.setTags(this);
		}

		return word;
	}
}
