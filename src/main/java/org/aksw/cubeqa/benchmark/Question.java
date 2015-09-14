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
	/** The natural language question representation.*/
	public final String string;
	/** correct SPARQL query to answer the question*/
	public final String query;
	/** answer set containing a map from variable name to value*/
	public final Set<Map<String,String>> answers;
	public final Map<String,DataType> dataTypes;

	public Question(String cubeUri, String string, String query)
	{
		this.cubeUri=cubeUri;
		this.string=string;
		this.query=query;
		this.answers=null;
		this.dataTypes=null;
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
		this.dataTypes=Collections.unmodifiableMap(answerTypes);
	}

}