package net.freehal.ui.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import net.freehal.core.logs.StandardLogUtils;
import net.freehal.core.logs.receiver.ColoredLog;
import net.freehal.core.logs.receiver.LogRecorder;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.LogUtilsImpl;
import net.freehal.core.util.SystemUtils;

import org.apache.commons.cli.Options;

public class InteractiveShell {

	private enum Mode {
		LEARN, NOLEARN, DECIDE;
	}

	private static final Set<String> commandsList = new HashSet<String>(Arrays.asList(new String[] { "help",
			"input", "say", "learn", "log", "exit", "quit" }));

	private static final String PROMPT_INPUT = ColoredLog.ANSI.color(SystemUtils.getUsername(),
			ColoredLog.Color.GREEN, ColoredLog.Modifier.BOLD)
			+ ColoredLog.ANSI.color(" $ ", ColoredLog.Color.RED, ColoredLog.Modifier.BOLD);

	private static final String PROMPT_OUTPUT = ColoredLog.ANSI.color("freehal", ColoredLog.Color.BLUE,
			ColoredLog.Modifier.BOLD)
			+ ColoredLog.ANSI.color(" $ ", ColoredLog.Color.RED, ColoredLog.Modifier.BOLD);

	private static final String RECORDER_LOG_FILE = "recorded.logs";

	private LogRecorder logRecorder = null;

	/**
	 * Construct a new interactive shell.
	 */
	public InteractiveShell() {
		FreehalFile logfile = Storages.getCacheDirectory().getChild(RECORDER_LOG_FILE);
		logfile.delete();
		logfile.touch();
		logRecorder = new LogRecorder(logfile);
	}

	/**
	 * Run the shell loop.
	 * 
	 * @param options
	 *        the command line options
	 * @param in
	 *        the InputStream to read from (typically System.in)
	 * @param out
	 *        the PrintStream to write to (typically System.out)
	 * @throws IOException
	 *         when an IO error occurs
	 */
	public void loop(Options options, InputStream in, PrintStream out) throws IOException {
		// initialize data
		DataInitializer.initializeData(Collections.<String> emptySet());

		ConsoleReader reader = new ConsoleReader();
		reader.setBellEnabled(false);
		List<Completer> completors = new LinkedList<Completer>();

		completors.add(new StringsCompleter(commandsList));
		reader.addCompleter(new ArgumentCompleter(completors));

		out.println();

		String line;
		while ((line = readLine(reader)) != null) {
			processInteractiveInput(line, in, out);
			out.flush();
		}
	}

	private boolean isCommand(String cmd) {
		return commandsList.contains(cmd);
	}

	private void processInteractiveInput(String line, InputStream in, PrintStream out) {
		String[] params = line.split("\\s+", 2);

		if (params.length == 1) {
			String p1 = params[0];

			if ("".equals(p1)) {
				return;
			} else if ("exit".startsWith(p1) || "quit".startsWith(p1)) {
				SystemUtils.exit(0);
			} else if ("help".startsWith(p1)) {
				printShellHelp(out);
			} else if ("log".startsWith(p1)) {
				printLog();
			} else if (isCommand(p1)) {
				printShellInvalid(out);
			} else {
				processInteractiveInput(line, Mode.DECIDE, out);
			}
		} else if (params.length == 2) {
			String p1 = params[0];
			String p2 = params[1];

			if ("learn".startsWith(p1)) {
				processInteractiveInput(p2, Mode.LEARN, out);
			} else if ("say".startsWith(p1)) {
				processInteractiveInput(p2, Mode.NOLEARN, out);
			} else if ("input".startsWith(p1)) {
				processInteractiveInput(p2, Mode.DECIDE, out);
			} else if (isCommand(p1)) {
				printShellInvalid(out);
			} else {
				processInteractiveInput(line, Mode.DECIDE, out);
			}
		} else {
			// this should never happen
			printShellInvalid(out);
		}
	}

	private void printLog() {
		logRecorder.play();
	}

	private void printShellInvalid(PrintStream out) {
		out.println(ColoredLog.ANSI.color("Invalid command, " + "for assistance press TAB or "
				+ "type \"help\" and hit ENTER.", ColoredLog.Color.RED));
	}

	private void printShellHelp(PrintStream out) {
		out.println("List of commands:");
		out.println("");
		out.println("help -- Show this help message");
		out.println("exit | quit | <Ctrl>+D -- Exit Freehal");
		out.println("learn TEXT -- Freehal should try to learn the following sentence");
		out.println("say TEXT -- Freehal should answer to the following sentence (without learning it)");
		out.println("input TEXT -- Let Freehal decide whether to answer or to learn (or to do both) with the giben sentence");
		out.println("");
		out.println("Command name abbreviations are allowed if unambiguous.");
	}

	private String readLine(ConsoleReader reader) throws IOException {
		String line = reader.readLine(PROMPT_INPUT);
		return line != null ? line.trim() : null;
	}

	public void processInteractiveInput(String input, Mode mode, PrintStream out) {
		// back up the currently used logging streams
		LogUtilsImpl realLogging = LogUtils.get();

		// the following log output should be recorded for later use
		StandardLogUtils fakeLogging = new StandardLogUtils();
		fakeLogging.addDestination(logRecorder);
		LogUtils.set(fakeLogging);

		// process the given input text
		final String output = DataInitializer.processInput(input);
		out.println(PROMPT_OUTPUT + output);

		// restore the original logging streams
		LogUtils.set(realLogging);
	}
}
