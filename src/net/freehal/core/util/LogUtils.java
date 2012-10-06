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
package net.freehal.core.util;

/**
 * An utility class for logging.
 * 
 * @author "Tobias Schulz"
 */
public class LogUtils {

	public static final String ERROR = "debug";
	public static final String WARNING = "warning";
	public static final String INFO = "info";
	public static final String DEBUG = "debug";

	/**
	 * The current {@link LogUtilsImpl} implementation.
	 */
	private static LogUtilsImpl instance = null;

	/**
	 * Set the {@link LogUtilsImpl} implementation to use.
	 * 
	 * @param instance
	 *        an instance of a class which implements {@link LogUtilsImpl}
	 */
	public static void set(LogUtilsImpl instance) {
		LogUtils.instance = instance;
	}

	/**
	 * Print the exception and it's stacktrace as error message with
	 * {@link StringUtils#asString(Exception)}.
	 * 
	 * @see StringUtils#asString(Exception)
	 * @see LogUtilsImpl#e(String)
	 * @param ex
	 *        the exception to use
	 */
	public static void e(final Exception ex) {
		e(StringUtils.asString(ex));
	}

	/**
	 * Log an error!
	 * 
	 * @see LogUtilsImpl#e(String)
	 * @param obj
	 *        the message
	 */
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

	/**
	 * Log a warning!
	 * 
	 * @see LogUtilsImpl#w(String)
	 * @param obj
	 *        the message
	 */
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

	/**
	 * Log an info message!
	 * 
	 * @see LogUtilsImpl#i(String)
	 * @param obj
	 *        the message
	 */
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

	/**
	 * Log a debug message!
	 * 
	 * @see LogUtilsImpl#d(String)
	 * @param obj
	 *        the message
	 */
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

	/**
	 * Flush all output streams used for logging.
	 */
	public static void flush() {
		instance.flush();
	}

	/**
	 * Add a log filter with the given class or package name and the given
	 * message type.
	 * 
	 * @see LogUtilsImpl#addFilter(String, String)
	 * @param className
	 *        the class or package name to filter
	 * @param type
	 *        the type of logs to filter (error, warning, info, debug)
	 * @return the current instance of {@link LogUtilsImpl}
	 */
	public static LogUtilsImpl addFilter(String className, final String type) {
		instance.addFilter(className, type);
		return instance;
	}

	/**
	 * Add a temporary log filter with the given class or package name and the
	 * given message type. These filters are put on a separate list and can be
	 * cleaned with {@link #resetTemporaryFilters()}. Use them for filtering
	 * messages in a small piece of code.
	 * 
	 * @see LogUtilsImpl#addTemporaryFilter(String, String)
	 * @see #resetTemporaryFilters()
	 * @param className
	 *        the class or package name to filter
	 * @param type
	 *        the type of logs to filter (error, warning, info, debug)
	 * @return the current instance of {@link LogUtilsImpl}
	 */
	public static LogUtilsImpl addTemporaryFilter(String className, final String type) {
		instance.addTemporaryFilter(className, type);
		return instance;
	}

	/**
	 * Reset the temporary log filter list.
	 * 
	 * @see LogUtilsImpl#resetTemporaryFilters()
	 * @see #addTemporaryFilter(String, String)
	 * @return the current instance of {@link LogUtilsImpl}
	 */
	public static LogUtilsImpl resetTemporaryFilters() {
		instance.resetTemporaryFilters();
		return instance;
	}
}
