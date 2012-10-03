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
package net.freehal.core.util;

import net.freehal.core.util.FreehalFile;

public class NoConfiguration implements FreehalConfigImpl {

	final String msgNoConfig = "You need to set a class which "
			+ "implements FreehalConfigImpl as global configuration "
			+ "with FreehalConfig.set(...) before using the Freehal API!";

	@Override
	public FreehalFile getLanguageDirectory() {
		throw new NoConfigurationException();
	}

	@Override
	public String getLanguage() {
		throw new NoConfigurationException();
	}

	@Override
	public FreehalFile getPath() {
		throw new NoConfigurationException();
	}

	@Override
	public FreehalFile getCacheDirectory() {
		throw new NoConfigurationException();
	}

}
