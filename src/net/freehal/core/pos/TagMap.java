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
package net.freehal.core.pos;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

public class TagMap extends HashMap<String, Tags> implements TagContainer {

	private static final long serialVersionUID = 1519345148659396083L;

	@Override
	public void add(String word, Tags tags) {
		put(word, tags);
	}

	@Override
	public boolean containsKey(String word) {
		return super.containsKey(word);
	}

	@Override
	public Iterator<java.util.Map.Entry<String, Tags>> iterator() {
		return super.entrySet().iterator();
	}

	@Override
	public Tags get(String word) {
		return super.get(word);
	}

	@Override
	public void add(File filename) {
		// ignore
	}
}
