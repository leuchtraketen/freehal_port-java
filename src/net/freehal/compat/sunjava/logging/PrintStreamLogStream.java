package net.freehal.compat.sunjava.logging;

import java.io.PrintStream;


public abstract class PrintStreamLogStream extends AbstractLogStream {
	private PrintStream out;

	public PrintStreamLogStream(PrintStream out) {
		this.out = out;
	}

	public PrintStream getStream() {
		return out;
	}

	@Override
	public void flush() {
		out.flush();
	}

	protected void println(String string) {
		out.println(string);
	}

	protected void print(String string) {
		out.print(string);
	}
}