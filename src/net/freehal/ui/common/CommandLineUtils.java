package net.freehal.ui.common;

import org.apache.commons.cli.CommandLine;

public class CommandLineUtils {

	public static String getStringOption(CommandLine line, String option, String defaultValue) {
		if (line.hasOption(option)) {
			return line.getOptionValue(option);
		} else {
			return defaultValue;
		}
	}

	public static boolean getBooleanOption(CommandLine line, String option, boolean defaultValue) {
		if (line.hasOption(option)) {
			final String value = line.getOptionValue(option).toLowerCase();
			if (value.contains("y") || value.contains("1"))
				return true;
			else
				return false;
		} else {
			return defaultValue;
		}
	}
}
