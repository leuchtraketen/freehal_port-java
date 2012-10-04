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

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.pos.Tags;

/**
 * This class represents an abstract XML object and is extended by
 * {@link XmlList}, {@link XmlText} and their subclasses like {@link XmlFact}
 * and {@link XmlSynonyms}.
 * 
 * @see XmlList
 * @see XmlFact
 * @see XmlText
 * @author "Tobias Schulz"
 */
public abstract class XmlObj {

	/** The name of the tag this object represents. */
	protected String name = new String();

	/**
	 * {@code true} if {@link #prepareWords()} has already been run,
	 * {@code false} otherwise.
	 */
	protected boolean isCachedWords = false;
	/**
	 * A cache of all words contained in all embedded XML objects used by
	 * {@link #getWords()} and {@link #getWords(List)}.
	 */
	protected List<Word> cacheWords = new ArrayList<Word>();

	/**
	 * Create a new XML object. This constructor does nothing.
	 */
	public XmlObj() {}

	/**
	 * Get the name of the tag which is represented by this object.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the tag which is represented by this object.
	 * 
	 * @param name
	 *        the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The XML code (ASCII) representation of this data structure.
	 * 
	 * @return XML code
	 */
	public String printXml() {
		return printXml(0, 0);
	}

	protected abstract String printXml(int level, int secondlevel);

	/**
	 * A string representation of this XML object and its embedded XML objects
	 * which is principally used for logging purposes.
	 * 
	 * @return a string representation
	 */
	public abstract String printStr();

	/**
	 * All words contained in any embedded objects join'ed together with spaces.
	 * 
	 * @return the text representation of this object
	 */
	public abstract String printText();

	/**
	 * Search all words in all embedded objects and put them in
	 * {@link #cacheWords}. Set {@link #isCachedWords} to {@code true}.
	 * 
	 * @return {@code false} if the words are already cached, {@code false}
	 *         otherwise.
	 */
	protected boolean prepareWords() {
		if (isCachedWords)
			return false;
		cacheWords.clear();
		isCachedWords = true;
		return true;
	}

	public List<Word> getWords() {
		if (!isCachedWords) {
			prepareWords();
		}
		return cacheWords;
	}

	public int getWords(List<Word> words) {
		if (!isCachedWords) {
			prepareWords();
		}
		words.addAll(cacheWords);
		return cacheWords.size();
	}

	public void resetCache() {
		isCachedWords = false;
		cacheWords = new ArrayList<Word>();
	}

	public abstract double isLike(XmlObj other);

	public abstract double matches(XmlObj other);

	public abstract double countWords();

	public double countTags(Tags tags) {
		List<Word> words = this.getWords();
		int amount = 0;
		for (Word word : words) {
			if (word.hasTags()) {
				if (word.getTags().isType(tags.getType())) {
					++amount;
				}
			}
		}

		return (double) amount;
	}

	/**
	 * Toggle all words in any embedded XML objects using the global part of
	 * speech tagger.
	 * 
	 * @see net.freehal.core.pos.Taggers#getTagger()
	 * @see net.freehal.core.pos.Tagger#toggle(Word)
	 */
	public abstract boolean toggle();

	@Override
	public abstract String toString();

	@Override
	public int hashCode() {
		return hashString().hashCode();
	}

	protected abstract String hashString();

	@Override
	public boolean equals(Object other) {
		return this.hashCode() == other.hashCode();
	}
}
