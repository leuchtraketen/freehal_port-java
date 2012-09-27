package net.freehal.core.answer;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.parser.Sentence;

public class AnswerProviders implements AnswerProvider {

	private static AnswerProviders singleton = new AnswerProviders();

	private List<AnswerProvider> providers = null;

	private AnswerProviders() {
		providers = new ArrayList<AnswerProvider>();
	}

	public static AnswerProviders getInstance() {
		return singleton;
	}

	public AnswerProviders add(AnswerProvider filter) {
		if (providers == null) {
			providers = new ArrayList<AnswerProvider>();
		}
		providers.add(filter);
		return this;
	}

	@Override
	public String getAnswer(Sentence s) {
		String answer = null;
		for (AnswerProvider a : providers) {
			if (answer == null) {
				answer = a.getAnswer(s);
				
				Runtime.getRuntime().gc();
			}
		}
		return answer;
	}
}
