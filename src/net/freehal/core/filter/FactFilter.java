package net.freehal.core.filter;

import net.freehal.core.xml.XmlFact;

public interface FactFilter {
	public double filter(XmlFact f1, XmlFact f2, double match);
}
