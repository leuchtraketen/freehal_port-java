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

import java.util.Collection;

/**
 * This class represents list of a word and its synonyms and is based on
 * {@link XmlList}.
 * 
 * @author "Tobias Schulz"
 */
public class XmlSynonyms extends XmlList.OrOperation {

	@SuppressWarnings("unused")
	private XmlSynonyms() {
		setName("synonyms");
	}

	/**
	 * Construct a new XML synonym list which contains the given word and its
	 * synonyms, which are found by using the given {@link SynonymProvider} and
	 * {@link SynonymProvider#getSynonyms(String)}.
	 * 
	 * @param word
	 *        the word to add and to search synonyms for
	 * @param database
	 *        the database which conatins the synonym information
	 */
	public XmlSynonyms(final Word word, SynonymProvider database) {
		setName("synonyms");
		add(XmlWord.fromWord(word));
		add(database.getSynonyms(word.getWord()));
	}

	public XmlSynonyms(final Collection<String> synonyms) {
		add(synonyms);
	}

	/**
	 * A no-op because an XML synonym list already contains all synonyms.
	 */
	@Override
	public void insertSynonyms(SynonymProvider database) {
		// ignore, the synonyms are already inserted if we are a XmlSynonxms
		// instance!
	}

	/**
	 * Add the given collection of strings as synonyms to this XML synonym list.
	 * 
	 * @param collection
	 *        the synonyms to add
	 */
	private void add(Collection<String> synonyms) {
		for (String word : synonyms) {
			this.add(XmlWord.fromWord(word));
		}
	}
}
