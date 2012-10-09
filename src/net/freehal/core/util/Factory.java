package net.freehal.core.util;

public interface Factory<A, B> {
	public A newInstance(B b);
}
