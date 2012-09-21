package net.freehal.core.util;

import java.io.File;

public class FreehalConfig {

	private static FreehalConfigImpl instance = null;

	public static void set(FreehalConfigImpl instance) {
		FreehalConfig.instance = instance;
	}

	public static File getLanguageDirectory() {
		return instance.getLanguageDirectory();
	}
}
