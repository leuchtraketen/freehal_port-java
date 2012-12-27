package net.freehal.core.logs.receiver;

public class FakeLogReciever implements LogReceiver {

	@Override
	public void addLine(String type, String line, StackTraceElement stacktrace) {
		// ignore
	}

	@Override
	public void flush() {
		// ignore
	}
}