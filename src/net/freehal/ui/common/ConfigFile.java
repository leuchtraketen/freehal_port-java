package net.freehal.ui.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;

public class ConfigFile implements Configuration.ConfigSource {

	public static String[] COMMON_LOCATIONS;

	static {
		COMMON_LOCATIONS = new String[] {
				System.getProperty("user.home") + File.separator + ".freehalrc", "/etc/freehalrc" };
	}

	private Map<String, String> options = new HashMap<String, String>();

	public ConfigFile(String[] locations) {
		for (String location : locations) {
			readOptions(new FreehalFile(location));
		}
	}

	private void readOptions(FreehalFile path) {
		LogUtils.d("searching config file: " + path);
		if (path.isDirectory()) {
			for (FreehalFile child : path.listFiles()) {
				readOptions(child);
			}
		} else if (path.isFile()) {
			String name = path.getName();
			if (name.contains("freehalrc") || name.endsWith("ini") || name.startsWith(".")) {
				readOptions(path.readLines());
			}
		}
	}

	private void readOptions(Iterable<String> lines) {
		for (String line : lines) {
			if (line.contains("=")) {
				String[] parts = line.split("=", 2);
				if (parts.length >= 2) {
					readOptions(parts[0].trim(), parts[1].trim());
				}
			}
		}
	}

	private void readOptions(String name, String value) {
		name = strip(name);
		options.put(name, value);
	}

	private String strip(String text) {
		text = RegexUtils.replace(text, "[^a-zA-Z0-9]", "");
		return text;
	}

	@Override
	public boolean hasOption(String name) {
		return options.containsKey(strip(name));
	}

	@Override
	public String getOptionValue(String name) {
		return options.get(strip(name));
	}
}
