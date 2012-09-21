package net.freehal.core.filter;

import net.freehal.core.pos.Tags;
import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlText;

public class FilterQuestionWho implements FactFilter {

	static {
		FactFilters.addFilter(new FilterQuestionWho());
	}

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		LogUtils.d("matches (filter: question who): " + match);

		if (f1.part("questionword").matches(XmlText.fromText("who")) == 1) {
			if (f2.part("clause").countWords() > 0) {
				match *= 0.75;
			}
			if (f2.part("adverbs").countWords() > 0) {
				match *= 0.3;
			}
			if (f2.part("object").countWords() > 0) {
				if (f2.part("object").matches(XmlText.fromText("(a)")) > 0) {
					match *= 0.5;
				} else if (f2.part("object").countTags(new Tags("n", "")) == 0) {
					match *= 0.3;
				}
			} else {
				match *= 0.3;
			}
		}

		return match;
	}
}
