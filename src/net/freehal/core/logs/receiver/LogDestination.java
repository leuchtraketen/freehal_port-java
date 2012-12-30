package net.freehal.core.logs.receiver;

public interface LogDestination {

	void addLine(String type, String e, StackTraceElement stacktrace);

	void flush();
}