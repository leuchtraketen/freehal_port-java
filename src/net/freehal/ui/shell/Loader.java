package net.freehal.ui.shell;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import net.freehal.core.util.LogUtils;
import net.freehal.ui.common.Extension;

public class Loader implements Extension {

	@SuppressWarnings("static-access")
	@Override
	public void addOptions(Options options) {
		options.addOption(OptionBuilder.withLongOpt("shell")
				.withDescription("opens an interactive shell for answering").create("s"));
	}

	@Override
	public void parseCommandLine(CommandLine line) {
		if (line.hasOption("shell")) {
			try {
				InteractiveShell shell = new InteractiveShell();
				shell.loop(System.in, System.out);
			} catch (IOException ex) {
				LogUtils.e(ex);
			}
		}
	}
}
