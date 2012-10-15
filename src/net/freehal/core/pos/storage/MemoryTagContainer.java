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
package net.freehal.core.pos.storage;

import net.freehal.core.pos.Tags;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;

import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;

public abstract class MemoryTagContainer implements TagContainer {

	public boolean add(final FreehalFile filename) {

		LogUtils.i("read part of speech file: " + filename);

		int countOfLines = Storages.inLanguageDirectory(filename).countLines();
		LogUtils.startProgress(0, 1, countOfLines);
		LogUtils.updateProgress("read part of speech file");

		int n = 0;
		Iterable<String> lines = Storages.inLanguageDirectory(filename).readLines();
		for (String line : lines) {
			line = RegexUtils.trimRight(line, "\\s");

			LogUtils.updateProgress();

			String[] parts = StringUtils.splitEscaped(line, "|");
			if (parts.length == 3) {
				this.add(parts[0], new Tags(parts[1], parts[2]), filename);
			} else if (parts.length == 2) {
				this.add(parts[0], new Tags(parts[1], null), filename);
			}

			if (++n % 10000 == 0) {
				LogUtils.i("\\r  " + n + " lines...          ");
				LogUtils.flush();
			}
		}

		LogUtils.i("\\r  " + n + " lines...          ");
		LogUtils.flush();
		LogUtils.stopProgress();

		return n > 0 ? true : false;
	}

	protected abstract void add(String word, Tags tags, FreehalFile from);
}
