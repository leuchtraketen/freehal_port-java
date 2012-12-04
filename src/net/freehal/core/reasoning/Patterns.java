package net.freehal.core.reasoning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.SynonymProviders;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlList;
import net.freehal.core.xml.XmlObj;
import net.freehal.core.xml.XmlText;

public class Patterns {

	public static boolean isConsequenceFact(XmlFact xfact) {
		List<XmlObj> clauses = new ArrayList<XmlObj>();
		if (xfact.part(clauses, "clause") > 0) {
			for (XmlObj clause : clauses) {
				if (clause instanceof XmlList) {
					if (((XmlList) clause).part("questionword").matches(getConsequenceQuestionwords()) > 0) {
						return true;
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public static Set<XmlFact> filterConsequenceFacts(Collection<XmlFact> consequences) {
		Set<XmlFact> filtered = new HashSet<XmlFact>();
		for (XmlFact xfact : consequences) {
			if (Patterns.isConsequenceFact(xfact)) {
				filtered.add(xfact);
				LogUtils.i("is a consequence fact: " + xfact.printStr());
			} else {
				LogUtils.i("is not a consequence fact: " + xfact.printStr());
			}
		}
		return filtered;
	}

	public static XmlObj getConsequenceQuestionwords() {
		XmlList list = new XmlList();
		list.add(XmlText.fromText("if"));
		list.add(XmlText.fromText("when"));
		list.insertSynonyms(SynonymProviders.getSynonymProvider());
		return list;
	}

	public static Collection<? extends Pattern> createPatterns(XmlFact xfact) {
		Set<Pattern> patterns = new HashSet<Pattern>();
		if (isConsequenceFact(xfact)) {
			List<XmlObj> clauses = new ArrayList<XmlObj>();
			xfact.part(clauses, "clause");

			XmlList conclusion = new XmlList();
			conclusion.setName("conclusion");
			for (XmlObj obj : xfact.getEmbedded())
				if (!obj.getName().equals("clause"))
					conclusion.add(obj);

			List<XmlList> premises = new ArrayList<XmlList>();

			for (XmlObj clause : clauses) {
				if (clause instanceof XmlList) {
					if (((XmlList) clause).part("questionword").matches(getConsequenceQuestionwords()) > 0) {
						clause.setName("premise");
						premises.add((XmlList) clause);
					} else {
						conclusion.add(clause);
					}
				}
			}

			for (XmlList premise : premises) {
				patterns.add(new Pattern(premise, conclusion));
			}
			
			LogUtils.d("premises: " + premises);
			LogUtils.d("conclusion: " + conclusion);
		}
		return patterns;
	}

}
