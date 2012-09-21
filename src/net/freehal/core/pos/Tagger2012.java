package net.freehal.core.pos;

public class Tagger2012 implements AbstractTagger {

	@Override
	public Tags getPartOfSpeech(String word) {
		return new Tags("n", "", word);
	}

	@Override
	public boolean isName(String word) {
		return false;
	}

}
