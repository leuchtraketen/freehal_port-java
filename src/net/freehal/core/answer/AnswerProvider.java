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

import net.freehal.core.parser.Sentence;

/**
 * An interface for all classes which can return an answer to a given input
 * sentence.
 * 
 * @author "Tobias Schulz"
 */
public interface AnswerProvider {

	/**
	 * Returns an answer for the given input sentence.
	 * 
	 * @param s
	 *        the sentence to find an answer for
	 * @return the answer as a string
	 */
	public String getAnswer(Sentence s);

}
