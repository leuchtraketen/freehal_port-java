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
package net.freehal.compat.sunjava;

import java.io.File;

import net.freehal.core.util.FreehalConfigImpl;

public class FreehalConfigStandard implements FreehalConfigImpl {

	private String language;
	private File path;

	@Override
	public String getLanguage() {
		return language;
	}

	public FreehalConfigStandard setLanguage(String language) {
		this.language = language;
		return this;
	}

	@Override
	public File getPath() {
		return path;
	}

	public FreehalConfigStandard setPath(File path) {
		this.path = path;
		return this;
	}

	@Override
	public File getLanguageDirectory() {
		return new File(path, "lang_" + language + "/").getAbsoluteFile();
	}

	@Override
	public File getCacheDirectory() {
		return new File(path, "cache_" + language + "/").getAbsoluteFile();
	}

}
