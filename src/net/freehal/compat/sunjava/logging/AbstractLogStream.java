package net.freehal.compat.sunjava.logging;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLogStream implements LogStream {
	private Set<String> filter = new HashSet<String>();

	@Override
	public abstract void add(String type, String line, StackTraceElement stacktrace);

	@Override
	public Set<String> getFilters() {
		return filter;
	}

	@Override
	public LogStream addFilter(String className, String type) {
		filter.add(className + ":" + type);
		return this;
	}

	public boolean isFiltered(final String className, final String type) {
		return StandardLogUtils.isFiltered(filter, className, type);
	}
}