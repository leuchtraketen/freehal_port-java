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

	@Override
	public void e(String e) {
		output("error", e);
	}

	@Override
	public void w(String e) {
		output("warn", e);
	}

	@Override
	public void i(String e) {
		output("info", e);
	}

	@Override
	public void d(String e) {
		output("debug", e);
	}

	private void output(final String type, String e) {
		StackTraceElement stacktrace = StackTraceUtils.caller();
		// for each stream
		for (LogStream stream : streams) {
			// is it filtered?
			if (isFiltered(stream, StackTraceUtils.className(stacktrace), type)
					|| isFiltered(stream,
							StackTraceUtils.lastPackage(stacktrace), type)) {
				// ignore this line!
			}
			// print it
			else {
				stream.add(type, e, stacktrace);
			}
		}
	}

	@Override
	public void flush() {
		for (LogStream stream : streams) {
			stream.flush();
		}
	}

	public LogStream to(LogStream stream) {
		if (!(stream instanceof NullLogStream))
			streams.add(stream);
		return stream;
	}

	public static class NullLogStream extends LogStream {

		private NullLogStream(PrintStream out) {
			super(out);
		}

		public NullLogStream() {
			super(null);
		}

		@Override
		public void add(String type, String line, StackTraceElement stacktrace) {
			// ignore
		}
	}

	public static class FileLogStream extends LogStream {

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
			final String prefix = StackTraceUtils.whereInCode(stacktrace) + "("
					+ type + ") " + (type.length() == 4 ? " " : "");

			// remove all carriage returns
			if (line.contains("\\r")) {
				line = RegexUtils.replace(line, "\\\\r", "");
				print(prefix + line);
			} else {
				println(prefix + line);
			}
		}
	}

	public static class ConsoleLogStream extends LogStream {

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
			final String prefix = StackTraceUtils.whereInCode(stacktrace) + "("
					+ type + ") " + (type.length() == 4 ? " " : "");

			// use carriage return for output
			if (line.contains("\\r")) {
				line = RegexUtils.replace(line, "\\\\r", "\r" + prefix);
				print(prefix + line + "\r");
			} else {
				println(prefix + line);
			}
		}
	}

	public static abstract class LogStream {
		private PrintStream out;
		private Set<String> filter = new HashSet<String>();

		public LogStream(PrintStream out) {
			this.out = out;
		}

		public abstract void add(String type, String line,
				StackTraceElement stacktrace);

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

		protected void println(String string) {
			out.println(string);
		}

		protected void print(String string) {
			out.print(string);
		}
	}

	public static class StackTraceUtils {

		/**
		 * How many spaces to add to the source file for formatted output
		 */
		private static final int maxLengthSourceFile = 20;
		/**
		 * How many spaces to add to the line number for formatted output
		 */
		private static final int maxLengthLineNumber = 4;

		/**
		 * Find the last stacktrace entry which does not correspond to a
		 * LogUtils* class
		 * 
		 * @return
		 */
		public static StackTraceElement caller() {
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				if (!className(ste).contains("LogUtils")
						&& !className(ste).contains("Thread")
						&& !className(ste).contains("LogStream")) {
					return ste;
				}
			}
			return null;
		}

		public static String className(StackTraceElement ste) {
			final String fullClassName = ste.getClassName();
			final String className = fullClassName.substring(fullClassName
					.lastIndexOf(".") + 1);
			return className;
		}

		public static String lastPackage(StackTraceElement ste) {
			final String fullClassName = ste.getClassName();
			String[] pkgs = fullClassName.split("[.]");
			final String lastPackage = pkgs[pkgs.length - 2];
			return lastPackage;
		}

		public static String whereInCode(StackTraceElement ste) {
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
								+ maxLengthLineNumber - lengthLineNumber))
						.append("..");
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
	}
}
