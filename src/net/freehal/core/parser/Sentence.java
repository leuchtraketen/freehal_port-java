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
package net.freehal.core.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.freehal.core.grammar.Entities;
import net.freehal.core.grammar.Grammar2012;
import net.freehal.core.pos.Tags;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlUtils;
import net.freehal.core.xml.XmlUtils.XmlStreamIterator;

public class Sentence {

	private List<Word> wordsList = null;
	private XmlFact xfact = null;

	public Sentence(AbstractParser parser, final String input) {
		String tmp = input;
		tmp = RegexUtils.replace(tmp, "[.!\\?\"']+", "");
		tmp = RegexUtils.trim(tmp, "-;,#+ ");
		String[] tmpList = tmp.split("\\s+");
		LogUtils.i("sentence: " + tmp);

		wordsList = new ArrayList<Word>();

		for (String word : tmpList) {
			Tags tags = FreehalConfig.getTagger().getPartOfSpeech(word);
			wordsList.add(new Word(word, tags));
		}

		parse();
	}

	private void parse() {
		List<Entities> parsed = FreehalConfig.getGrammar().parse(wordsList);
		final String xmlInput = Grammar2012.printXml(parsed);
		LogUtils.d("parsed fact as xml:");
		LogUtils.d(xmlInput);
		final XmlStreamIterator xmlPre = XmlUtils.orderTags(xmlInput);
		LogUtils.d("xml parsing string:");
		LogUtils.d(xmlPre);
		final Sentence sentence = this;
		XmlUtils.readXmlFacts(xmlPre, null, new XmlFactReciever() {

			@Override
			public void useXmlFact(XmlFact xfact, int countFacts, long start,
					File filename, int countFactsSoFar) {
				xfact.tag(FreehalConfig.getTagger());
				sentence.setFact(xfact);
				LogUtils.i("found fact: " + xfact);
			}

		});
	}

	protected void setFact(XmlFact xfact) {
		this.xfact = xfact;
	}

	public XmlFact getFact() {
		return xfact;
	}

	public boolean isValidFact() {
		return xfact != null;
	}
}
