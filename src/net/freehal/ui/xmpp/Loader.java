package net.freehal.ui.xmpp;

import net.freehal.core.util.LogUtils;
import net.freehal.ui.common.Configuration;
import net.freehal.ui.common.Extension;
import net.freehal.ui.common.Extensions;

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
	public void parseConfig(Configuration config) {
		if (config.hasOption("xmpp-user") && config.hasOption("xmpp-password")) {
			String user = config.getStringOption("xmpp-user", null);
			String password = config.getStringOption("xmpp-password", null);
			String host = config.getStringOption("xmpp-host", DEFAULT_SERVER);
			int port = config.getIntegerOption("xmpp-port", DEFAULT_PORT);

			server = new XmppServer(host, port, user, password);
			Extensions.registerMainLoop(server);
			
		} else if (config.hasOption("xmpp-user") || config.hasOption("xmpp-password")
				|| config.hasOption("xmpp-host") || config.hasOption("xmpp-port")) {
			LogUtils.e("You have to specify a valid username and password to use XMPP.");
		}
	}
}
