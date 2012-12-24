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
package net.freehal.core.parser;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.Mutable;

public abstract class Parser {

	private List<Sentence> sentences = new ArrayList<Sentence>();

	public void parse(String rawInput) {
		String[] splittedInput = cleanInput(rawInput).split("[@]+");
		for (String i : splittedInput) {
			Mutable<Boolean> isQuestion = new Mutable<Boolean>(false);
			i = extendInput(simplifyInput(i, isQuestion));
			if (i != null) {
				sentences.add(new Sentence(this, i, isQuestion.get()));
			}
		}
	}

	abstract protected String cleanInput(String str);

	abstract protected String simplifyInput(String str,
			Mutable<Boolean> isQuestion);

	abstract protected String extendInput(String str);

	public List<Sentence> getSentences() {
		return sentences;
	}
}
