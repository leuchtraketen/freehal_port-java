package net.freehal.core.logs.listener;

import java.util.Iterator;

import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;

public class LogRecorder implements LogDestination {

	private static StackTraceElementStringSerializer stes = new StackTraceElementStringSerializer();
	private FreehalFile logfile;

	public LogRecorder(FreehalFile logfile) {
		this.logfile = logfile;
	}

	@Override
	public void addLine(String type, String e, StackTraceElement stacktrace) {
		logfile.append(type + "\n" + e + "\n" + stes.toString(stacktrace) + "\n");
	}

	public void play() {
		Iterator<String> iter = logfile.readLines().iterator();
		while (iter.hasNext()) {
			String type = iter.next();
			if (!iter.hasNext())
				break;
			String e = iter.next();
			if (!iter.hasNext())
				break;
			String stacktraceString = iter.next();
			StackTraceElement stacktrace = stes.fromString(stacktraceString);
			LogUtils.get().addLine(type, e, stacktrace);
		}
	}

	@Override
	public void flush() {}

}
