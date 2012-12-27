package net.freehal.core.logs.receiver;

import net.freehal.core.logs.output.LogOutput;
import net.freehal.core.util.RegexUtils;

public class UncoloredLog implements LogReceiver {

	private LogOutput stream;

	public UncoloredLog(LogOutput stream) {
		this.stream = stream;
	}

	@Override
	public void addLine(String type, String line, StackTraceElement stacktrace) {
		final String prefix = StackTraceUtils.whereInCode(stacktrace) + "(" + type + ") "
				+ (type.length() == 4 ? " " : "");

		// remove all carriage returns
		if (line.contains("\\r")) {
			line = RegexUtils.replace(line, "\\\\r", "");
			stream.append(prefix + line);
		} else {
			stream.append(prefix + line);
		}
	}

	@Override
	public void flush() {
		stream.flush();
	}
}
