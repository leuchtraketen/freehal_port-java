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

import java.util.Collection;

/**
 * The synonym provider interface should be implemented by any class that can be
 * used to find out the synonyms of a word.
 * 
 * @author "Tobias Schulz"
 */
public interface SynonymProvider {

	/**
	 * Get the synonyms of the given String as a Collection of Strings.
	 * 
	 * @param text
	 *            the word to search
	 * @return the synonyms
	 */
	public Collection<String> getSynonyms(final String text);

	/**
	 * Get the synonyms of the given Word as a Collection of Words.
	 * 
	 * @param text
	 *            the word to search
	 * @return the synonyms
	 */
	public Collection<Word> getSynonyms(final Word word);

}
