package org.aksw.cubeqa.benchmark;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**A single benchmark question along with a correct SPARQL query.*/
@RequiredArgsConstructor
@ToString
class Question
{
	public final String string;
	public final String query;
}