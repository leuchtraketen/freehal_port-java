package net.freehal.ui.xmpp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import net.freehal.core.util.ExitListener;
import net.freehal.core.util.SystemUtils;
import net.freehal.ui.common.Extension;

public class XmppServer implements Extension, ExitListener {

	public XmppServer() {
		SystemUtils.destructOnExit(this);
	}

	@Override
	public void onExit(int status) {}

	@Override
	public void addOptions(Options options) {}

	@Override
	public void parseCommandLine(CommandLine line) {

	}

}
