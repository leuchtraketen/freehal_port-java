package net.freehal.core.logs.listener;

public interface LogDestination {

	void addLine(String type, String e, StackTraceElement stacktrace);

	void flush();
}