package net.freehal.core.lang.english;

import net.freehal.core.parser.AbstractParser;

public class EnglishParser extends AbstractParser {

	public EnglishParser(String rawInput) {
		super(rawInput);
	}

	@Override
	protected String cleanInput(String str) {
		return str;
	}

	@Override
	protected String simplifyInput(String str) {
		return str;
	}

	@Override
	protected String extendInput(String str) {
		return str;
	}

}
