package net.freehal.core.util;

public class Mutable<A> {
	private A a;

	public Mutable() {
		this.a = null;
	}

	public Mutable(A a) {
		this.a = a;
	}

	public Mutable<A> set(A a) {
		this.a = a;
		return this;
	}

	public A get() {
		return this.a;
	}

	public String toString() {
		return a != null ? a.toString() : "null";
	}

	public boolean equals(Object o) {
		return o.equals(a);
	}
}
