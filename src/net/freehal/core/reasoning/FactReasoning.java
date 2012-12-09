package net.freehal.core.reasoning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.background.IdleActivity;
import net.freehal.core.pos.TaggerUtils;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;
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
		LogUtils.startProgress(0, 1, 4);

		LogUtils.updateProgress("thinking: initializing");
		LogUtils.i("retrieve consequence questionwords...");
		XmlObj inputWords = Patterns.getConsequenceQuestionwords();
		LogUtils.updateProgress();

		LogUtils.updateProgress("thinking, step 1: premises");
		LogUtils.i("retrieve consequence facts...");
		Set<XmlFact> consequenceFacts = backend.findFacts(inputWords.getWords());
		LogUtils.i("filter consequence facts...");
		Set<XmlFact> filteredConsequenceFacts = Patterns.filterConsequenceFacts(consequenceFacts);
		LogUtils.updateProgress();

		LogUtils.i("create patterns...");
		List<Pattern> patterns = new ArrayList<Pattern>();
		for (XmlFact xfact : filteredConsequenceFacts) {
			patterns.addAll(Patterns.createPatterns(xfact));
		}
		LogUtils.updateProgress();

		FreehalFile conclusionsFile = Storages.inLanguageDirectory("conclusions.xml");
		conclusionsFile.delete();

		LogUtils.updateProgress("thinking, step 2: conclusions");
		LogUtils.i("match patterns...");
		//LogUtils.addTemporaryFilter("Pattern", "debug");
		LogUtils.startProgress(0, 1, patterns.size());
		for (Pattern p : patterns) {
			LogUtils.d(p);
			LogUtils.i("searching for facts matching premise: " + p.getPremise());
			Set<XmlFact> facts = backend.findFacts(TaggerUtils.getIndexWords(p.getPremise()));
			for (XmlFact xfact : facts) {
				Mutable<XmlList> conclusion = new Mutable<XmlList>();
				if (p.matches(xfact, conclusion)) {
					LogUtils.i("found conclusion: " + conclusion.get());
					conclusionsFile.append(conclusion.get().printXml() + "\n");
				} else {
					LogUtils.i("does not matches premise: " + xfact);
				}
				conclusion = null;
			}
			LogUtils.updateProgress();
			p = null;
			facts = null;
		}
		LogUtils.stopProgress();
		LogUtils.resetTemporaryFilters();
		LogUtils.updateProgress();

		LogUtils.stopProgress();
		LogUtils.i("stop: update conclusions");
	}

}
