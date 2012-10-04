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
package net.freehal.core.parser;

import net.freehal.core.util.FreehalFile;
import java.util.ArrayList;
import java.util.List;

import net.freehal.core.grammar.Entities;
import net.freehal.core.grammar.StandardGrammar;
import net.freehal.core.grammar.Grammars;
import net.freehal.core.pos.Taggers;
import net.freehal.core.pos.Tags;
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
	private String input = null;
	private boolean isQuestion = false;

	public Sentence(AbstractParser parser, final String input, boolean isQuestion) {
		this.isQuestion = isQuestion;
		this.input = input;
		String tmp = input;
		tmp = RegexUtils.replace(tmp, "[.!\\?\"']+", "");
		tmp = RegexUtils.trim(tmp, "-;,#+ ");
		String[] tmpList = tmp.split("\\s+");
		LogUtils.i("sentence: " + tmp);

		wordsList = new ArrayList<Word>();

		for (String word : tmpList) {
			Tags tags = Taggers.getTagger().getPartOfSpeech(word);
			wordsList.add(new Word(word, tags));
		}

		parse();
	}

	private void parse() {
		List<Entities> parsed = Grammars.getGrammar().parse(wordsList);
		final String xmlInput = StandardGrammar.printXml(parsed);
		LogUtils.d("parsed fact as xml:");
		LogUtils.d(xmlInput);
		final XmlStreamIterator xmlPre = new XmlUtils.XmlStreamIterator(new XmlUtils.OneStringIterator(
				xmlInput));
		final Sentence sentence = this;
		XmlUtils.readXmlFacts(xmlPre, null, new XmlFactReciever() {

			@Override
			public void useXmlFact(XmlFact xfact, int countFacts, long start, FreehalFile filename,
					int countFactsSoFar) {
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

	public String getInput() {
		return input;
	}

	public boolean isQuestion() {
		return isQuestion;
	}

	public void setQuestion(boolean isQuestion) {
		this.isQuestion = isQuestion;
	}
}
