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
package net.freehal.compat.sunjava;

import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;

import net.freehal.core.util.FreehalConfigImpl;

public class FreehalConfigStandard implements FreehalConfigImpl {

	private String language;
	private FreehalFile path;

	@Override
	public String getLanguage() {
		return language;
	}

	public FreehalConfigStandard setLanguage(String language) {
		this.language = language;
		return this;
	}

	@Override
	public FreehalFile getPath() {
		return path;
	}

	public FreehalConfigStandard setPath(FreehalFile path) {
		this.path = path;
		return this;
	}

	@Override
	public FreehalFile getLanguageDirectory() {
		return FreehalFiles.create(path.getAbsolutePath(), "lang_" + language + "/");
	}

	@Override
	public FreehalFile getCacheDirectory() {
		return FreehalFiles.create(path.getAbsolutePath(), "cache_" + language + "/");
	}

}
