package net.freehal.core.filter;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.xml.XmlFact;

public class FactFilters implements FactFilter {
	
	private static FactFilters singleton = new FactFilters();
	
	private List<FactFilter> factfilters = null;
	
	private FactFilters() {
		factfilters = new ArrayList<FactFilter>();
	}
	
	public static FactFilters getInstance() {
		return singleton;
	}
	
	public FactFilters add(FactFilter filter) {
		if (factfilters == null) {
			factfilters = new ArrayList<FactFilter>();
		}
		factfilters.add(filter);
		return this;
	}

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		for (FactFilter filter : factfilters) {
			match = filter.filter(f1, f2, match);
		}
		return match;
	}
}
