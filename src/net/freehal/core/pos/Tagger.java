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

public interface Tagger {

	public Tags getPartOfSpeech(final String word);
	
	public boolean isName(final String word);

	public void readTagsFrom(final FreehalFile filename);

	public void readRegexFrom(final FreehalFile filename);

	public Word toggle(Word word);

	public void readToggleWordsFrom(final FreehalFile filename);

	public boolean isIndexWord(final Word word);
}
