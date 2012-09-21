package net.freehal.core.filter;

import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlText;

public class FilterQuestionWhat implements FactFilter {

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		LogUtils.d("matches (filter: question what): " + match);

		if (f1.part("questionword").matches(XmlText.fromText("what")) == 1) {
			if (f2.part("clause").countWords() > 0) {
				match *= 0.9;
			}
			if (f2.part("object").countWords() > 0) {
				if (f2.part("object").matches(XmlText.fromText("(a)")) > 0) {
					match *= 2;
				} else {
					match *= 0.7;
				}
			} else {
				match *= 0.3;
			}
		}

		return match;
	}
}
