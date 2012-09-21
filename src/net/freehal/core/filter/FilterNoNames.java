package net.freehal.core.filter;

import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlText;

public class FilterNoNames implements FactFilter {

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		LogUtils.d("matches (filter: no names): " + match);

		if (f2.part("verb").matches(XmlText.fromText("is-a")) == 1) {
			if (f2.part("object").matches(XmlText.fromText("(a) name")) == 2) {
				match *= 0.1;
			}
		}
		
		return match;
	}
}
