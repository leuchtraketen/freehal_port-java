package net.freehal.core.database;

import java.util.List;

import net.freehal.core.answer.AnswerProvider;
import net.freehal.core.parser.Sentence;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Ranking;
import net.freehal.core.xml.XmlFact;

public class DatabaseAnswerProvider implements AnswerProvider {

	private DatabaseImpl database;
	
	public DatabaseAnswerProvider(DatabaseImpl database) {
		this.database = database;
	}

	@Override
	public String getAnswer(Sentence s) {
		if (!s.isValidFact()) {
			return null;
		}
		
		XmlFact input = s.getFact();
		List<XmlFact> possibleAnswers = database.findFacts(input);
		Ranking<XmlFact> rank = input.ranking(possibleAnswers);
		for (int i = 0; i < rank.size(); ++i) {
			for (XmlFact xfact : rank.get(i)) {
				LogUtils.i(rank.rank(i) + ": " + xfact.printStr());
			}
		}
		XmlFact best = rank.getBestOne();
		return best.printStr();
	}

}
