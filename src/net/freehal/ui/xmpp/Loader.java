package net.freehal.ui.xmpp;

import net.freehal.ui.common.CommandLineUtils;
import net.freehal.ui.common.Extension;
import net.freehal.ui.common.Extensions;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class Loader implements Extension {

	private static final String DEFAULT_SERVER = "talk.google.com";
	private static final int DEFAULT_PORT = 5222;

	private XmppServer server;

	@SuppressWarnings("static-access")
	@Override
	public void addOptions(Options options) {
		options.addOption(OptionBuilder.withLongOpt("xmpp-host")
				.withDescription("the XMPP server to connect to (default: " + DEFAULT_SERVER + ")").hasArg()
				.withArgName("HOST").create());
		options.addOption(OptionBuilder.withLongOpt("xmpp-port")
				.withDescription("the XMPP port to use (default: " + DEFAULT_PORT + ")").hasArg()
				.withArgName("PORT").create());
		options.addOption(OptionBuilder.withLongOpt("xmpp-user").withDescription("the XMPP user name")
				.hasArg().withArgName("MAIL").create());
		options.addOption(OptionBuilder.withLongOpt("xmpp-password").withDescription("the XMPP user name")
				.hasArg().withArgName("PASSWORD").create());
	}

	@Override
	public void parseCommandLine(CommandLine line) {
		if (line.hasOption("xmpp-user") && line.hasOption("xmpp-password")) {
			String user = CommandLineUtils.getStringOption(line, "xmpp-user", null);
			String password = CommandLineUtils.getStringOption(line, "xmpp-password", null);
			String host = CommandLineUtils.getStringOption(line, "xmpp-host", DEFAULT_SERVER);
			int port = CommandLineUtils.getIntOption(line, "xmpp-port", DEFAULT_PORT);

			server = new XmppServer(host, port, user, password);
			Extensions.registerMainLoop(server);
		}
	}
}
