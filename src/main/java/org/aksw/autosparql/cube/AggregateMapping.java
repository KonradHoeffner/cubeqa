package org.aksw.autosparql.cube;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import de.konradhoeffner.commons.TSVReader;

public class AggregateMapping
{

	private final Map<String,Aggregate> aggregateMap;

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

	static Set<Aggregate> find(String question)
	{
		return INSTANCE.aggregateMap.keySet().stream()
				.filter(question::contains).map(INSTANCE.aggregateMap::get).collect(Collectors.toSet());
	}
}
