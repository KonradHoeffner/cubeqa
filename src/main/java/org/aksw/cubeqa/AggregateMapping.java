package org.aksw.cubeqa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import de.konradhoeffner.commons.TSVReader;

/** Mapping from natural language phrase to SPARQL aggregate function.*/
public class AggregateMapping
{
	public final Map<String,Aggregate> aggregateMap;

	public static final AggregateMapping INSTANCE = new AggregateMapping();

	@SneakyThrows
	private AggregateMapping()
	{
		Map<String,Aggregate> aggregateMap = new HashMap<>();
		try(TSVReader in = new TSVReader(this.getClass().getClassLoader().getResourceAsStream("aggregatemapping.tsv")))
		{
			while(in.hasNextTokens())
			{
				String[] tokens = in.nextTokens();
				aggregateMap.put(tokens[0],Aggregate.valueOf(tokens[1].toUpperCase()));
			}
		}
		this.aggregateMap = Collections.unmodifiableMap(aggregateMap);
	}

	static public Set<Aggregate> aggregatesContained(String phrase)
	{
		return INSTANCE.aggregateMap.keySet().stream()
				.filter(phrase::contains).map(INSTANCE.aggregateMap::get).collect(Collectors.toSet());
	}

	static public Set<Aggregate> aggregatesReferenced(String phrase)
	{
		return INSTANCE.aggregateMap.keySet().stream()
				.filter(phrase::equalsIgnoreCase).map(INSTANCE.aggregateMap::get).collect(Collectors.toSet());
	}

}