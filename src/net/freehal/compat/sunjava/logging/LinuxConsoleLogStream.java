package net.freehal.compat.sunjava.logging;

import java.io.PrintStream;

import net.freehal.compat.sunjava.logging.StandardLogUtils.LogStream;
import net.freehal.compat.sunjava.logging.StandardLogUtils.StackTraceUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;

public class LinuxConsoleLogStream extends ConsoleLogStream {
	public static final String ANSI = "\u001B[";
	public static final String RESET = "0m";
	public static final String BLACK = "30m";
	public static final String RED = "31m";
	public static final String GREEN = "32m";
	public static final String YELLOW = "33m";
	public static final String BLUE = "34m";
	public static final String PURPLE = "35m";
	public static final String CYAN = "36m";
	public static final String WHITE = "37m";
	public static final String BOLD_ON = "1;";

	public static LogStream create(PrintStream out) {
		return new LinuxConsoleLogStream(out);
	}

	public LinuxConsoleLogStream(PrintStream out) {
		super(out);
	}

	private String typeToColor(String type, boolean bold) {
		String color = ANSI + RESET + ANSI;
		if (bold)
			color += BOLD_ON;
		if (type.equals("error"))
			color += RED;
		else if (type.equals("info"))
			color += GREEN;
		else if (type.equals("debug"))
			color += BLUE;
		else if (type.equals("warn"))
			color += YELLOW;
		else if (type.equals("info"))
			color += GREEN;
		return color;
	}

	private String formatLine(String line) {
		line = RegexUtils.replace(line, "([\"][^\"]+[\"])", "ANSI" + YELLOW + "$1" + "ANSI" + RESET);
		line = RegexUtils.replace(line, "([\'][^\']+[\'])", "ANSI" + BLUE + "$1" + "ANSI" + RESET);
		line = RegexUtils.replace(line, "ANSI", ANSI);
		return line;
	}

	@Override
	public void add(String type, String line, StackTraceElement stacktrace) {

		final String prefix = StringUtils.replace(StackTraceUtils.whereInCode(stacktrace), ":", ":")
				+ typeToColor(type, true) + "[" + type + "] " + ANSI + RESET + (type.length() == 4 ? " " : "");

		if (type.equals("error") || type.equals("warn"))
			line = ANSI + BOLD_ON + RED + line + ANSI + RESET;
		else
			line = formatLine(line);
		// use carriage return for output
		if (line.contains("\\r")) {
			line = RegexUtils.replace(line, "\\\\r", "\r" + prefix);
			print(prefix + line + "\r");
		} else {
			println(prefix + line);
		}
	}
}