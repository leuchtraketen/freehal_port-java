/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.filter;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.LogUtils;
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
		LogUtils.d( "fact filter: compare " + f1 + " and " + f2);
		for (FactFilter filter : factfilters) {
			match = filter.filter(f1, f2, match);
		}
		return match;
	}
}
