package net.freehal.compat.sunjava.logging;


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