package net.freehal.compat.sunjava;

import net.freehal.core.util.LogUtilsImpl;

public class LogUtilsStandard implements LogUtilsImpl {

	final int maxLengthSourceFile = 20;
	final int maxLengthLineNumber = 4;

	private String whereInCode() {
		final String fullClassName = Thread.currentThread().getStackTrace()[4]
				.getClassName();
		final String className = fullClassName.substring(fullClassName
				.lastIndexOf(".") + 1);
		final String sourceFile = className + ".java";
		@SuppressWarnings("unused")
		final String methodName = Thread.currentThread().getStackTrace()[4]
				.getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[4]
				.getLineNumber();

		int lengthSourceFile = sourceFile.length();
		int lengthLineNumber = ("" + lineNumber).length();

		StringBuilder place = new StringBuilder();
		place.append("(");

		if (lengthSourceFile < maxLengthSourceFile)
			place.append(sourceFile);
		else {
			place.append(sourceFile.substring(0, maxLengthSourceFile - 2 + maxLengthLineNumber - lengthLineNumber))
					.append("..");
			lengthSourceFile = maxLengthSourceFile;
			lengthLineNumber = maxLengthLineNumber;
		}

		place.append(":");
		place.append(lineNumber);
		place.append(")");

		for (int i = lengthSourceFile + lengthLineNumber; i <= maxLengthSourceFile
				+ maxLengthLineNumber; ++i)
			place.append(" ");

		return place.toString();
	}

	@Override
	public void e(String e) {
		System.out.println(whereInCode() + "(error)  " + e);
	}

	@Override
	public void w(String e) {
		System.out.println(whereInCode() + "(warn)   " + e);
	}

	@Override
	public void i(String e) {
		System.out.println(whereInCode() + "(info)   " + e);
	}

	@Override
	public void d(String e) {
		System.out.println(whereInCode() + "(debug)  " + e);
	}

}
