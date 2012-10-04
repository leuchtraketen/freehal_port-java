package net.freehal.core.lang.german;

import net.freehal.core.lang.Language;

public class GermanLanguage implements Language {
	
	private static final String code = "de"; 

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public boolean isCode(String otherCode) {
		return code.equals(otherCode);
	}
}
