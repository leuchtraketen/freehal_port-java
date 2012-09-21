package net.freehal.core.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.freehal.core.database.DatabaseImpl;
import net.freehal.core.grammar.Grammar2012;
import net.freehal.core.grammar.typedefs.Entities;
import net.freehal.core.pos.Tags;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlUtils;

public class Sentence {

	private List<Word> wordsList = null;
	private XmlFact xfact = null;

	public Sentence(AbstractParser parser, String input) {
		String tmp = input;
		tmp = RegexUtils.replace(tmp, "[.!?\"']", "");
		tmp = RegexUtils.trim(tmp, "-;,#+ ");
		String[] tmpList = tmp.split("\\s+");

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
		final String xmlPre = XmlUtils.orderTags(xmlInput);
		final Sentence sentence = this;
		XmlUtils.readXmlFacts(null, xmlPre, null, new XmlFactReciever() {

			@Override
			public void useXmlFact(DatabaseImpl d, XmlFact xfact,
					int countFacts, long start, File filename,
					int countFactsSoFar) {
				sentence.setFact(xfact);
			}
			
		});
	}

	protected void setFact(XmlFact xfact) {
		this.xfact = xfact;
	}
}
