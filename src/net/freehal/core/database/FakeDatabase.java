package net.freehal.core.database;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;

public class FakeDatabase implements DatabaseImpl {

	/**
	 * returns the question itself as answer
	 */
	@Override
	public List<XmlFact> findFacts(XmlFact xfact) {
		List<XmlFact> list = new ArrayList<XmlFact>();
		list.add(xfact);
		return list;
	}

	@Override
	public List<XmlFact> findFacts(List<Word> words) {
		return new ArrayList<XmlFact>();
	}

	@Override
	public List<XmlFact> findFacts(Word word) {
		return new ArrayList<XmlFact>();
	}

}
