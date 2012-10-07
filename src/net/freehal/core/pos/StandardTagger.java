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
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	/**
	 * To be implemented in a language-specific tagger class!
	 */
	public abstract boolean isName(final String word);

	public static void writeTagsTo(FreehalFile filename, Word word) {
		if (word.hasTags()) {
			StringBuilder toAppend = new StringBuilder();

			toAppend.append(word.getWord()).append(":\n");
			toAppend.append(word).append(":\n");
			toAppend.append("  type: ").append(word.getTags().getCategory()).append("\n");
			toAppend.append("  genus: ").append(word.getTags().getGender()).append("\n");

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
		if (staticTags.containsKey(word))
			return staticTags.get(word);
		else
			return null;
	}

	private Tags getRegexTags(final String word) {
		for (Map.Entry<String, Tags> entry : regexTags) {
			final String regex = entry.getKey();
			final Tags tags = entry.getValue();

			if (RegexUtils.ifind(word, regex)) {
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
