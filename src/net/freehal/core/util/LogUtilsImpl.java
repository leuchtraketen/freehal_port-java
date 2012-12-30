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

import net.freehal.core.logs.listener.LogDestination;

/**
 * This interface is used by {@link LogUtils} and needs be implemented by any
 * class which can be used for receiving and saving or displaying log messages.
 * 
 * @author "Tobias Schulz"
 */
public interface LogUtilsImpl extends LogDestination {

	/**
	 * Log an error!
	 * 
	 * @see LogUtils#e(Object)
	 * @param e
	 *        the message
	 */
	public void e(String e);

	/**
	 * Log a warning!
	 * 
	 * @see LogUtils#w(Object)
	 * @param e
	 *        the message
	 */
	public void w(String e);

	/**
	 * Log an info message!
	 * 
	 * @see LogUtils#i(Object)
	 * @param e
	 *        the message
	 */
	public void i(String e);

	/**
	 * Log a debug message!
	 * 
	 * @see LogUtils#d(Object)
	 * @param e
	 *        the message
	 */
	public void d(String e);

	/**
	 * Flush all output streams used for logging.
	 */
	public void flush();

	/**
	 * Add a log filter with the given class or package name and the given
	 * message type. See {@link LogUtils#addFilter(String, String)} for more
	 * information.
	 * 
	 * @see LogUtils#addFilter(String, String)
	 * @param className
	 *        the class or package name to filter
	 * @param type
	 *        the type of logs to filter (error, warning, info, debug)
	 * @return this instance of {@link LogUtilsImpl}
	 */
	public LogUtilsImpl addFilter(String className, String type);

	/**
	 * Add a temporary log filter with the given class or package name and the
	 * given message type. See
	 * {@link LogUtils#addTemporaryFilter(String, String)} for more information.
	 * 
	 * @see LogUtils#addTemporaryFilter(String, String)
	 * @see #resetTemporaryFilters()
	 * @param className
	 *        the class or package name to filter
	 * @param type
	 *        the type of logs to filter (error, warning, info, debug)
	 * @return this instance of {@link LogUtilsImpl}
	 */
	public LogUtilsImpl addTemporaryFilter(String className, String type);

	/**
	 * Reset the temporary log filter list. See
	 * {@link LogUtils#resetTemporaryFilters()} for more information.
	 * 
	 * @see LogUtils#resetTemporaryFilters()
	 * @see #addTemporaryFilter(String, String)
	 * @return this instance of {@link LogUtilsImpl}
	 */
	public LogUtilsImpl resetTemporaryFilters();

}
