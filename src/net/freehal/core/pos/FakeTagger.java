package net.freehal.core.pos;

import java.io.File;

import net.freehal.core.xml.Word;


public class FakeTagger implements AbstractTagger {

	@Override
	public Tags getPartOfSpeech(String word) {
		if (word.equals("is"))
			return new Tags("v", "", word);
		else if (word.equals("cool"))
			return new Tags("adj", "", word);
		else
			return new Tags("n", "", word);
	}

	@Override
	public boolean isName(String word) {
		return false;
	}

	@Override
	public void readTagsFrom(File filename) {
	}

	@Override
	public void readRegexFrom(File filename) {
	}

	@Override
	public Word toggle(Word word) {
		return word;
	}

	@Override
	public void readToggleWordsFrom(File filename) {
	}

}
