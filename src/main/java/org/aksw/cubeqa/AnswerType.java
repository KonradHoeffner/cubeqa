package org.aksw.cubeqa;

import java.util.*;

public enum AnswerType
{
	UNCOUNTABLE,COUNTABLE,TEMPORAL,AFFIRMATIVE,LOCATION;

	static public Map<String,EnumSet<AnswerType>> ofQuestionWord = new HashMap<>();

	static
	{
		ofQuestionWord.put("what", EnumSet.allOf(AnswerType.class));
		ofQuestionWord.put("which", EnumSet.allOf(AnswerType.class));
		ofQuestionWord.put("how many",EnumSet.of(COUNTABLE));
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
}