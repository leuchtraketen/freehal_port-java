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
		final String xmlPre = XmlUtils.orderTags(xmlInput);
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
