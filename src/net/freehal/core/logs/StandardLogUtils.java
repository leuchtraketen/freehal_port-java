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
import net.freehal.core.logs.receiver.LogDestination;
import net.freehal.core.logs.receiver.StackTraceUtils;
import net.freehal.core.util.LogUtilsImpl;

public class StandardLogUtils implements LogUtilsImpl, FilteredLog {

	private Set<String> filter = new HashSet<String>();
	private Set<String> temporaryFilter = new HashSet<String>();
	private List<FilteredLog> streams = new ArrayList<FilteredLog>();

	@Override
	public LogUtilsImpl addFilter(String className, String type) {
		filter.add(className + ":" + type);
		return this;
	}

	@Override
	public Set<String> getFilters() {
		return filter;
	}

	@Override
	public boolean isFiltered(String className, String type) {
		return FilteredLog.Algorithms.isFiltered(filter, className, type)
				|| FilteredLog.Algorithms.isFiltered(temporaryFilter, className, type);
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

		// is it filtered?
		if (isFiltered(StackTraceUtils.className(stacktrace), type)
				|| isFiltered(StackTraceUtils.lastPackage(stacktrace), type)) {
			// ignore this line!
			return;
		}

		// for each stream
		for (FilteredLog stream : streams) {
			// is it filtered?
			if (stream.isFiltered(StackTraceUtils.className(stacktrace), type)
					|| stream.isFiltered(StackTraceUtils.lastPackage(stacktrace), type)) {}
			// print it
			else {
				stream.addLine(type, e, stacktrace);
			}
		}
	}

	@Override
	public void addLine(String type, String e, StackTraceElement stacktrace) {
		// for each stream
		for (FilteredLog stream : streams) {
			// print it
			stream.addLine(type, e, stacktrace);
		}
	}

	@Override
	public void flush() {
		for (LogDestination stream : streams) {
			stream.flush();
		}
	}

	public LogDestination addDestination(LogDestination stream) {
		if (stream instanceof FakeLogReciever) {
			// ignore
			return stream;

		} else if (stream instanceof FilteredLog) {
			return addDestination((FilteredLog) stream);

		} else {
			return addDestination(new FilteredLog.StandardFilteredLog(stream));
		}
	}

	public FilteredLog addDestination(FilteredLog stream) {
		streams.add(stream);
		return stream;
	}
}
