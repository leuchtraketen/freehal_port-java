package net.freehal.core.filter;

import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlText;

public class FilterNot implements FactFilter {

	@Override
	public double filter(XmlFact f1, XmlFact f2, double match) {
		LogUtils.d( "matches (filter: not): " + match);

		if (f2.part("adverbs").matches(XmlText.fromText("nicht")) >= 1 ||
				f2.part("adverbs").matches(XmlText.fromText("not")) >= 1) {
			match *= 0.25;
		}
		if (f2.part("truth").matches(XmlText.fromText("0")) >= 1) {
			match *= 0.1;
		}
		return match;
	}
}
