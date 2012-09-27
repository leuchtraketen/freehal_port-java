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
package net.freehal.core.cache;

import java.io.File;

import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.xml.Word;

public class DiskStorage {

	public static File getFile(final String type1, final String type2,
			final Key key, final File filename) {
		if (filename != null)
			return new File(getDirectory(type1, type2, key), filename.getPath());
		else
			return getDirectory(type1, type2, key);
	}

	public static File getDirectory(final String type1, final String type2,
			final Key key) {
		StringBuilder keyPath = new StringBuilder();
		keyPath.append(key.getKey(0)).append("/").append(key.getKey(1))
				.append("/").append(key.getKey(2)).append("/")
				.append(key.getKey(3));

		File directory = new File(getDirectory(type1, type2),
				keyPath.toString());
		directory.mkdirs();

		return directory;
	}

	public static File getDirectory(final String type1, final String type2) {
		File directory = new File(FreehalConfig.getCacheDirectory(), type1
				+ "/" + type2);
		directory.mkdirs();
		return directory;
	}

	public static class Key {

		private Word word;
		private String key;
		private int keysize = 4;

		public Key(Word word) {
			this.word = word;
			init();
		}

		public Key(String word) {
			this.word = new Word(word, null);
			init();
		}

		public Key(Word word, int keysize) {
			this.word = word;
			this.keysize = keysize;
			init();
		}

		public Key(String word, int keysize) {
			this.word = new Word(word, null);
			this.keysize = keysize;
			init();
		}

		private void init() {
			final String onlyChars = RegexUtils.replace(word.getWord()
					.toLowerCase(), "[^a-zA-Z0-9]", "");
			if (onlyChars.length() >= keysize)
				key = onlyChars.substring(0, keysize);
			else
				key = onlyChars;
			while (key.length() < keysize)
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
			if (index >= keysize || index < 0)
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
