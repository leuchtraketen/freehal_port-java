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
package net.freehal.core.database;

import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.xml.Word;

public class DiskStorage {

	public static FreehalFile getCacheFile(final String dir1, final String dir2, final Key key,
			final FreehalFile filename) {
		if (filename != null)
			return getCacheDirectory(dir1, dir2, key).getChild(filename);
		else
			return getCacheDirectory(dir1, dir2, key);
	}

	public static FreehalFile getCacheDirectory(final String dir1, final String dir2, final Key key) {
		StringBuilder keyPath = new StringBuilder();
		keyPath.append(key.getKey(0)).append("/").append(key.getKey(1)).append("/").append(key.getKey(2))
				.append("/").append(key.getKey(3));

		FreehalFile directory = getCacheDirectory(dir1, dir2).getChild(keyPath.toString());
		directory.mkdirs();

		return directory;
	}

	public static FreehalFile getCacheDirectory(final String dir1, final String dir2) {
		FreehalFile directory = Storages.inCacheDirectory(dir1 + "/" + dir2);
		directory.mkdirs();
		return directory;
	}

	public static class Key {

		private Word word;
		private String key;
		private static int globalKeyLength = 4;
		private int keylength = globalKeyLength;

		public static void setGlobalKeyLength(int globalKeyLength) {
			Key.globalKeyLength = globalKeyLength;
		}

		public Key(Word word) {
			this.word = word;
			init();
		}

		public Key(String word) {
			this.word = new Word(word, null);
			init();
		}

		public Key(Word word, int keylength) {
			this.word = word;
			this.keylength = keylength;
			init();
		}

		public Key(String word, int keylength) {
			this.word = new Word(word, null);
			this.keylength = keylength;
			init();
		}

		private void init() {
			final String onlyChars = RegexUtils.replace(word.getWord().toLowerCase(), "[^a-zA-Z0-9]", "");
			if (onlyChars.length() >= keylength)
				key = onlyChars.substring(0, keylength);
			else
				key = onlyChars;
			while (key.length() < keylength)
				key += "_";
		}

		public Word getWord() {
			return word;
		}

		public void setWord(Word word) {
			this.word = word;
		}

		public String getKey() {
			return key;
		}

		public char getKey(int index) {
			if (index >= keylength || index < 0)
				return '_';
			else
				return key.charAt(index);
		}

		public void setKey(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return "{key=\"" + key + "\",word=" + word + "}";
		}
	}
}
