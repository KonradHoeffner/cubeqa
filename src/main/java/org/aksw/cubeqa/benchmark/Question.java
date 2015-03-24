package org.aksw.cubeqa.benchmark;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**A single benchmark question along with a correct SPARQL query.*/
@RequiredArgsConstructor
@ToString
class Question
{
	public final String string;
	public final String query;
	public final Set<Map<String,Object>> answers;
}