package org.aksw.cubeqa.benchmark;

import java.util.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**A single benchmark question along with a correct SPARQL query.*/
@ToString
@EqualsAndHashCode
public class Question
{
	public final String cubeUri;
	public final String string;
	public final String query;
	public final Set<Map<String,String>> answers;
	public final Map<String,DataType> answerTypes;

	public Question(String cubeUri, String string, String query)
	{
		this.cubeUri=cubeUri;
		this.string=string;
		this.query=query;
		this.answers=null;
		this.answerTypes=null;
	}

	public Question(String cubeUri, String string, String query, Set<Map<String,String>> answers, Map<String,DataType> answerTypes)
	{
		this.cubeUri=cubeUri;
		this.string=string;
		this.query=query;
//		if(answers.isEmpty()) {throw new IllegalArgumentException("empty answer set");}
//		answers.stream().filter(Map::isEmpty).findFirst().ifPresent
//		(x->{throw new IllegalArgumentException("empty answer for question "+string+", query "+query);});
		this.answers=Collections.unmodifiableSet(answers);
		this.answerTypes=Collections.unmodifiableMap(answerTypes);
	}

}