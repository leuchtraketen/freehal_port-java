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

public class XmlSynonyms extends XmlList {

	public XmlSynonyms() {
		setName("synonyms");
	}

	public XmlSynonyms(final Word word, SynonymProvider database) {
		add(XmlText.fromText(word));
		add(database.getSynonyms(word.getWord()));
	}

	@Override
	public void insertSynonyms(SynonymProvider database) {
		// ignore, the synonyms are already inserted if we are a XmlSynonxms
		// instance!
	}

	public void add(Collection<String> collection) {
		for (String word : collection) {
			this.add(XmlText.fromText(word));
		}
	}
}
