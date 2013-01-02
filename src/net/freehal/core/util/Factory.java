package net.freehal.core.util;

public interface Factory<A> {
	public A newInstance(String... params);
}
