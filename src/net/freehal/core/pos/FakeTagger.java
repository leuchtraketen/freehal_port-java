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

import net.freehal.core.util.FreehalFile;

import net.freehal.core.xml.Word;

/**
 * A fake implementation of a tagger for API tests.
 * 
 * @author "Tobias Schulz"
 */
public class FakeTagger implements Tagger {

	/**
	 * Always returns a noun, no matter which word is given.
	 * 
	 * @return {@code new Tags("n", "", word);}
	 */
	@Override
	public Tags getPartOfSpeech(String word) {
		return new Tags("n", "", word);
	}

	/**
	 * Always returns {@code false}.
	 * 
	 * @return {@code false}
	 */
	@Override
	public boolean isName(String word) {
		return false;
	}

	/**
	 * A no-op.
	 */
	@Override
	public void readTagsFrom(FreehalFile filename) {}

	/**
	 * A no-op.
	 */
	@Override
	public void readRegexFrom(FreehalFile filename) {}

	/**
	 * Returns the given word.
	 * 
	 * @return the given word
	 */
	@Override
	public Word toggle(Word word) {
		return word;
	}

	/**
	 * A no-op.
	 */
	@Override
	public void readToggleWordsFrom(FreehalFile filename) {}

	/**
	 * Always returns {@code true}.
	 * 
	 * @return {@code true}
	 */
	@Override
	public boolean isIndexWord(Word word) {
		return true;
	}
}
