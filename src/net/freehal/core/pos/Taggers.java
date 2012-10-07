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
package net.freehal.core.pos;

/**
 * An utility class for holding the currently used tagger.
 * 
 * @author "Tobias Schulz"
 */
public class Taggers {

	private static Tagger tagger = null;

	static {
		tagger = new FakeTagger();
	}

	private Taggers() {}

	/**
	 * Get the currently used tagger.
	 * 
	 * @return an instance of a class implementing the {@link Tagger} interface
	 */
	public static Tagger getTagger() {
		return tagger;
	}

	/**
	 * Set the tagger to use; run at the beginning of your code!
	 * 
	 * @param tagger
	 *        the tagger to set
	 */
	public static void setTagger(Tagger tagger) {
		Taggers.tagger = tagger;
	}
}
