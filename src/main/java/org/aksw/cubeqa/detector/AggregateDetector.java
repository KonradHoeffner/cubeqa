package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.stream.Collectors;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.template.Fragment;
import de.konradhoeffner.commons.TSVReader;

@Slf4j
public enum AggregateDetector implements Detector
{
	INSTANCE;
	
	public final Map<String,Aggregate> aggregateMap;

	@SneakyThrows
	private AggregateDetector()
	{
		Map<String,Aggregate> aggregateMap = new HashMap<>();
		try(TSVReader in = new TSVReader(this.getClass().getClassLoader().getResourceAsStream("aggregatemapping.tsv")))
		{
			while(in.hasNextTokens())
			{
				String[] tokens = in.nextTokens();
				// lowercase
				aggregateMap.put(tokens[0].toLowerCase(),Aggregate.valueOf(tokens[1].toUpperCase()));
				// Capitalized
				aggregateMap.put((tokens[0].charAt(0)+"").toUpperCase()+tokens[0].substring(1),Aggregate.valueOf(tokens[1].toUpperCase()));
			}
		}
		this.aggregateMap = Collections.unmodifiableMap(aggregateMap);
	}

	static public Set<Aggregate> aggregatesContained(String phrase)
	{
		return INSTANCE.aggregateMap.keySet().stream()
				.filter(phrase::contains).map(INSTANCE.aggregateMap::get).collect(Collectors.toSet());
	}

	@Override public Set<Fragment> detect(Cube cube, String phrase)
	{
		Set<Fragment> fragments = new HashSet<>();
		// for now only return up to one aggregate
		aggregateMap.keySet().stream().filter(phrase::contains).findFirst().ifPresent(s->
		{
			Aggregate aggregate = aggregateMap.get(s);
			fragments.add(new Fragment(cube, s,
					Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.singleton(aggregate), Collections.emptySet()));
			log.debug("Found aggregate "+aggregate+ " in phrase '"+s+"'");
		});
		return fragments;
	}

}