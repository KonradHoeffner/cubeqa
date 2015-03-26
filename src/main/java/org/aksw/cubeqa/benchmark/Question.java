package org.aksw.cubeqa.benchmark;

import java.util.Map;
import java.util.Set;
import lombok.*;

/**A single benchmark question along with a correct SPARQL query.*/
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
class Question
{
	public final String string;
	public final String query;
	public final Set<Map<String,String>> answers;
	public final Map<String,AnswerType> answerTypes;
}