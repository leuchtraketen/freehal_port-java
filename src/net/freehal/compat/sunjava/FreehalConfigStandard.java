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
