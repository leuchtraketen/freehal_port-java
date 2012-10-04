package net.freehal.core.storage;

import net.freehal.core.util.FreehalFile;

public class Storages {

	private static Storage language = new IllegalStorage();

	public static Storage getStorage() {
		return language;
	}

	public static void setStorage(Storage language) {
		Storages.language = language;
	}

	private static class IllegalStorage implements Storage {
		@Override
		public FreehalFile getLanguageDirectory() {
			throw new UnsupportedOperationException();
		}

		@Override
		public FreehalFile getPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public FreehalFile getCacheDirectory() {
			throw new UnsupportedOperationException();
		}
	}
}
