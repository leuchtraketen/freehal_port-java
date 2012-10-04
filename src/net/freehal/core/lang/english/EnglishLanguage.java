package net.freehal.core.lang.english;

import net.freehal.core.lang.Language;

public class EnglishLanguage implements Language {
	
	private static final String code = "en"; 

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public boolean isCode(String otherCode) {
		return code.equals(otherCode);
	}
}
