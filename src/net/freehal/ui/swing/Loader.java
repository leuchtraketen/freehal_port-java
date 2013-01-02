package net.freehal.ui.swing;

import net.freehal.core.logs.StandardLogUtils;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.SystemUtils;
import net.freehal.ui.common.Configuration;
import net.freehal.ui.common.Extension;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class Loader implements Extension {

	private SwingLogWindow instance = null;

	@SuppressWarnings("static-access")
	@Override
	public void addOptions(Options options) {
		options.addOption(OptionBuilder
				.withLongOpt("log-window")
				.withDescription(
						"display a window to show logs " + "(default on Microsoft Windows and Mac OS X)")
				.hasArg().withArgName("FILE").create());
	}

	@Override
	public void parseConfig(Configuration line) {
		// show a swing log window? it's default on windows systems...
		final boolean showLogWindow = line.getBooleanOption("log-window", SystemUtils.isWindows()
				|| SystemUtils.isMacOSX());

		if (showLogWindow) {
			StandardLogUtils logger = (StandardLogUtils) LogUtils.get();
			instance = new SwingLogWindow();
			logger.addDestination(instance);
		}
	}

}
