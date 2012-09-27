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
package net.freehal.core.answer;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.parser.Sentence;

public class AnswerProviders implements AnswerProvider {

	private static AnswerProviders singleton = new AnswerProviders();

	private List<AnswerProvider> providers = null;

	private AnswerProviders() {
		providers = new ArrayList<AnswerProvider>();
	}

	public static AnswerProviders getInstance() {
		return singleton;
	}

	public AnswerProviders add(AnswerProvider filter) {
		if (providers == null) {
			providers = new ArrayList<AnswerProvider>();
		}
		providers.add(filter);
		return this;
	}

	@Override
	public String getAnswer(Sentence s) {
		String answer = null;
		for (AnswerProvider a : providers) {
			if (answer == null) {
				answer = a.getAnswer(s);
				
				Runtime.getRuntime().gc();
			}
		}
		return answer;
	}
}
