package net.freehal.core.logs.receiver;

public class StackTraceUtils {

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
			if (!className(ste).contains("LogUtils") && !className(ste).contains("Thread")
					&& !className(ste).contains("LogStream") && !className(ste).contains("VMStack")) {
				return ste;
			}
		}
		return null;
	}

	public static String className(StackTraceElement ste) {
		final String fullClassName = ste.getClassName();
		final String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		return className;
	}

	public static String methodName(StackTraceElement ste) {
		final String methodName = ste.getMethodName();
		return methodName;
	}

	public static String lastPackage(StackTraceElement ste) {
		final String fullClassName = ste.getClassName();
		String[] pkgs = fullClassName.split("[.]");
		final String lastPackage = pkgs[pkgs.length - 2];
		return lastPackage;
	}

	public static String whereInCode(StackTraceElement ste) {
		final String fullClassName = ste.getClassName();
		final String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
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
					sourceFile.substring(0, maxLengthSourceFile - 2 + maxLengthLineNumber
							- lengthLineNumber)).append("..");
			lengthSourceFile = maxLengthSourceFile;
			lengthLineNumber = maxLengthLineNumber;
		}

		place.append(":");
		place.append(lineNumber);

		for (int i = lengthSourceFile + lengthLineNumber; i <= maxLengthSourceFile + maxLengthLineNumber; ++i)
			place.append(" ");

		return place.toString();
	}
}