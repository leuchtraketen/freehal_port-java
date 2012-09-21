package net.freehal.core.grammar.typedefs;

import net.freehal.core.util.Mutable;

public class MutableInteger extends Mutable<Integer> {
	public MutableInteger() {
		super();
	}

	public MutableInteger(Integer i) {
		super(i);
	}

	public MutableInteger increment() {
		return add(1);
	}

	public MutableInteger decrement() {
		return add(-1);
	}

	public MutableInteger add(Integer i) {
		set(get() + i);
		return this;
	}
}
