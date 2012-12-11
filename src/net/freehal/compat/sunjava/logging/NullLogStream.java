package net.freehal.compat.sunjava.logging;

import net.freehal.compat.sunjava.logging.StandardLogUtils.AbstractLogStream;

public class NullLogStream extends AbstractLogStream {

	@Override
	public void add(String type, String line, StackTraceElement stacktrace) {
		// ignore
	}

	@Override
	public void flush() {
		// ignore
	}
}