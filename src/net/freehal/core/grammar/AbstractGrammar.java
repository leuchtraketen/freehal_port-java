package net.freehal.core.grammar;

import java.io.File;
import java.util.List;

import net.freehal.core.typedefs.Entities;
import net.freehal.core.xml.Word;

public abstract class AbstractGrammar {

	public abstract List<Entities> parse(List<Word> words);

	public abstract boolean readGrammar(File file);
}
