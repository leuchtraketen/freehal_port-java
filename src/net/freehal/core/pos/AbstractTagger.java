package net.freehal.core.pos;

public interface AbstractTagger {

	public Tags getPartOfSpeech(final String word);
	
	public boolean isName(final String word);

}
