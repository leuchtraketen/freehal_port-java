package net.freehal.core.grammar;

public class Grammars {
	private static Grammar grammar = null;
	
	static {
		grammar = new FakeGrammar();
	}

	public static Grammar getGrammar() {
		return grammar;
	}

	public static void setGrammar(Grammar grammar) {
		Grammars.grammar = grammar;
	}
}
