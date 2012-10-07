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

public abstract class TagContainerMemory implements TagContainer {

	public boolean add(final FreehalFile filename) {

		LogUtils.i("read part of speech file: " + filename);

		String word = null;
		Tags tags = null;
		int n = 0;
		Iterable<String> lines = Storages.inLanguageDirectory(filename).readLines();
		for (String line : lines) {
			line = RegexUtils.trimRight(line, "\\s");

			if (line.endsWith(":")) {
				if (word != null && tags != null) {
					this.add(word, tags);
					word = null;
					tags = null;
				}

				line = RegexUtils.trim(line, ":\\s");
				word = line;
			} else if (line.startsWith(" ")) {
				line = RegexUtils.trim(line, ":,;\\s");
				if (line.startsWith("type")) {
					line = line.substring(4);
					line = RegexUtils.trimLeft(line, ":\\s");
					line = Tags.getUniqueCategory(line);
					tags = new Tags(tags, line, null, word);
				} else if (line.startsWith("genus")) {
					line = line.substring(5);
					line = RegexUtils.trimLeft(line, ":\\s");
					tags = new Tags(tags, null, line, word);
				}
			}

			if (++n % 10000 == 0) {
				LogUtils.i("\\r  " + n + " lines...          ");
				LogUtils.flush();
			}
		}
		if (word != null && tags != null) {
			this.add(word, tags);
		}

		LogUtils.i("\\r  " + n + " lines...          ");
		LogUtils.flush();

		return n > 0 ? true : false;
	}
}
