package net.freehal.core.storage;

import net.freehal.core.util.FreehalFile;

public interface Storage {

	public FreehalFile getLanguageDirectory();

	public FreehalFile getPath();

	public FreehalFile getCacheDirectory();
}
