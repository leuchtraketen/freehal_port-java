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
package net.freehal.core.lang;

/**
 * An utility class for holding the currently used language.
 * 
 * @author "Tobias Schulz"
 */
public class Languages {

	private static Language language = new IllegalLanguage();

	private Languages() {}

	/**
	 * Returns the current language.
	 * 
	 * @return an instance of {@link Language}
	 */
	public static Language getCurrentLanguage() {
		return language;
	}

	/**
	 * Set the current language.
	 * 
	 * @param language
	 *        the language to set
	 */
	public static void setLanguage(Language language) {
		Languages.language = language;
	}

	private static class IllegalLanguage implements Language {
		@Override
		public String getCode() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isCode(String otherCode) {
			throw new UnsupportedOperationException();
		}
	}
}
