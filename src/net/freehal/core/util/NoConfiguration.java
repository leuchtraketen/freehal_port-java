package net.freehal.core.util;

import java.io.File;

public class NoConfiguration implements FreehalConfigImpl {

	final String msgNoConfig = "You need to set a class which "
			+ "implements FreehalConfigImpl as global configuration "
			+ "with FreehalConfig.set(...) before using the Freehal API!";

	@Override
	public File getLanguageDirectory() {
		throw new NoConfigurationException();
	}

	@Override
	public String getLanguage() {
		throw new NoConfigurationException();
	}

	@Override
	public File getPath() {
		throw new NoConfigurationException();
	}

	@Override
	public File getCacheDirectory() {
		throw new NoConfigurationException();
	}

}
