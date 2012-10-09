package net.freehal.plugin.wikipedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.freehal.core.answer.AnswerProvider;
import net.freehal.core.parser.Sentence;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;
import net.freehal.core.xml.FactProvider;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlText;

public class WikipediaPlugin implements FactProvider, AnswerProvider {

	private WikipediaLanguageUtils langUtils;

	public WikipediaPlugin(WikipediaLanguageUtils langUtils) {
		this.langUtils = langUtils;
	}

	@Override
	public String getAnswer(Sentence input) {
		XmlFact fact = input.getFact();

		if (fact.part("questionword").matches(XmlText.fromText("what")) == 1
				&& fact.part("verb").matches(XmlText.fromText("is-a")) == 1) {

			final String rawName = fact.part("subject").printText() + " " + fact.part("object").printText();
			final String name = toArticleName(rawName);
			if (name.length() > 0) {
				return getShortDescription(name);
			}
		}

		// if ((matched = RegexUtils.imatch(input.getInput(),
		// "was ist ([^?!.]+)")) != null) {
		return null;
	}

	private String toArticleName(String name) {
		name = StringUtils.trim(name);
		name = RegexUtils.trim(name, "_");
		name = StringUtils.trim(name);
		name = StringUtils.ucfirst(name);
		return name;
	}

	private String getShortDescription(String name) {
		LogUtils.i("Searching a short description for: \"" + name + "\"");

		final Iterable<String> html = FreehalFiles.getFile("wikipedia", name).readLines();
		List<String> desc = new ArrayList<String>();
		final int limitLines = 3;

		for (String line : html) {
			// skip empty lines
			line = StringUtils.trim(line);
			if (line.length() == 0)
				continue;
			// skip disambiguation links
			if (langUtils.lineContainsDisambiguation(line))
				continue;
			// skip images
			if (langUtils.lineContainsFile(line))
				continue;
			// skip templates
			if (line.contains("[[Wikipedia:"))
				continue;
			// skip headings
			if (line.startsWith("=="))
				continue;
			// skip infoboxes
			if (line.contains("{{") || line.contains("}}") || (line.startsWith("|") && line.contains("=")))
				continue;

			// add sentences to description
			// is it the beginning of a list?
			if (line.endsWith(":") && !line.contains("."))
				desc.add(line + "\n");
			// or a list element
			else if (line.startsWith("*"))
				desc.add(line + "\n");
			// or a normal paragraph...
			else if (desc.size() < limitLines) {

				for (String sentence : line.split("\\.( |$)")) {
					sentence = sentence.trim();
					if (sentence.length() > 0 && desc.size() < limitLines)
						desc.add(sentence + ". ");
				}
			} else
				break;
		}

		if (desc.size() > 0)
			return toPlainText(StringUtils.join("", desc));
		else
			return null;
	}

	private String toPlainText(String code) {
		// remove [[link|text]]
		code = RegexUtils.replace(code, "\\[\\[([^\\]|]+)[|]([^\\]|]+)\\]\\]", "$2");
		// remove [[link]]
		code = RegexUtils.replace(code, "\\[\\[([^\\]|]+)\\]\\]", "$1");
		// remove ''text'' and '''text'''
		code = RegexUtils.replace(code, "[']+([^']+)[']+", "$1");
		return code;
	}

	@Override
	public Set<XmlFact> findFacts(XmlFact xfact) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public Set<XmlFact> findFacts(List<Word> words) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public Set<XmlFact> findFacts(Word word) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}
}
