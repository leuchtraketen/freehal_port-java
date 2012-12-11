package net.freehal.compat.sunjava.logging;

import java.io.PrintStream;

import net.freehal.compat.sunjava.logging.StandardLogUtils.LogStream;
import net.freehal.compat.sunjava.logging.StandardLogUtils.PrintStreamLogStream;
import net.freehal.compat.sunjava.logging.StandardLogUtils.StackTraceUtils;
import net.freehal.core.util.RegexUtils;

public class ConsoleLogStream extends PrintStreamLogStream {

	public static LogStream create(PrintStream out) {
		return new ConsoleLogStream(out);
	}

	public ConsoleLogStream(PrintStream out) {
		super(out);
	}

	public void print(String e) {
		super.print(e);
	}

	@Override
	public void add(String type, String line, StackTraceElement stacktrace) {
		final String prefix = StackTraceUtils.whereInCode(stacktrace) + "(" + type + ") "
				+ (type.length() == 4 ? " " : "");

		// use carriage return for output
		if (line.contains("\\r")) {
			line = RegexUtils.replace(line, "\\\\r", "\r" + prefix);
			print(prefix + line + "\r");
		} else {
			println(prefix + line);
		}
	}
}