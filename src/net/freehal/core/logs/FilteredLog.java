package net.freehal.core.logs;

import java.util.HashSet;
import java.util.Set;

import net.freehal.core.logs.listener.LogDestination;

public interface FilteredLog extends LogDestination {

	Set<String> getFilters();

	boolean isFiltered(String className, String type);

	LogDestination addFilter(String className, String type);

	public static class Algorithms {

		public static boolean isFiltered(Set<String> filter, String className, String type) {
			return filter.contains(className + ":" + type)
					|| filter.contains(className + ":" + type.charAt(0)) || filter.contains(className + ":*")
					|| filter.contains(className);
		}
	}

	public static class StandardFilteredLog implements FilteredLog {

		LogDestination unfiltered;

		public StandardFilteredLog(LogDestination unfiltered) {
			this.unfiltered = unfiltered;
		}

		private Set<String> filter = new HashSet<String>();

		@Override
		public void addLine(String type, String e, StackTraceElement stacktrace) {
			unfiltered.addLine(type, e, stacktrace);
		}

		@Override
		public Set<String> getFilters() {
			return filter;
		}

		@Override
		public LogDestination addFilter(String className, String type) {
			filter.add(className + ":" + type);
			return this;
		}

		@Override
		public boolean isFiltered(final String className, final String type) {
			return Algorithms.isFiltered(filter, className, type);
		}

		@Override
		public void flush() {
			unfiltered.flush();
		}
	}
}