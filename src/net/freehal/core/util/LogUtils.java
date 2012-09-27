/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.util;

public class LogUtils {

	private static LogUtilsImpl instance = null;

	public static void set(LogUtilsImpl instance) {
		LogUtils.instance = instance;
	}

	public static void e(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.e(line);
			}
		} else {
			instance.e(s);
		}
	}

	public static void w(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.w(line);
			}
		} else {
			instance.w(s);
		}
	}

	public static void i(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.i(line);
			}
		} else {
			instance.i(s);
		}
	}

	public static void d(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.d(line);
			}
		} else {
			instance.d(s);
		}
	}

	public static void flush() {
		instance.flush();
	}
	

	public static LogUtilsImpl addFilter(String className, final String type) {
		instance.addFilter(className, type);
		return instance;
	}

	public static LogUtilsImpl addTemporaryFilter(String className, final String type) {
		instance.addTemporaryFilter(className, type);
		return instance;
	}

	public static LogUtilsImpl resetTemporaryFilters() {
		instance.resetTemporaryFilters();
		return instance;
	}
}
