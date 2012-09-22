package net.freehal.core.pos;

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

}
