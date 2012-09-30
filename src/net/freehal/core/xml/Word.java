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
package net.freehal.core.xml;

import java.util.Iterator;
import java.util.List;

import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.pos.Tags;
import net.freehal.core.util.LogUtils;

/**
 * This class represents a word. It is a wrapper for a String which contains the
 * text and a {@link Tags} object which represents the part of speech of
 * the word.
 * 
 * @author "Tobias Schulz"
 */
public class Word {

	/** the word. */
	private String word = new String();
	/** the part of speech */
	private Tags tag = null;

	/**
	 * Construct an empty word object.
	 */
	public Word() {}

	/**
	 * Constructs a word object with a given word and given part-of-speech tags.
	 * 
	 * @param word
	 *        the word
	 * @param tag
	 *        the part-of-speech tags
	 */
	public Word(String word, Tags tag) {
		init(null, word, tag);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param word
	 *        the word to copy
	 */
	public Word(Word word) {
		init(word, null, null);
	}

	/**
	 * First create a copy of the given word, and then change the text and tags
	 * values if they are not {@code null}.
	 * 
	 * @param word
	 *        the word to copy
	 * @param wordStr
	 *        if not null this overrides the word
	 * @param tag
	 *        if not null this overrides the tags
	 */
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
			throw new IllegalArgumentException("A toString()'ed Word object is going to "
					+ "be set as a String to a Word object");
	}

	/**
	 * Copy everything from the given word.
	 * 
	 * @param newWord
	 *        the other word object to copy
	 */
	public void set(Word newWord) {
		init(newWord, null, null);
	}

	/**
	 * Only compare the text, not the tags.
	 */
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

	/**
	 * Use the given part of speech tagger to set the part of speech tags.
	 * 
	 * @param tagger
	 *        the part of speech tagger
	 */
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

	/**
	 * Are there any part of speech tags?
	 * 
	 * @return true if the tags are not null, false otherwise
	 */
	public boolean hasTags() {
		return tag != null;
	}

	/**
	 * Like matches(XmlObj), but weighted.
	 * 
	 * @param other
	 * @return something between 0.0 and 1.0, depending on the amount of words
	 *         which are contained in the Xml Object.
	 */
	public double isLike(XmlObj other) {
		final List<Word> otherWords = other.getWords();

		for (Word otherWord : otherWords) {
			double matches = this.isLike(otherWord);
			LogUtils.d("---- compare: " + this.toString() + " isLike " + otherWord + " = " + matches);
			if (matches > 0)
				return matches * (3.0 * otherWords.size() + 1) / 4.0 / otherWords.size();
		}

		LogUtils.d("---- compare: " + this.toString() + " isLike " + other.printStr() + " = " + 0);

		return 0;
	}

	/**
	 * Like matches(Word), but weighted.
	 * 
	 * @param otherWord
	 * @return 0.75 if there are no part of speech tags, 1.0 for a noun, 0.5 for
	 *         an adjective, 0.75 for a verb, 0.1 for article and 0.4 for any
	 *         other part of speech given, neither in this object not in the
	 *         other one.
	 */
	private double isLike(Word otherWord) {
		if (this.matches(otherWord) > 0) {
			Tags tags = null;
			if (this.hasTags())
				tags = this.getTags();
			else if (otherWord.hasTags())
				tags = otherWord.getTags();

			double weight = tags == null ? 0.75 : tags.isType("n") ? 1.0 : tags.isType("adj") ? 0.5 : tags
					.isType("v") ? 0.75 : tags.isType("art") ? 0.1 : 0.4;

			return weight;
		} else {
			return 0;
		}
	}

	/**
	 * Does the given Xml Object contain this word?
	 * 
	 * @param other
	 *        the Xml Object
	 * @return 1.0 if this word is contained in the Xml Object, 0.0 otherwise
	 */
	public double matches(XmlObj other) {
		final List<Word> otherWords = other.getWords();

		for (Word otherWord : otherWords) {
			double matches = this.matches(otherWord);
			if (matches > 0)
				return matches;
		}

		return 0;
	}

	/**
	 * Like {@code equals(otherWord)}, but NOT case-sensitive!
	 * 
	 * @param otherWord
	 * @return 1.0 if this word is the same as the other word
	 *         (case-insensitive), 0.0 otherwise
	 */
	private double matches(Word otherWord) {
		return (this.word.equals(otherWord.word) || this.word.toLowerCase().equals(
				otherWord.word.toLowerCase())) ? 1 : 0;
	}

	@Override
	public String toString() {
		return "{word=\"" + word + "\",tags=" + tag + "}";
	}

	/**
	 * A static method which joins the given words with a given delimiter to a
	 * string.
	 * 
	 * @see StringUtils.join(...)
	 * @param delimiter
	 *        the delimiter
	 * @param words
	 *        a list of words
	 * @return a string
	 */
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
