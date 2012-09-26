package net.freehal.core.database;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;

public class FakeDatabase implements DatabaseImpl {

	/**
	 * returns the question itself as answer
	 */
	@Override
	public Set<XmlFact> findFacts(XmlFact xfact) {
		Set<XmlFact> list = new HashSet<XmlFact>();
		list.add(xfact);
		return list;
	}

	/**
	 * returns the question itself as answer
	 */
	@Override
	public Set<XmlFact> findFacts(List<Word> words) {
		return new HashSet<XmlFact>();
	}

	/**
	 * returns the question itself as answer
	 */
	@Override
	public Set<XmlFact> findFacts(Word word) {
		return new HashSet<XmlFact>();
	}

	/**
	 * a fake database doesn't need a cache...
	 */
	@Override
	public void updateCache() {
	}

	/**
	 * a fake database doesn't need a cache...
	 */
	@Override
	public void updateCache(File filename) {
	}

}
