package net.freehal.core.logs.output;

import java.io.PrintStream;


public class PrintStreamLog implements LogOutput {

	private PrintStream stream;

	public PrintStreamLog(PrintStream stream) {
		this.stream = stream;
	}

	@Override
	public void append(String line) {
		stream.println(line);
	}

	@Override
	public void changeLast(String line) {
		stream.print(line + "\r");
	}

	@Override
	public void flush() {
		stream.flush();
	}

}