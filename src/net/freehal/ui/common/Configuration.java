package net.freehal.ui.common;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.LogUtils;
import net.freehal.core.util.SystemUtils;

import org.apache.commons.cli.CommandLine;

public class Configuration {

	private List<ConfigSource> sources = new ArrayList<ConfigSource>();

	public void addSource(CommandLine line) {
		sources.add(new CommandLineConfigAdapter(line));
	}

	public void addSource(ConfigSource configFile) {
		sources.add(configFile);
	}

	public boolean hasOption(String name) {
		for (ConfigSource source : sources) {
			if (source.hasOption(name)) {
				return true;
			}
		}
		return false;
	}

	public String getStringOption(String name, String defaultValue) {
		for (ConfigSource source : sources) {
			if (source.hasOption(name)) {
				return source.getOptionValue(name);
			}
		}
		return defaultValue;
	}

	public boolean getBooleanOption(String name, boolean defaultValue) {
		for (ConfigSource source : sources) {
			if (source.hasOption(name)) {
				final String value = source.getOptionValue(name).toLowerCase();
				if (value.contains("y") || value.contains("1") || value.contains("true")
						|| value.contains("on"))
					return true;
				else
					return false;
			}
		}
		return defaultValue;
	}

	public int getIntegerOption(String name, int defaultValue) {
		for (ConfigSource source : sources) {
			if (source.hasOption(name)) {
				try {
					return Integer.parseInt(source.getOptionValue(name));
				} catch (NumberFormatException ex) {
					LogUtils.e("Illegal option value: " + name + " is not a number!");
					SystemUtils.exit(1);
					return defaultValue;
				}
			}
		}
		return defaultValue;
	}

	public interface ConfigSource {

		boolean hasOption(String name);

		String getOptionValue(String name);
	}

	public class CommandLineConfigAdapter implements ConfigSource {

		private CommandLine line;

		public CommandLineConfigAdapter(CommandLine line) {
			this.line = line;
		}

		@Override
		public boolean hasOption(String name) {
			return line.hasOption(name);
		}

		@Override
		public String getOptionValue(String name) {
			return line.getOptionValue(name);
		}
	}
}
