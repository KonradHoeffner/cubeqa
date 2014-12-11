package org.aksw.autosparql.cube;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.restriction.Restriction;

public class CubeTemplate
{
	String cubeName;
	Set<Restriction> restrictions;
	Optional<Aggregate> aggregate;

	String sparqlQuery()
	{
		Set<String> wherePatterns = restrictions.stream().flatMap(r->r.wherePatterns().stream()).collect(Collectors.toSet());
		wherePatterns.add("?obs qb:dataSet <"+cubeName+">. ?obs a qb:Observation.");

		Set<String> orderLimitPatterns = restrictions.stream().flatMap(r->r.orderLimitPatterns().stream()).collect(Collectors.toSet());
		if(orderLimitPatterns.size()>1) throw new IllegalArgumentException("more than one orderlimit pattern");

		StringBuilder sb = new StringBuilder();
		sb.append("select * {");
		for(String pattern: wherePatterns) {sb.append(pattern);}
		sb.append("}");
		sb.append(orderLimitPatterns.iterator().next());
		return sb.toString();
	}
}
