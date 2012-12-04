/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.database;

import java.util.Set;

import net.freehal.core.answer.AnswerProvider;
import net.freehal.core.parser.Sentence;
import net.freehal.core.pos.TaggerUtils;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Ranking;
import net.freehal.core.wording.Wordings;
import net.freehal.core.xml.FactProvider;
import net.freehal.core.xml.XmlFact;

public class DatabaseAnswerProvider implements AnswerProvider {

	private FactProvider database;

	public DatabaseAnswerProvider(FactProvider database) {
		this.database = database;
	}

	@Override
	public String getAnswer(Sentence s) {
		if (!s.isValidFact()) {
			return null;
		}

		XmlFact input = s.getFact();
		Set<XmlFact> possibleAnswers = database.findFacts(TaggerUtils.getIndexWords(input));
		Ranking<XmlFact> rank = input.ranking(possibleAnswers);
		for (int i = 0; i < rank.size(); ++i) {
			for (XmlFact xfact : rank.get(i)) {
				LogUtils.i(rank.rank(i) + ": " + xfact.printStr());
			}
		}
		XmlFact best = rank.getBestOne();

		if (best != null)
			return Wordings.getWording().phrase(best);
		else
			return null;
	}

}
