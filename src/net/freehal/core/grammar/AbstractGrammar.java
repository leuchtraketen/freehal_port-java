package net.freehal.core.grammar;

import java.util.List;

import net.freehal.core.grammar.typedefs.Entities;
import net.freehal.core.xml.Word;

public abstract class AbstractGrammar {

	public abstract List<Entities> parse(List<Word> words);
}
