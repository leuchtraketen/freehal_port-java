package net.freehal.core.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.freehal.core.util.ArrayUtils;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;
import net.freehal.core.xml.SynonymProvider;
import net.freehal.core.xml.SynonymProviders;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlList;
import net.freehal.core.xml.XmlObj;
import net.freehal.core.xml.XmlText;
import net.freehal.core.xml.XmlWord;
import net.freehal.core.xml.XmlVariable;

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
		Map<String, String> variablemap = new HashMap<String, String>();
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

	private static boolean matches(XmlList f1, XmlList f2, Map<String, String> variablemap) {
		LogUtils.d("compare: (1) " + f1.printStr() + ", (2) " + f2.printStr());

		if (isIgnoredTree(f1)) {
			return true;

		} else if (isOuterTree(f1)) {
			boolean matches = true;
			for (XmlObj e1 : f1.getEmbedded()) {
				XmlList e2 = f2.part(e1.getName());

				matches = matches && matches((XmlList) e1, e2, variablemap);

				if (!matches)
					break;
			}
			return matches;

		} else if (f1 instanceof XmlText && f2 instanceof XmlText) {
			List<XmlObj> emb1 = f1.getEmbedded();
			List<XmlObj> emb2 = f2.getEmbedded();
			int i = 0;
			int k = 0;
			boolean matches1 = true;
			for (; i < emb1.size() && matches1; ++i) {
				boolean matches2 = false;
				for (; k < emb2.size() * 2 && !matches2; ++k) {
					int j = k % emb2.size();
					XmlObj left = emb1.get(i);
					XmlObj right = emb2.get(j);
					if (left instanceof XmlVariable)
						matches2 = matches2
								|| matches((XmlVariable) left, ArrayUtils.partOfList(emb2, j, 0), variablemap);
					else if (left instanceof XmlWord)
						matches2 = matches2 || matches((XmlWord) left, right, variablemap);
					else
						matches2 = matches2 || matches((XmlList) left, right, variablemap);
				}

				k %= emb2.size();
				if (k >= emb2.size())
					k = 0;

				matches1 = matches1 && matches2;
			}

			return matches1;

		} else if (f1 instanceof XmlList.AndOperation) {
			boolean matches = true;
			for (XmlObj e1 : f1.getEmbedded()) {
				matches = matches && matches((XmlList) e1, f2, variablemap);
				if (!matches)
					break;
			}
			return matches;

		} else if (f1 instanceof XmlList) {
			boolean matches = false;
			for (XmlObj e1 : f1.getEmbedded()) {
				if (e1 instanceof XmlWord)
					matches = matches || matches((XmlWord) e1, f2, variablemap);
				else
					matches = matches || matches((XmlList) e1, f2, variablemap);
				if (matches)
					break;
			}
			return matches;

		} else {
			throw new IllegalArgumentException("Unknown: f1=" + f1.printXml() + ", f2=" + f2.printStr()
					+ ", " + (f1 instanceof XmlText) + "," + (f1 instanceof XmlList));
		}
	}

	private static boolean matches(XmlList left, XmlObj right, Map<String, String> variablemap) {
		boolean matches = false;
		for (XmlObj e1 : left.getEmbedded()) {
			if (e1 instanceof XmlWord)
				matches = matches || matches((XmlWord) e1, right, variablemap);
			else
				matches = matches || matches((XmlList) e1, right, variablemap);
			if (matches)
				break;
		}
		return matches;
	}

	private static boolean matches(XmlWord left, XmlObj right, Map<String, String> variablemap) {
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
			return matches(left, (XmlWord) right, variablemap);
		} else
			return false;
	}

	private static boolean matches(XmlWord f1, XmlWord f2, Map<String, String> variablemap) {
		LogUtils.d("compare (word): (1) " + f1.printStr() + ", (2) " + f2.printStr());

		String text1 = f1.printText();
		String text2 = f2.printText();
		if (text1.equals(text2)) {
			LogUtils.d("equal (string): '" + text1 + "', '" + text2 + "'");
			return true;
		} else {
			LogUtils.d("not equal: '" + text1 + "', '" + text2 + "'");
			return false;
		}
	}

	private static boolean matches(XmlVariable left, List<XmlObj> right, Map<String, String> variablemap) {
		LogUtils.d("compare (variable): (1) " + left.printStr() + ", (2) " + right);

		String text1 = left.printText();

		List<XmlObj> after = left.getAfter();
		List<String> words = new ArrayList<String>();
		for (XmlObj e2 : right) {
			if (after.size() != 0 && after.get(0) == e2) {
				LogUtils.d("first element of after-variable array reached: after.size()=" + after.size()
						+ ", after.get(0)=" + after.get(0) + ", e2=" + e2);
				break;
			}

			words.add(e2.printText());
		}
		String text2 = StringUtils.join(" ", words);

		LogUtils.d("equal (regex): '" + text1 + "', '" + text2 + "'");
		variablemap.put(text1, text2);
		LogUtils.d("variable: '" + text1 + "'='" + text2 + "'");

		return true;
	}

	@SuppressWarnings("unused")
	private static boolean __matches(XmlWord f1, XmlWord f2, Map<String, String> variablemap) {
		LogUtils.d("compare (word): (1) " + f1.printStr() + ", (2) " + f2.printStr());

		String text1 = f1.printText();
		List<String> variables = new ArrayList<String>();
		List<String> groups;
		while ((groups = RegexUtils.match(text1, XmlVariable.REGEX_VARIABLE)) != null) {
			variables.add(groups.get(0));
			text1 = RegexUtils.replace(text1, java.util.regex.Pattern.quote(groups.get(0)), "(.*)");
		}

		String text2 = f2.printText();

		if (text1.equals(text2)) {
			LogUtils.d("equal (string): '" + text1 + "', '" + text2 + "'");
			return true;
		} else if ((groups = RegexUtils.match(text2, text1)) != null) {
			LogUtils.d("equal (regex): '" + text1 + "', '" + text2 + "'");
			for (int i = 0; i < groups.size(); ++i) {
				final String variable = i < variables.size() ? variables.get(i) : "unknown";
				final String value = groups.get(i);
				variablemap.put(variable, value);
				LogUtils.d("variable: '" + variable + "'='" + value + "'");
			}
			return true;
		} else {
			LogUtils.d("not equal: '" + text1 + "', '" + text2 + "'");
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
