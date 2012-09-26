package net.freehal.core.pos;

import java.io.File;

import net.freehal.core.xml.Word;

public interface AbstractTagger {

	public Tags getPartOfSpeech(final String word);
	
	public boolean isName(final String word);

	public void readTagsFrom(File filename);

	public void readRegexFrom(File filename);

	public Word toggle(Word word);

	public void readToggleWordsFrom(File filename);
}
