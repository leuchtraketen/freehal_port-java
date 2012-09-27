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

import net.freehal.core.pos.Tags;
import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlText;

public class FilterQuestionWho implements FactFilter {

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		LogUtils.d("matches (filter: question who): " + match);

		if (f1.part("questionword").matches(XmlText.fromText("who")) == 1) {
			if (f2.part("clause").countWords() > 0) {
				match *= 0.75;
			}
			if (f2.part("adverbs").countWords() > 0) {
				match *= 0.3;
			}
			if (f2.part("object").countWords() > 0) {
				if (f2.part("object").matches(XmlText.fromText("(a)")) > 0) {
					match *= 0.5;
				} else if (f2.part("object").countTags(new Tags("n", "")) == 0) {
					match *= 0.3;
				}
			} else {
				match *= 0.3;
			}
		}

		return match;
	}
}
