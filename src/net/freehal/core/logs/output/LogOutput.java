package net.freehal.core.logs.output;

public interface LogOutput {

	void append(String line);

	void changeLast(String line);

	void flush();
}