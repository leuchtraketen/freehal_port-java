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
package net.freehal.core.xml;

import java.util.Iterator;
import java.util.List;

import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.pos.Tags;
import net.freehal.core.util.LogUtils;

public class Word {

	private String word = new String();
	private Tags tag = null;

	public Word() {
	}

	public Word(String word, Tags tag) {
		init(null, word, tag);
	}

	public Word(Word word) {
		init(word, null, null);
	}

	public Word(Word word, String wordStr, Tags tag) {
		init(word, wordStr, tag);
	}

	private void init(Word word, String wordStr, Tags tag) {
		if (word != null) {
			this.word = word.word;
			this.tag = word.tag;
		}
		if (wordStr != null)
			this.word = wordStr;
		if (tag != null)
			this.tag = tag;
		
		if (this.word.contains("{word="))
			throw new IllegalArgumentException(
					"A toString()'ed Word object is going to "
							+ "be set as a String to a Word object");
	}

	public void set(Word newWord) {
		init(newWord, null, null);
	}

	public boolean equals(Object o) {
		if (o instanceof Word) {
			return (this.word.equals(((Word) o).word));
		} else if (o instanceof String) {
			return (this.word.equals((String) o));
		} else {
			return false;
		}
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void setTags(Tags tag) {
		this.tag = tag;
	}

	public void setTags(AbstractTagger tagger) {
		this.tag = tagger.getPartOfSpeech(word);
	}

	public String getWord() {
		return word;
	}

	public int size() {
		return word.length();
	}

	public int length() {
		return word.length();
	}

	public Tags getTags() {
		return tag;
	}

	public boolean hasTags() {
		return tag != null;
	}

	public double isLike(XmlObj other) {
		final List<Word> otherWords = other.getWords();

		for (Word otherWord : otherWords) {
			double matches = this.isLike(otherWord);
			LogUtils.d("---- compare: " + this.toString() + " isLike "
					+ otherWord + " = " + matches);
			if (matches > 0)
				return matches * (3.0 * otherWords.size() + 1) / 4.0
						/ otherWords.size();
		}

		LogUtils.d("---- compare: " + this.toString() + " isLike "
				+ other.printStr() + " = " + 0);

		return 0;
	}

	private double isLike(Word otherWord) {
		if (this.matches(otherWord) > 0) {
			Tags tags = null;
			if (this.hasTags())
				tags = this.getTags();
			else if (otherWord.hasTags())
				tags = otherWord.getTags();

			double weight = tags == null ? 0.75 : tags.isType("n") ? 1 : tags
					.isType("adj") ? 0.5 : tags.isType("v") ? 0.75 : tags
					.isType("art") ? 0.1 : 0.4;

			return weight;
		} else {
			return 0;
		}
	}

	public double matches(XmlObj other) {
		final List<Word> otherWords = other.getWords();

		for (Word otherWord : otherWords) {
			double matches = this.matches(otherWord);
			if (matches > 0)
				return matches;
		}

		return 0;
	}

	private double matches(Word otherWord) {
		return (this.word.equals(otherWord.word) || this.word.toLowerCase()
				.equals(otherWord.word.toLowerCase())) ? 1 : 0;
	}

	@Override
	public String toString() {
		return "{word=\"" + word + "\",tags=" + tag + "}";
	}

	public static String join(String delimiter, List<Word> words) {
		if (words == null)
			return "";
		Iterator<Word> iter = words.iterator();
		StringBuilder builder = new StringBuilder();
		builder.append(iter.next().getWord());
		while (iter.hasNext()) {
			builder.append(delimiter).append(iter.next().getWord());
		}
		return builder.toString();
	}
}
