package net.freehal.core.pos;


public class Taggers {
	
	private static Tagger tagger = null;

	static {
		tagger = new FakeTagger();
	}

	public static Tagger getTagger() {
		return tagger;
	}

	public static void setTagger(Tagger tagger) {
		Taggers.tagger = tagger;
	}
}
