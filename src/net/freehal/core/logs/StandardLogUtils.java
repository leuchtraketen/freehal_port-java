/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.logs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.logs.receiver.FakeLogReciever;
import net.freehal.core.logs.receiver.LogReceiver;
import net.freehal.core.logs.receiver.StackTraceUtils;
import net.freehal.core.util.LogUtilsImpl;

public class StandardLogUtils implements LogUtilsImpl {

	private Set<String> filter = new HashSet<String>();
	private Set<String> temporaryFilter = new HashSet<String>();
	private List<FilteredLog> streams = new ArrayList<FilteredLog>();

	@Override
	public LogUtilsImpl addFilter(String className, String type) {
		filter.add(className + ":" + type);
		return this;
	}

	public boolean isFiltered(FilteredLog stream, final String className, final String type) {
		return isFiltered(stream.getFilters(), className, type) || isFiltered(filter, className, type)
				|| isFiltered(temporaryFilter, className, type);
	}

	static boolean isFiltered(Set<String> filter, final String className, final String type) {
		return filter.contains(className + ":" + type) || filter.contains(className + ":" + type.charAt(0))
				|| filter.contains(className + ":*") || filter.contains(className);
	}

	@Override
	public LogUtilsImpl addTemporaryFilter(String className, String type) {
		temporaryFilter.add(className + ":" + type);
		return this;
	}

	@Override
	public LogUtilsImpl resetTemporaryFilters() {
		temporaryFilter.clear();
		return this;
	}

	@Override
	public void e(String e) {
		output("error", e);
	}

	@Override
	public void w(String e) {
		output("warn", e);
	}

	@Override
	public void i(String e) {
		output("info", e);
	}

	@Override
	public void d(String e) {
		output("debug", e);
	}

	private void output(final String type, String e) {
		StackTraceElement stacktrace = StackTraceUtils.caller();
		// for each stream
		for (FilteredLog stream : streams) {
			// is it filtered?
			if (isFiltered(stream, StackTraceUtils.className(stacktrace), type)
					|| isFiltered(stream, StackTraceUtils.lastPackage(stacktrace), type)) {
				// ignore this line!
			}
			// print it
			else {
				stream.addLine(type, e, stacktrace);
			}
		}
	}

	@Override
	public void flush() {
		for (LogReceiver stream : streams) {
			stream.flush();
		}
	}

	public LogReceiver to(LogReceiver stream) {
		if (stream instanceof FakeLogReciever) {
			// ignore
			return stream;
		} else {
			final FilteredLog filteredStream;
			if (stream instanceof FilteredLog)
				filteredStream = (FilteredLog) stream;
			else
				filteredStream = new FilteredLog(stream);

			streams.add(filteredStream);
			return filteredStream;
		}
	}

	public FilteredLog to(FilteredLog stream) {
		streams.add(stream);
		return stream;
	}

	public static class FilteredLog implements LogReceiver {

		LogReceiver unfiltered;

		public FilteredLog(LogReceiver unfiltered) {
			this.unfiltered = unfiltered;
		}

		private Set<String> filter = new HashSet<String>();

		@Override
		public void addLine(String type, String e, StackTraceElement stacktrace) {
			unfiltered.addLine(type, e, stacktrace);
		}

		public Set<String> getFilters() {
			return filter;
		}

		public LogReceiver addFilter(String className, String type) {
			filter.add(className + ":" + type);
			return this;
		}

		public boolean isFiltered(final String className, final String type) {
			return StandardLogUtils.isFiltered(filter, className, type);
		}

		@Override
		public void flush() {
			unfiltered.flush();
		}
	}
}
