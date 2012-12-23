package net.freehal.core.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.freehal.core.util.ArrayUtils;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.xml.SynonymProvider;
import net.freehal.core.xml.SynonymProviders;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlList;
import net.freehal.core.xml.XmlObj;
import net.freehal.core.xml.XmlText;
import net.freehal.core.xml.XmlVariable;
import net.freehal.core.xml.XmlWord;

public class Pattern {

	private static final XmlObj CONCLUSION_MARKER = createConclusionMarker();

	private static final SynonymProvider selfSynonymProvider = new SynonymProviders.NullSynonymProvider();

	private XmlList premise;
	private XmlList conclusion;

	public Pattern(XmlList premise, XmlList conclusion) {
		// premise.trim();
		// conclusion.trim();
		premise.insertSynonyms(SynonymProviders.getSynonymProvider());
		// premise.expandSynonyms();
		this.premise = premise;
		this.conclusion = conclusion;
	}

	private static XmlObj createConclusionMarker() {
		XmlList tmp = new XmlList();
		tmp.setName("is_conclusion");
		tmp.add(XmlText.fromText("1"));
		return tmp;
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

		Map<String, List<XmlObj>> variablemap = new HashMap<String, List<XmlObj>>();
		xfact.insertSynonyms(selfSynonymProvider);

		if (Pattern.matches(premise, xfact, variablemap)) {
			if (conclusionHolder != null) {
				XmlList modifiedConclusion = (XmlList) conclusion.copy();
				modifiedConclusion.insertVariables(variablemap);
				modifiedConclusion.setName("fact");
				modifiedConclusion.add(Pattern.CONCLUSION_MARKER);
				
				XmlList origin = new XmlList();
				origin.setName("origin");
				origin.addAll(xfact);
				modifiedConclusion.add(origin);

				conclusionHolder.set(modifiedConclusion);
			}
			return true;

		} else {
			return false;
		}
	}

	private static boolean isIgnoredTree(XmlObj tree) {
		final String tagname = tree.getName();
		return tagname.equals("truth") || tagname.equals("questionword");
	}

	private static boolean isOuterTree(XmlObj tree) {
		return tree.isName("subject") || tree.isName("object") || tree.isName("verb")
				|| tree.isName("adverbs") || tree.isName("fact") || tree.isName("premise")
				|| tree.isName("conclusion");
	}

	private static String whatis(XmlObj o) {
		if (o instanceof XmlVariable)
			return "XmlVariable";
		else if (o instanceof XmlText)
			return "XmlText";
		else if (o instanceof XmlWord)
			return "XmlWord";
		else if (o instanceof XmlFact)
			return "XmlFact";
		else if (o instanceof XmlList)
			return "XmlList";
		else
			return "XmlObj";
	}

	private static boolean matches(XmlObj left, XmlObj right, Map<String, List<XmlObj>> variablemap) {
		LogUtils.d("compare (" + whatis(left) + "," + whatis(right) + "): (1) " + left.printStr() + ", (2) "
				+ right.printStr());

		if (left instanceof XmlWord) {
			if (right instanceof XmlList) {
				boolean matches = false;
				for (XmlObj e2 : ((XmlList) right).getEmbedded()) {
					if (e2 instanceof XmlWord)
						matches = matches || matches(left, (XmlWord) e2, variablemap);
					else
						matches = matches || matches(left, (XmlList) e2, variablemap);
					if (matches)
						break;
				}
				return matches;
			} else if (right instanceof XmlWord) {
				String text1 = left.printText();
				String text2 = right.printText();
				if (text1.equals(text2)) {
					LogUtils.d("equal: compare (XmlWord,XmlWord): (1) " + left.printStr() + ", (2) "
							+ right.printStr());
					return true;
				} else {
					LogUtils.d("not equal: compare (XmlWord,XmlWord): (1) " + left.printStr() + ", (2) "
							+ right.printStr());
					return false;
				}
			} else {
				return false;
			}

		} else if (left instanceof XmlText && right instanceof XmlText) {
			List<XmlObj> emb1 = ((XmlList) left).getEmbedded();
			List<XmlObj> emb2 = ((XmlList) right).getEmbedded();
			int i = 0;
			int k = 0;
			boolean matches1 = true;
			for (; i < emb1.size() && matches1; ++i) {
				boolean matches2 = false;
				for (; k < emb2.size() * 2 && !matches2; ++k) {
					int j = k % emb2.size();
					XmlObj l = emb1.get(i);
					XmlObj r = emb2.get(j);
					if (l instanceof XmlVariable)
						matches2 = matches2
								|| matches((XmlVariable) l, ArrayUtils.partOfList(emb2, j, 0), variablemap);
					else if (l instanceof XmlWord)
						matches2 = matches2 || matches((XmlWord) l, r, variablemap);
					else
						matches2 = matches2 || matches((XmlList) l, r, variablemap);
				}

				k %= emb2.size();
				if (k >= emb2.size())
					k = 0;

				matches1 = matches1 && matches2;
			}
			return matches1;

		} else if (left instanceof XmlList) {
			if (isIgnoredTree(left)) {
				return true;

			} else if (isOuterTree(left)) {
				if (right instanceof XmlList) {
					boolean matches = true;
					for (XmlObj e1 : ((XmlList) left).getEmbedded()) {
						XmlList e2 = ((XmlList) right).part(e1.getName());

						matches = matches && matches(e1, e2, variablemap);

						if (!matches)
							break;
					}
					return matches;

				} else {
					boolean matches = false;
					for (XmlObj e1 : ((XmlList) left).getEmbedded()) {
						matches = matches || matches(e1, right, variablemap);
						if (matches)
							break;
					}
					return matches;
				}
			} else {
				boolean matches = false;
				for (XmlObj e1 : ((XmlList) left).getEmbedded()) {
					matches = matches || matches(e1, right, variablemap);
					if (matches)
						break;
				}
				return matches;

			}
		} else {
			throw new IllegalArgumentException("unknown: left=" + left.printXml() + ", right="
					+ right.printStr());
		}
	}

	private static boolean matches(XmlVariable left, List<XmlObj> right, Map<String, List<XmlObj>> variablemap) {
		LogUtils.d("compare (XmlVariable,List<XmlObj>): (1) " + left.printStr() + ", (2) " + right);

		String text1 = left.printText();

		List<XmlObj> after = left.getAfter();
		List<XmlObj> words = new ArrayList<XmlObj>();
		for (XmlObj e2 : right) {
			if (after.size() != 0 && after.get(0) == e2) {
				LogUtils.d("first element of after-variable array reached: after.size()=" + after.size()
						+ ", after.get(0)=" + after.get(0) + ", e2=" + e2);
				break;
			}

			words.add(e2);
		}
		// String text2 = StringUtils.join(" ", words);

		LogUtils.d("  equal (regex): '" + text1 + "', '" + words + "'");
		variablemap.put(text1, words);
		LogUtils.d("  variable: '" + text1 + "'='" + words + "'");

		return true;
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
