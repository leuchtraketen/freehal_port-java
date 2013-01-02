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
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;

import net.freehal.core.xml.Word;

/**
 * A part of speech tagger; all part of speech taggers have to implement this
 * interface.
 * 
 * @author "Tobias Schulz"
 */
public interface Tagger {

	public void setDatabase(Factory<TagContainer> newFactory);

	/**
	 * Returns the part of speech tags of the given word as a {@link Tags}
	 * object.
	 * 
	 * @param word
	 *        the word string to analyse
	 * @return an instance of {@link Tags} representing the part of speech tags
	 */
	public Tags getPartOfSpeech(final String word);

	/**
	 * Is the given word a name?
	 * 
	 * @param word
	 *        the word string to analyse
	 * @return {@code true} if the given word is a name, {@code false} otherwise
	 */
	public boolean isName(final String word);

	/**
	 * Read pairs of words and part of speech tags from a given database file.
	 * 
	 * @param filename
	 *        the file to read
	 */
	public void readTagsFrom(final FreehalFile filename);

	/**
	 * Read regular expression rules from a given database file.
	 * 
	 * @param filename
	 *        the file to read
	 */
	public void readRegexFrom(final FreehalFile filename);

	/**
	 * If the given word is a first or second person pronoun, change the point
	 * of view of the given word; for example, "you" is returned if "I" is given
	 * or "my" is returns if the argument is "your"; otherwise, the given word
	 * is returned.
	 * 
	 * @param word
	 *        the word
	 * @return the second person if a first person pronoun is given, the first
	 *         person if a first person pronoun is given, and the given word
	 *         otherwise
	 */
	public Word toggle(Word word);

	/**
	 * Read predefined rules for {@link #toggle(Word)} from the given file.
	 * 
	 * @param filename
	 *        the file to read
	 */
	public void readToggleWordsFrom(final FreehalFile filename);

	/**
	 * Can the given word be an index for a fact database?
	 * 
	 * @param word
	 *        the word to check
	 * @return {@code true} if it can be an index, {@code false} otherwise.
	 */
	public boolean isIndexWord(final Word word);
}
