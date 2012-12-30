/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.ui.shell;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import net.freehal.core.lang.Language;
import net.freehal.core.lang.english.EnglishLanguage;
import net.freehal.core.lang.german.GermanLanguage;
import net.freehal.core.util.ArrayUtils;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.SystemUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This class is a reference implementation of a simple console user interface.
 * It uses all main APIs and runs on every normal java platform.
 * 
 * @author "Tobias Schulz"
 */
public class Main {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("h", "help", false, "display this help and exit");
		options.addOption("v", "version", false, "output version information and exit");

		options.addOption(OptionBuilder.withLongOpt("base")
				.withDescription("the base directory for language data and caches").hasArg()
				.withArgName("DIRECTORY").create("b"));
		options.addOption(OptionBuilder.withLongOpt("input")
				.withDescription("a statement or question to answer").hasArg().withArgName("TEXT")
				.create("i"));
		options.addOption(OptionBuilder.withLongOpt("shell")
				.withDescription("opens an interactive shell for answering").create("s"));
		options.addOption(OptionBuilder.withLongOpt("language")
				.withDescription("the natual language TEXT is written in").hasArg().withArgName("LANG")
				.create("l"));
		options.addOption(OptionBuilder.withLongOpt("log-file").withDescription("write logs to a file")
				.hasArg().withArgName("FILE").create());
		options.addOption(OptionBuilder
				.withLongOpt("log-window")
				.withDescription(
						"display a window to show logs " + "(default on Microsoft Windows and Mac OS X)")
				.hasArg().withArgName("FILE").create());
		options.addOption(OptionBuilder.withLongOpt("log-terminal")
				.withDescription("display logs in terminal " + "(default on Linux and Unix)").hasArg()
				.withArgName("FILE").create());
		options.addOption("t", "think", false, "do some reasoning processes");

		if (args.length == 0) {
			printHelp(options);

		} else {
			// create the parser
			CommandLineParser parser = new GnuParser();
			try {
				// parse command line arguments
				CommandLine line = parser.parse(options, args);
				parse(line, options);

			} catch (ParseException exp) {
				System.err.println(exp.getMessage());
				SystemUtils.exit(1);
			}
		}

		SystemUtils.exit(0);
	}

	private static String getStringOption(CommandLine line, String option, String defaultValue) {
		if (line.hasOption(option)) {
			return line.getOptionValue(option);
		} else {
			return defaultValue;
		}
	}

	private static boolean getBooleanOption(CommandLine line, String option, boolean defaultValue) {
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

	private static void parse(CommandLine line, Options options) {
		if (line.hasOption("help"))
			printHelp(options);

		// default base directory
		final String base = getStringOption(line, "base", ".");
		// default log file
		final String logfile = getStringOption(line, "log-file", base + "/stdout.txt");
		// show a swing log window? it's default on windows systems...
		final boolean showLogWindow = getBooleanOption(line, "log-window", SystemUtils.isWindows()
				|| SystemUtils.isMacOSX());
		// show logs in terminal? it's default on unix systems...
		final boolean showLogTerminal = getBooleanOption(line, "log-terminal", !SystemUtils.isWindows()
				&& !SystemUtils.isMacOSX());

		DataInitializer.initializeLogging(base, logfile, showLogTerminal, showLogWindow);
		DataInitializer.initializeFilesystem(base);

		Language language = new GermanLanguage(); // default language
		if (line.hasOption("language")) {
			final String langCode = line.getOptionValue("language").toLowerCase();
			if (langCode.equals("de") || langCode.equals("german") || langCode.equals("deutsch")) {
				language = new GermanLanguage();
			} else if (langCode.equals("en") || langCode.equals("english")
					|| langCode.equals("international")) {
				language = new EnglishLanguage();
			}
		}
		DataInitializer.initializeLanguage(language);

		if (line.hasOption("input")) {
			String[] args = { line.getOptionValue("input") };
			processNonInteractiveInput(args);
		}

		if (line.hasOption("shell")) {
			try {
				InteractiveShell shell = new InteractiveShell();
				shell.loop(options, System.in, System.out);
			} catch (IOException ex) {
				LogUtils.e(ex);
			}
		}

		if (line.hasOption("think")) {
			think();
		}
	}

	private static void printHelp(Options options) {
		final String header = "FreeHAL is a self-learning conversation simulator, "
				+ "an artificial intelligence " + "which uses semantic nets to organize its knowledge.";
		final String footer = "Please report bugs to <info@freehal.net>.";
		final int width = 80;
		final int descPadding = 10;
		final PrintWriter out = new PrintWriter(System.out, true);

		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(width);
		formatter.setDescPadding(descPadding);
		formatter.printUsage(out, width, "java " + Main.class.getName(), options);
		formatter.printWrapped(out, width, header);
		formatter.printWrapped(out, width, "");
		formatter.printOptions(out, width, options, formatter.getLeftPadding(), formatter.getDescPadding());
		formatter.printWrapped(out, width, "");
		formatter.printWrapped(out, width, footer);
	}

	public static void processNonInteractiveInput(String[] sentences) {
		// initialize data
		DataInitializer.initializeData(Collections.<String> emptySet());

		for (String input : sentences) {
			final String output = DataInitializer.processInput(input);
			System.out.println("Input: " + input);
			System.out.println("Output: " + output);
		}
	}

	public static void think() {
		DataInitializer.initializeData(ArrayUtils.asSet(new String[] { "reasoning" }));
	}
}
