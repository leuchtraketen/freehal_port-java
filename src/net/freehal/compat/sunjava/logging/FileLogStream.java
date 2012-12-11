package net.freehal.compat.sunjava.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.freehal.compat.sunjava.logging.StandardLogUtils.LogStream;
import net.freehal.compat.sunjava.logging.StandardLogUtils.PrintStreamLogStream;
import net.freehal.compat.sunjava.logging.StandardLogUtils.StackTraceUtils;
import net.freehal.core.util.RegexUtils;

public class FileLogStream extends PrintStreamLogStream {

	public static LogStream create(File filename) {
		try {
			return new FileLogStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new NullLogStream();
		}
	}

	public static LogStream create(String filename) {
		return create(new File(filename));
	}

	public FileLogStream(File filename) throws FileNotFoundException {
		super(new PrintStream(new FileOutputStream(filename)));
	}

	public void print(String e) {
		super.print(e);
	}

	@Override
	public void add(String type, String line, StackTraceElement stacktrace) {
		final String prefix = StackTraceUtils.whereInCode(stacktrace) + "(" + type + ") "
				+ (type.length() == 4 ? " " : "");

		// remove all carriage returns
		if (line.contains("\\r")) {
			line = RegexUtils.replace(line, "\\\\r", "");
			println(prefix + line);
		} else {
			println(prefix + line);
		}
	}
}