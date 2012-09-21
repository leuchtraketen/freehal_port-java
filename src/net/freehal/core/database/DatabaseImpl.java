package net.freehal.core.database;

import java.util.List;

import net.freehal.core.xml.XmlFact;

public interface DatabaseImpl {

	public List<XmlFact> findFacts(XmlFact xfact);

}
