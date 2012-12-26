package net.freehal.compat.sunjava.logging;

import java.util.Set;

public interface LogStream {
	void add(String type, String e, StackTraceElement stacktrace);

	Set<String> getFilters();

	void flush();

	LogStream addFilter(String string, String string2);
}