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
package net.freehal.core.xml;

import java.io.File;

import net.freehal.core.filter.FactFilters;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Ranking;

public class XmlFact extends XmlList {

	private String line;
	private File filename;

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public File getFilename() {
		return filename;
	}

	public void setFilename(File filename) {
		this.filename = filename;
	}

	public double isLike(XmlFact other) {
		double matches = 0;

		XmlList verbs1 = this.part("verb");
		XmlList verbs2 = other.part("verb");

		double verbs_match = verbs1.isLike(verbs2);
		if (verbs_match == 0)
			return matches;

		XmlList subject1 = this.part("subject");
		XmlList subject2 = other.part("subject");
		XmlList object1 = this.part("object");
		XmlList object2 = other.part("object");
		XmlList adverbs1 = this.part("adverbs");
		XmlList adverbs2 = other.part("adverbs");

		double subject_match = subject1.isLike(subject2)
				+ subject1.isLike(object2) + subject1.isLike(adverbs2);
		matches += subject_match;

		double object_match = object1.isLike(subject2)
				+ object1.isLike(object2) + object1.isLike(adverbs2);
		matches += object_match;

		double adverbs_match = adverbs1.isLike(subject2)
				+ adverbs1.isLike(object2) + adverbs1.isLike(adverbs2);
		matches += adverbs_match;

		LogUtils.d("matches (before filterlist): " + matches);

		// fact filters!
		matches = FactFilters.getInstance().filter(this, other, matches);

		LogUtils.d("matches (after filterlist): " + matches);

		return matches;
	}

	public Ranking<XmlFact> ranking(Iterable<XmlFact> possibleAnswers) {
		Ranking<XmlFact> rank = new Ranking<XmlFact>();
		for (XmlFact other : possibleAnswers) {
			rank.insert(other, this.isLike(other));
		}
		return rank;
	}
}
