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
package net.freehal.core.answer;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.parser.Sentence;

/**
 * An utility class for holding a list of currently used {@link AnswerProvider}
 * objects.
 * 
 * @author "Tobias Schulz"
 */
public class AnswerProviders {

	private static List<AnswerProvider> providers = new ArrayList<AnswerProvider>();

	private AnswerProviders() {}

	/**
	 * Add the given instance of {@link AnswerProvider} to the end of the list.
	 * 
	 * @param provider
	 *        the {@link AnswerProvider} to add
	 */
	public static void add(AnswerProvider provider) {
		if (provider != null)
			providers.add(provider);
	}

	/**
	 * Iterate over the list of {@link AnswerProvider}s to find an answer.
	 * 
	 * @param input
	 *        the input to find an answer for
	 * @return the first answer found, or {@code null} if no
	 *         {@link AnswerProvider} was able to find an answer.
	 */
	public static String getAnswer(Sentence input) {
		String answer = null;
		for (AnswerProvider a : providers) {
			if (answer == null) {
				answer = a.getAnswer(input);

				Runtime.getRuntime().gc();
			}
		}
		return answer;
	}
}
