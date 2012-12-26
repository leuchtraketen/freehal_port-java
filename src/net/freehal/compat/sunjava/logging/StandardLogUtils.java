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
package net.freehal.compat.sunjava.logging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.util.LogUtilsImpl;

public class StandardLogUtils implements LogUtilsImpl {

	private Set<String> filter = new HashSet<String>();
	private Set<String> temporaryFilter = new HashSet<String>();
	private List<LogStream> streams = new ArrayList<LogStream>();

	@Override
	public LogUtilsImpl addFilter(String className, String type) {
		filter.add(className + ":" + type);
		return this;
	}

	public boolean isFiltered(LogStream stream, final String className, final String type) {
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
		for (LogStream stream : streams) {
			// is it filtered?
			if (isFiltered(stream, StackTraceUtils.className(stacktrace), type)
					|| isFiltered(stream, StackTraceUtils.lastPackage(stacktrace), type)) {
				// ignore this line!
			}
			// print it
			else {
				stream.add(type, e, stacktrace);
			}
		}
	}

	@Override
	public void flush() {
		for (LogStream stream : streams) {
			stream.flush();
		}
	}

	public LogStream to(LogStream stream) {
		if (!(stream instanceof NullLogStream))
			streams.add(stream);
		return stream;
	}
}
