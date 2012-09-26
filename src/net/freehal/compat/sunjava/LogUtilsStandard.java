package net.freehal.compat.sunjava;

import java.util.HashSet;
import java.util.Set;

import net.freehal.core.util.LogUtilsImpl;
import net.freehal.core.util.RegexUtils;

public class LogUtilsStandard implements LogUtilsImpl {

	private final int maxLengthSourceFile = 20;
	private final int maxLengthLineNumber = 4;
	private Set<String> filter = new HashSet<String>();

	public LogUtilsStandard filter(String className) {
		filter.add(className);
		return this;
	}

	private boolean isFiltered(final String className, final String type) {
		return filter.contains(className + ":" + type)
				|| filter.contains(className + ":" + type.charAt(0))
				|| filter.contains(className + ":*")
				|| filter.contains(className);
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
		if (isFiltered(className(stacktrace), type)
				|| isFiltered(lastPackage(stacktrace), type)) {
			// if filtered, ignore this line!
		} else if (e.contains("\\r")) {
			e = RegexUtils.replace(e, "\\r", "\r" + prefix);
			System.out.print(prefix + e + "\r");
		} else {
			System.out.println(prefix + e);
		}
	}

	@Override
	public void flush() {
		System.out.flush();
	}
}
