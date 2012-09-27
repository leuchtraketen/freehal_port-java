package net.freehal.core.filter;

import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlList;

public class FilterQuestionExtra implements FactFilter {

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		LogUtils.d("matches (filter: question extra): " + match);
		LogUtils.d("f1.part(\"extra\") = " + f1.part("extra"));
		LogUtils.d("f1.part(\"extra\").countWords() = "
				+ f1.part("extra").countWords());

		if (f1.part("extra").countWords() > 0) {
			XmlList extra1 = f1.part("extra");
			XmlList subject2 = f2.part("subject");
			XmlList object2 = f2.part("object");
			XmlList adverbs2 = f2.part("adverbs");

			LogUtils.d("question_extra: (extra1 isLike subject2) = "
					+ extra1.isLike(subject2) + ", (extra1 isLike object2) = "
					+ extra1.isLike(object2) + ", (extra1 isLike adverbs2) = "
					+ extra1.isLike(adverbs2) + ", fact = " + f2);

			double _m = extra1.isLike(subject2) + extra1.isLike(object2)
					+ extra1.isLike(adverbs2);
			if (_m > 0)
				match += _m;
			else
				match *= 0.75;
			LogUtils.d("m = " + match + ", _m = " + _m);
		}

		return match;
	}
}
