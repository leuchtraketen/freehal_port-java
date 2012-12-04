package net.freehal.core.reasoning;

import net.freehal.core.util.Mutable;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlList;

public class Pattern {

	private XmlList premise;
	private XmlList conclusion;

	public Pattern(XmlList premise, XmlList conclusion) {
		this.premise = premise;
		this.conclusion = conclusion;
	}

	public XmlList getPremise() {
		return premise;
	}

	public XmlList getConclusion() {
		return conclusion;
	}

	public boolean matches(XmlFact xfact) {
		return matches(xfact, null);
	}

	public boolean matches(XmlFact xfact, Mutable<XmlList> conclusionHolder) {
		if (xfact.isLike(premise) > 0) {
			
			if (conclusionHolder != null)
				conclusionHolder.set(conclusion);
			
			return true;
		} else {
			return false;
		}
	}

	public String printStr() {
		return premise.printStr() + " -> " + conclusion.printStr();
	}

	public String printText() {
		return premise.printText() + " -> " + conclusion.printText();
	}

	@Override
	public String toString() {
		return printStr();
	}

	protected String hashString() {
		StringBuilder ss = new StringBuilder();
		ss.append(premise.printText());
		ss.append(conclusion.printText());
		return ss.toString();
	}

	@Override
	public int hashCode() {
		return hashString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.hashCode() == other.hashCode();
	}
}
