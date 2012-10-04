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

import net.freehal.core.pos.AbstractTagger;
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

	protected String name = new String();

	protected boolean isCachedWords = false;
	protected boolean isCachedTags = false;
	protected List<Word> cacheWords = new ArrayList<Word>();

	public XmlObj() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void trim() {
		// does nothing
	}

	public String printXml() {
		return printXml(0, 0);
	}

	protected String printXml(int level, int secondlevel) {
		return "";
	}

	public String printStr() {
		return "";
	}

	public String printText() {
		return "";
	}

	protected boolean prepareWords() {
		if (isCachedWords)
			return false;
		cacheWords.clear();
		isCachedWords = true;
		return true;
	}

	protected boolean prepareTags(AbstractTagger tagger) {
		if (isCachedTags)
			return false;

		prepareWords();
		isCachedTags = (tagger != null);
		return (tagger != null);
	}

	public void tag(AbstractTagger tagger) {
		prepareTags(tagger);
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
		isCachedTags = false;
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
	 * Toggle all words in any embedded XML objects using the given part of
	 * speech tagger.
	 * 
	 * @see AbstractTagger#toggle(Word)
	 */
	public abstract boolean toggle(AbstractTagger tagger);

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
