package net.freehal.core.storage;

import java.io.File;

import net.freehal.core.lang.Languages;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;

public class StandardStorage implements Storage {
	private FreehalFile path;

	public StandardStorage(File path) {
		this.path = FreehalFiles.create(path.getPath());
	}

	public StandardStorage(FreehalFile path) {
		this.path = path;
	}

	public StandardStorage(String path) {
		this.path = FreehalFiles.create(path);
	}

	@Override
	public FreehalFile getPath() {
		return path;
	}

	@Override
	public FreehalFile getLanguageDirectory() {
		return FreehalFiles.create(path.getAbsolutePath(), "lang_" + Languages.getLanguage().getCode() + "/");
	}

	@Override
	public FreehalFile getCacheDirectory() {
		return FreehalFiles
				.create(path.getAbsolutePath(), "cache_" + Languages.getLanguage().getCode() + "/");
	}
}
