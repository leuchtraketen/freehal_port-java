package net.freehal.ui.xmpp;

import net.freehal.core.util.LogUtils;
import net.freehal.ui.common.DataInitializer;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class IncomingMessageListener implements MessageListener {

	@Override
	public void processMessage(Chat chat, Message msg) {
		String input = msg.getBody();
		final String output = DataInitializer.processInput(input);
		try {
			chat.sendMessage(output);
		} catch (XMPPException ex) {
			LogUtils.e(ex);
		}
	}

}
