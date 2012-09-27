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

import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlList;

public class FilterQuestionExtra implements FactFilter {

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		LogUtils.d("matches (filter: question extra): " + match);

		if (f1.part("extra").countWords() > 0) {
			XmlList extra1 = f1.part("extra");
			XmlList subject2 = f2.part("subject");
			XmlList object2 = f2.part("object");
			XmlList adverbs2 = f2.part("adverbs");

			double _m = extra1.isLike(subject2) + extra1.isLike(object2)
					+ extra1.isLike(adverbs2);
			if (_m > 0)
				match += _m;
			else
				match *= 0.75;
			LogUtils.d("m = " + match + ", _m = " + _m);
		}

		return match;
	}
}
