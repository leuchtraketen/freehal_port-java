package net.freehal.ui.common;

import org.apache.commons.cli.Options;

public interface Extension {

	void addOptions(Options options);

	void parseConfig(Configuration line);

}
