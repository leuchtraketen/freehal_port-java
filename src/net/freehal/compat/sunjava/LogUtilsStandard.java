package net.freehal.compat.sunjava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.util.LogUtilsImpl;
import net.freehal.core.util.RegexUtils;

public class LogUtilsStandard implements LogUtilsImpl {

	private final int maxLengthSourceFile = 20;
	private final int maxLengthLineNumber = 4;
	private Set<String> filter = new HashSet<String>();
	private Set<String> temporaryFilter = new HashSet<String>();
	private List<LogStream> streams = new ArrayList<LogStream>();

	@Override
	public LogUtilsImpl addFilter(String className, String type) {
		filter.add(className + ":" + type);
		return this;
	}

	public boolean isFiltered(LogStream stream, final String className,
			final String type) {
		return isFiltered(stream.getFilters(), className, type)
				|| isFiltered(filter, className, type)
				|| isFiltered(temporaryFilter, className, type);
	}

	private static boolean isFiltered(Set<String> filter,
			final String className, final String type) {
		return filter.contains(className + ":" + type)
				|| filter.contains(className + ":" + type.charAt(0))
				|| filter.contains(className + ":*")
				|| filter.contains(className);
	}

	@Override
	public LogUtilsImpl addTemporaryFilter(String className, String type) {
		temporaryFilter.add(className + ":" + type);
		return this;
	}

	@Override
	public LogUtilsImpl resetTemporaryFilters() {
		temporaryFilter.clear();
		return this;
	}

	private String className(StackTraceElement ste) {
		final String fullClassName = ste.getClassName();
		final String className = fullClassName.substring(fullClassName
				.lastIndexOf(".") + 1);
		return className;
	}

	private String lastPackage(StackTraceElement ste) {
		final String fullClassName = ste.getClassName();
		String[] pkgs = fullClassName.split("[.]");
		final String lastPackage = pkgs[pkgs.length - 2];
		return lastPackage;
	}

	private String whereInCode(StackTraceElement ste) {
		final String fullClassName = ste.getClassName();
		final String className = fullClassName.substring(fullClassName
				.lastIndexOf(".") + 1);
		final String sourceFile = className;// + ".java";
		@SuppressWarnings("unused")
		final String methodName = ste.getMethodName();
		int lineNumber = ste.getLineNumber();

		int lengthSourceFile = sourceFile.length();
		int lengthLineNumber = ("" + lineNumber).length();

		StringBuilder place = new StringBuilder();

		if (lengthSourceFile < maxLengthSourceFile)
			place.append(sourceFile);
		else {
			place.append(
					sourceFile.substring(0, maxLengthSourceFile - 2
							+ maxLengthLineNumber - lengthLineNumber)).append(
					"..");
			lengthSourceFile = maxLengthSourceFile;
			lengthLineNumber = maxLengthLineNumber;
		}

		place.append(":");
		place.append(lineNumber);

		for (int i = lengthSourceFile + lengthLineNumber; i <= maxLengthSourceFile
				+ maxLengthLineNumber; ++i)
			place.append(" ");

		return place.toString();
	}

	@Override
	public void e(String e) {
		output("error", "", e);
	}

	@Override
	public void w(String e) {
		output("warn", " ", e);
	}

	@Override
	public void i(String e) {
		output("info", " ", e);
	}

	@Override
	public void d(String e) {
		output("debug", "", e);
	}

	private void output(final String type, final String spaces, String e) {
		StackTraceElement stacktrace = Thread.currentThread().getStackTrace()[4];

		final String prefix = whereInCode(stacktrace) + "(" + type + ") "
				+ spaces;
		// for each stream
		for (LogStream stream : streams) {
			// is it filtered?
			if (isFiltered(stream, className(stacktrace), type)
					|| isFiltered(stream, lastPackage(stacktrace), type)) {
				// ignore this line!
			}
			// ... is it console output?
			else if (stream.getStream() == System.out
					|| stream.getStream() == System.err) {
				// use carriage return for output
				if (e.contains("\\r")) {
					e = RegexUtils.replace(e, "\\\\r", "\r" + prefix);
					stream.print(prefix + e + "\r");
				} else {
					stream.println(prefix + e);
				}
			}
			// ... or a log file?
			else {
				// remove all carriage returns
				if (e.contains("\\r")) {
					e = RegexUtils.replace(e, "\\\\r", "");
					stream.print(prefix + e);
				} else {
					stream.println(prefix + e);
				}
			}
		}
	}

	@Override
	public void flush() {
		for (LogStream stream : streams) {
			stream.flush();
		}
	}

	public LogStream to(PrintStream out) {
		LogStream stream = new LogStream(out);
		streams.add(stream);
		return stream;
	}

	public LogStream to(File file) {
		try {
			return this.to(new PrintStream(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new LogStream(null);
	}

	public static class LogStream {
		private PrintStream out;
		private Set<String> filter = new HashSet<String>();

		public LogStream(PrintStream out) {
			this.out = out;
		}

		public PrintStream getStream() {
			return out;
		}

		public Set<String> getFilters() {
			return filter;
		}

		public LogStream addFilter(String className, String type) {
			filter.add(className + ":" + type);
			return this;
		}

		public boolean isFiltered(final String className, final String type) {
			return LogUtilsStandard.isFiltered(filter, className, type);
		}

		public void flush() {
			out.flush();
		}

		public void println(String string) {
			out.println(string);
		}

		public void print(String string) {
			out.print(string);
		}
	}

}
