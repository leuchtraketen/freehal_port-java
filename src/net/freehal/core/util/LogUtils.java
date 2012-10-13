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

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.storage.Storages;

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

	private static final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
	private static Progress topProgress = new TopProgress();
	private static Progress currentProgress = null;

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

	private static void updateProgressListeners(String text) {
		for (ProgressListener listener : progressListeners) {
			listener.onProgressUpdate(topProgress.getCurrent(), topProgress.getMax(), text);
		}
	}

	public static void updateProgress(double d, double e) {
		updateProgress(e, d, null);
	}

	public static void updateProgress(double d, double e, String text) {
		if (e > 0) {
			currentProgress.updateProgress(d, e);
		}
		updateProgressListeners(text);
	}

	public static void updateProgress() {
		updateProgress(null);
	}

	public static void updateProgress(String text) {
		currentProgress.updateProgress();
		updateProgressListeners(text);
	}

	public static void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}

	public static void startProgress(double current, double step, double max) {
		startProgress(new FixedProgress(current, step, max));
	}

	public static void startProgress(String componentName) {
		startProgress(new FlexibleProgress(componentName));
		updateProgressListeners(componentName);
	}

	private static void startProgress(Progress step) {
		if (currentProgress == null) {
			step.setParent(topProgress);
			topProgress.setChild(step);
			currentProgress = step;
			for (ProgressListener listener : progressListeners) {
				listener.onProgressBeginning();
			}
		} else {
			step.setParent(currentProgress);
			currentProgress.setChild(step);
			currentProgress = step;
			for (ProgressListener listener : progressListeners) {
				listener.onSubProgressBeginning();
			}
		}
	}

	public static void stopProgress() {
		if (currentProgress.getParent() == topProgress) {
			for (ProgressListener listener : progressListeners) {
				listener.onProgressEnd();
			}
			currentProgress = null;
		} else {
			for (ProgressListener listener : progressListeners) {
				listener.onSubProgressEnd();
			}
			currentProgress.updateToParent();
			currentProgress.getParent().setChild(null);
			currentProgress = currentProgress.getParent();
		}
	}

	public static interface ProgressListener {
		void onProgressUpdate(double d, double e, String text);

		void onProgressBeginning();

		void onProgressEnd();

		void onSubProgressBeginning();

		void onSubProgressEnd();
	}

	public static interface Progress {
		void updateProgress();

		void updateProgress(double d, double e);

		double getMax();

		double getCurrent();

		Progress getParent();

		void setParent(Progress parent);

		Progress getChild();

		void setChild(Progress parent);

		void updateToParent();

		void updateFromChild();

	}

	public static class FixedProgress implements Progress {

		private double max = 1;
		private double step = 0.1;
		private double current = 0;
		private Progress parent = null;
		private Progress child = null;

		public FixedProgress(double current, double step, double max) {
			this.current = current;
			this.step = step;
			this.max = max;
		}

		@Override
		public Progress getParent() {
			return parent;
		}

		@Override
		public void setParent(Progress parent) {
			this.parent = parent;
		}

		@Override
		public Progress getChild() {
			return child;
		}

		@Override
		public void setChild(Progress child) {
			this.child = child;
		}

		@Override
		public void updateProgress() {
			current += step;
		}

		@Override
		public void updateProgress(double d, double e) {
			current = max / e * d;
		}

		@Override
		public double getMax() {
			if (child != null)
				return max; // + child.getMax();
			else
				return max;
		}

		@Override
		public double getCurrent() {
			if (child != null)
				return current + step * child.getCurrent() / child.getMax();
			else
				return current;
			/*
			 * if (false) LogUtils.i("FixedProgress: current=" + current + " + "
			 * + ((child != null) ? (step * child.getCurrent() / child.getMax())
			 * : 0) + ", max=" + max + ", step=" + step); if (child != null)
			 * return current + step * child.getCurrent() / child.getMax(); else
			 * return current;
			 */
		}

		@Override
		public void updateToParent() {
			parent.updateFromChild();
		}

		@Override
		public void updateFromChild() {
			updateProgress();
		}
	}

	public static class FlexibleProgress implements Progress {

		private final String name;
		private final double averageMax;

		private double max = 2;
		private double step = 1;
		private double current = 0;
		private Progress parent = null;
		private Progress child = null;

		public FlexibleProgress(String name) {
			this.name = name;

			final String maxStr = Storages.getLanguageDirectory().getChild("progress/" + name).read();
			try {
				this.max = Double.parseDouble(maxStr);

			} catch (Exception ex) {
				// NumberFormatException and NullPointerException!
				this.max = 1;
				LogUtils.e(ex);
			}
			averageMax = this.max;
		}

		@Override
		public void updateProgress() {
			current += step;
			if (current >= max) {
				max = max * 2;
			}
		}

		@Override
		public void updateProgress(double d, double e) {
			if (max < e) {
				max = e;
			}
			current = max / e * d;
		}

		@Override
		public double getMax() {
			if (child != null)
				return max + child.getMax();
			else
				return max;
		}

		@Override
		public double getCurrent() {
			if (child != null)
				return current + child.getCurrent();
			else
				return current;
		}

		@Override
		public Progress getParent() {
			return parent;
		}

		@Override
		public void setParent(Progress parent) {
			this.parent = parent;
		}

		@Override
		public void updateToParent() {
			if (averageMax == 1)
				Storages.getLanguageDirectory().getChild("progress/" + name).write(max + "");
			else
				Storages.getLanguageDirectory().getChild("progress/" + name)
						.write(((averageMax * 2 + max) / 3) + "");
			parent.updateProgress();
		}

		@Override
		public Progress getChild() {
			return child;
		}

		@Override
		public void setChild(Progress child) {
			this.child = child;
		}

		@Override
		public void updateFromChild() {
			if (child.getMax() != step) {
				max *= child.getMax() / step;
				current *= child.getMax() / step;
				step = child.getMax();
			}
			updateProgress();
		}
	}

	public static class TopProgress implements Progress {

		private Progress step = null;

		@Override
		public Progress getParent() {
			return this;
		}

		@Override
		public void setParent(Progress parent) {
			// this is the root progress!
		}

		@Override
		public void updateToParent() {
			// this is the root progress!
		}

		@Override
		public void updateProgress() {
			// this is the root progress!
		}

		@Override
		public void updateProgress(double d, double e) {
			// this is the root progress!
		}

		@Override
		public double getMax() {
			if (step != null)
				return step.getMax();
			else
				return 1;
		}

		@Override
		public double getCurrent() {
			if (step != null)
				return step.getCurrent();
			else
				return 1;
		}

		@Override
		public Progress getChild() {
			return step;
		}

		@Override
		public void setChild(Progress child) {
			this.step = child;
		}

		@Override
		public void updateFromChild() {
			// this is the root progress!
		}
	}
}
