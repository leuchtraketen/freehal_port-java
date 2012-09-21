package net.freehal.core.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParser {

	private List<Sentence> sentences = new ArrayList<Sentence>();

	public AbstractParser(String rawInput) {
		String[] splittedInput = cleanInput(rawInput).split("[@]+");
		for (String i : splittedInput) {
			i = extendInput(simplifyInput(i));
			sentences.add(new Sentence(this, i));
		}
	}

	abstract protected String cleanInput(String str);

	abstract protected String simplifyInput(String str);

	abstract protected String extendInput(String str);

	public List<Sentence> getSentences() {
		return sentences;
	}

}
