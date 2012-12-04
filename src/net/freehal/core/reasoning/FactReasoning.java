package net.freehal.core.reasoning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.background.IdleActivity;
import net.freehal.core.pos.TaggerUtils;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.xml.FactProvider;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlList;
import net.freehal.core.xml.XmlObj;

public class FactReasoning implements FactProvider, IdleActivity {

	private FactProvider backend;

	public FactReasoning(FactProvider backend) {
		this.backend = backend;
	}

	@Override
	public Set<XmlFact> findFacts(List<Word> words) {
		LogUtils.i("find by words: " + words);

		Set<XmlFact> found = new HashSet<XmlFact>();

		LogUtils.i(found.size() + " facts found.");

		return found;
	}

	@Override
	public void doIdle() {
		updateConclusions();
	}

	private void updateConclusions() {
		LogUtils.i("start: update conclusions");

		LogUtils.i("retrieve consequence questionwords...");
		XmlObj inputWords = Patterns.getConsequenceQuestionwords();

		LogUtils.i("retrieve consequence facts...");
		Set<XmlFact> consequenceFacts = backend.findFacts(inputWords.getWords());
		LogUtils.i("filter consequence facts...");
		Set<XmlFact> filteredConsequenceFacts = Patterns.filterConsequenceFacts(consequenceFacts);

		LogUtils.i("create patterns...");
		Set<Pattern> patterns = new HashSet<Pattern>();
		for (XmlFact xfact : filteredConsequenceFacts) {
			patterns.addAll(Patterns.createPatterns(xfact));
		}

		LogUtils.i("match patterns...");
		for (Pattern p : patterns) {
			LogUtils.d(p);
			Set<XmlFact> facts = backend.findFacts(TaggerUtils.getIndexWords(p.getPremise()));
			for (XmlFact xfact : facts) {
				Mutable<XmlList> conclusion = new Mutable<XmlList>();
				if (p.matches(xfact, conclusion)) {
					LogUtils.i("found conclusion: " + conclusion.get());
				}
			}
		}

		LogUtils.i("stop: update conclusions");
	}

}
