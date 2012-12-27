package net.freehal.core.logs.receiver;

public interface LogReceiver {

	void addLine(String type, String e, StackTraceElement stacktrace);

	void flush();
}