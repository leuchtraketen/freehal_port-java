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
package net.freehal.core.pos;

import net.freehal.core.util.FreehalFile;

import net.freehal.core.xml.Word;


public class FakeTagger implements Tagger {

	@Override
	public Tags getPartOfSpeech(String word) {
		if (word.equals("is"))
			return new Tags("v", "", word);
		else if (word.equals("cool"))
			return new Tags("adj", "", word);
		else
			return new Tags("n", "", word);
	}

	@Override
	public boolean isName(String word) {
		return false;
	}

	@Override
	public void readTagsFrom(FreehalFile filename) {
	}

	@Override
	public void readRegexFrom(FreehalFile filename) {
	}

	@Override
	public Word toggle(Word word) {
		return word;
	}

	@Override
	public void readToggleWordsFrom(FreehalFile filename) {
	}

	@Override
	public boolean isIndexWord(Word word) {
		return true;
	}

}
