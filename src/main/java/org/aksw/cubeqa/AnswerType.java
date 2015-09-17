package org.aksw.cubeqa;

import java.util.*;
import de.konradhoeffner.commons.Pair;

public enum AnswerType
{
	UNCOUNTABLE,COUNT,COUNTABLE,TEMPORAL,AFFIRMATIVE,LOCATION,ENTITY;

	/** map of lower case question words to expected answer types*/
	static public Map<String,EnumSet<AnswerType>> ofQuestionWord = new HashMap<>();

	static
	{
		// what and which debatable
		ofQuestionWord.put("what", EnumSet.of(UNCOUNTABLE,COUNTABLE,TEMPORAL,LOCATION,ENTITY));
		ofQuestionWord.put("which", EnumSet.of(TEMPORAL,LOCATION,ENTITY));
		ofQuestionWord.put("how many",EnumSet.of(COUNTABLE,COUNT));
		ofQuestionWord.put("how much",EnumSet.of(UNCOUNTABLE));
		ofQuestionWord.put("when", EnumSet.of(TEMPORAL));
		Arrays.asList("is","do","are","where","was","did").stream()
		.forEach(w->ofQuestionWord.put(w, EnumSet.of(AFFIRMATIVE)));
		ofQuestionWord.put("where", EnumSet.of(LOCATION));
	}

	static public EnumSet<AnswerType> ofQuestion(String question)
	{
		return ofQuestionWord.get(
		ofQuestionWord.keySet().stream().filter(w->question.toLowerCase().startsWith(w)).findFirst().orElse("what")); // default to "what", which can be anything
	}

	static public Optional<Pair<String,EnumSet<AnswerType>>> eatAndQuestionWord(String question)
	{
		Optional<String> questionWord = ofQuestionWord.keySet().stream().filter(w->question.toLowerCase().startsWith(w)).findFirst();
		if(!questionWord.isPresent()) {return Optional.empty();}
		return Optional.of(new Pair<>(questionWord.get(), ofQuestionWord.get(questionWord.get())));
	}

}