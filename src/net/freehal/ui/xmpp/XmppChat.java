package net.freehal.ui.xmpp;

import java.util.Collection;

import net.freehal.core.util.ExitListener;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.SystemUtils;
import net.freehal.ui.common.DataInitializer;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

public class XmppChat implements ExitListener, ChatManagerListener, MessageListener, RosterListener {

	private static final String GROUP_NAME = "Freehal Contacts";
	private static final String TAG = "[XMPP] ";

	private XMPPConnection connection;
	private String username;
	private Roster roster;

	public XmppChat(XMPPConnection connection) {
		SystemUtils.destructOnExit(this);
		
		this.connection = connection;
		this.username = connection.getUser();
		this.roster = connection.getRoster();

		connection.getChatManager().addChatListener(this);

		roster.addRosterListener(this);
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		createGroup();
	}

	public void loop() {
		while (connection.isConnected()) {
			SystemUtils.sleep(500);
		}
	}

	private void createGroup() {
		try {
			LogUtils.d(TAG + "create group: " + GROUP_NAME);
			roster.createGroup(GROUP_NAME);
		} catch (IllegalArgumentException ex) {
			// grouo already exists
		}
	}

	private void addContact(String username) {
		try {
			roster.createEntry(username, username, new String[] { GROUP_NAME });
		} catch (XMPPException ex) {
			LogUtils.w(ex);
		}
	}

	/**
	 * Overridden from ChatManagerListener
	 */
	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		if (!createdLocally) {
			LogUtils.i(TAG + "Waiting for incoming messages in chat: " + chat);
			chat.addMessageListener(this);
		}
	}

	/**
	 * Overridden from MessageListener
	 */
	@Override
	public void processMessage(Chat chat, Message msg) {
		LogUtils.d(TAG + "message: " + msg);

		String otherUser = chat.getParticipant();
		addContact(otherUser);

		String input = msg.getBody();
		if (input != null) {
			LogUtils.i(TAG + otherUser + ": \"" + input + "\"");
			final String output = DataInitializer.processInput(input);
			try {
				chat.sendMessage(output);
			} catch (XMPPException ex) {
				LogUtils.e(ex);
			}
			LogUtils.i(TAG + otherUser + ": \"" + input + "\"");
			LogUtils.i(TAG + username + ": \"" + output + "\"");

		} else {
			LogUtils.d(TAG + "message body is null: " + msg);
		}
	}

	/**
	 * Overridden from RosterListener
	 */
	@Override
	public void entriesAdded(Collection<String> arg0) {
		LogUtils.d(TAG + "entriesAdded: " + arg0);
	}

	/**
	 * Overridden from RosterListener
	 */
	@Override
	public void entriesDeleted(Collection<String> arg0) {
		LogUtils.d(TAG + "entriesDeleted: " + arg0);
	}

	/**
	 * Overridden from RosterListener
	 */
	@Override
	public void entriesUpdated(Collection<String> arg0) {
		LogUtils.d(TAG + "entriesUpdated: " + arg0);
	}

	/**
	 * Overridden from RosterListener
	 */
	@Override
	public void presenceChanged(Presence arg0) {
		LogUtils.d(TAG + "presenceChanged: " + arg0);
	}

	@Override
	public void onExit(int status) {
		connection.disconnect();
	}
}
