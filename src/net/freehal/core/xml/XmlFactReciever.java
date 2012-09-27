package net.freehal.core.xml;

import java.io.File;

public interface XmlFactReciever {

	public void useXmlFact(XmlFact xfact, int countFacts,
			long start, File filename, int countFactsSoFar);

}
