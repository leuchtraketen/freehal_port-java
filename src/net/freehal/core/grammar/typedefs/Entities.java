package net.freehal.core.grammar.typedefs;

import java.util.ArrayList;

import net.freehal.core.grammar.Entity;
import net.freehal.core.grammar.Grammar2012;

public class Entities extends ArrayList<Entity> {

	private static final long serialVersionUID = 1658375404776969761L;

	public Entities() {
		super();
	}

	public Entities(Entities entities) {
		super(entities);
	}

	public void add(Entities entities, Entity previous, Grammar2012 grammar) {

		// / cout << "get_virt: size=" << previous->get_virt().size() << endl;
		if (previous.getMarker().size() > 0) {
			for (Entity it : entities) {
				Entity e = grammar.modifySymbol(it, previous.getMarker());
				this.add(e);
			}
		} else {
			for (Entity it : entities) {
				this.add(it);
			}
		}
	}

	public String print() {
		StringBuilder ss = new StringBuilder();
		ss.append("[");
		for (Entity i : this) {
			if (i != this.get(0))
				ss.append(", ");
			ss.append(i.print());
		}
		ss.append("]");
		return ss.toString();

	}
}
