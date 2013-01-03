package net.freehal.ui.xmpp;

import java.util.Collections;

import net.freehal.core.util.LogUtils;
import net.freehal.ui.common.DataInitializer;
import net.freehal.ui.common.MainLoopListener;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

public class XmppConnection implements MainLoopListener {

	private static final String TAG = "[XMPP] ";

	private ConnectionConfiguration connConfig;
	private String username;
	private String password;
	private boolean useSaslPlain;

	public XmppConnection(String host, int port, String username, String password) {

		// initialize data
		DataInitializer.initializeLanguageSpecificData(Collections.<String> emptySet());

		// print status info
		LogUtils.i(TAG + "XMPP Extension started:");
		LogUtils.i(TAG + "  Host: " + host);
		LogUtils.i(TAG + "  Port: " + port);
		LogUtils.i(TAG + "  User: " + username);
		// LogUtils.i(TAG +"  Password: " + password);
		LogUtils.i(TAG + "  Password: (not shown)");

		this.username = username;
		this.password = password;

		if (host.contains("gmail") || host.contains("google")) {
			connConfig = new ConnectionConfiguration("talk.google.com", port, "gmail.com");
			useSaslPlain = true;
		} else {
			connConfig = new ConnectionConfiguration(host, port, host);
			useSaslPlain = false;
		}
	}

	@Override
	public void loop() {
		XMPPConnection connection = new XMPPConnection(connConfig);
		while (connect(connection) && login(connection)) {
			XmppChat chat = new XmppChat(connection);
			chat.loop();
		}
	}

	private boolean login(XMPPConnection connection) {
		if (connection.isAuthenticated()) {
			return true;

		} else {
			try {
				connection.login(username, password);
				LogUtils.i(TAG + "Logged in as " + connection.getUser() + " (" + username + ")");

				Presence presence = new Presence(Presence.Type.available);
				connection.sendPacket(presence);

				return true;

			} catch (XMPPException ex) {
				LogUtils.e(TAG + "Failed to log in as " + connection.getUser() + " (" + username + ")");
				LogUtils.e(ex);
				return false;
			}
		}
	}

	private boolean connect(XMPPConnection connection) {
		if (connection.isConnected()) {
			return true;

		} else {
			try {
				if (useSaslPlain) {
					SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				}
				connection.connect();
				LogUtils.i(TAG + "Connected to " + connection.getHost());
				return true;

			} catch (XMPPException ex) {
				LogUtils.e(TAG + "Failed to connect to " + connection.getHost());
				LogUtils.e(ex);
				return false;
			}
		}
	}
}
