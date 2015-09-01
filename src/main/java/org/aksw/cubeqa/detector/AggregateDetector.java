package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.stream.Collectors;
import lombok.*;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.template.CubeTemplateFragment;
import org.apache.log4j.Level;
import de.konradhoeffner.commons.TSVReader;

@Log4j
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class AggregateDetector extends Detector
{
	{log.setLevel(Level.ALL);}
	public final Map<String,Aggregate> aggregateMap;

	public static final AggregateDetector INSTANCE = new AggregateDetector();

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

	@Override public Set<CubeTemplateFragment> detect(Cube cube, String phrase)
	{
		Set<CubeTemplateFragment> fragments = new HashSet<>();
		// for now only return up to one aggregate
		aggregateMap.keySet().stream().filter(phrase::contains).findFirst().ifPresent(s->
		{
			Aggregate aggregate = aggregateMap.get(s);
			fragments.add(new CubeTemplateFragment(cube, s,
					Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.singleton(aggregate), Collections.emptySet()));
			log.debug("Found aggregate "+aggregate+ " in phrase '"+s+"'");
		});
		return fragments;
	}

}