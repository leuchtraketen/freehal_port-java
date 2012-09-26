package net.freehal.core.database;

import java.io.File;
import java.util.List;
import java.util.Set;

import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;

public interface DatabaseImpl {

	public Set<XmlFact> findFacts(XmlFact xfact);

	public Set<XmlFact> findFacts(List<Word> words);

	public Set<XmlFact> findFacts(Word word);

	public void updateCache();

	public void updateCache(File filename);

}
