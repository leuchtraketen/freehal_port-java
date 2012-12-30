package net.freehal.core.logs.listener;

public class FakeLogDestination implements LogDestination {

	@Override
	public void addLine(String type, String line, StackTraceElement stacktrace) {
		// ignore
	}

	@Override
	public void flush() {
		// ignore
	}
}