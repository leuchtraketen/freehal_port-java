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

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	@Override
	public File getLanguageDirectory() {
		return new File(path, "lang_" + language + "/");
	}

}
