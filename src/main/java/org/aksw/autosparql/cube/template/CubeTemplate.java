package org.aksw.autosparql.cube.template;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.restriction.Restriction;
import com.hp.hpl.jena.query.ResultSet;

@RequiredArgsConstructor
public class CubeTemplate
{
	final Cube cube;

	final Set<Restriction> restrictions;
	final Set<ComponentProperty> answerProperties;
	final Set<ComponentProperty> perProperties = new HashSet<>();
	final Set<Aggregate> aggregates;

	boolean isComplete()
	{
		return !restrictions.isEmpty()&&!answerProperties.isEmpty();
	}

	public String sparqlQuery()
	{
		if(!isComplete())  throw new IllegalStateException("not complete");
		Set<String> wherePatterns = restrictions.stream().flatMap(r->r.wherePatterns().stream()).collect(Collectors.toSet());
		wherePatterns.add("?obs qb:dataSet <"+cube.uri+">. ?obs a qb:Observation.");

		Set<String> orderLimitPatterns = restrictions.stream().flatMap(r->r.orderLimitPatterns().stream()).collect(Collectors.toSet());
		if(orderLimitPatterns.size()>1) throw new IllegalArgumentException("more than one orderlimit pattern");

		StringBuilder sb = new StringBuilder();
//		answerProperties.forEach(action)
		String resultDef = "xsd:decimal(?"+answerProperties.iterator().next().var+")";
		if(!aggregates.isEmpty()) {resultDef = aggregates.iterator().next()+"("+resultDef+")";}
		sb.append("select "+resultDef+" ");
		perProperties.removeAll(answerProperties);
		for(ComponentProperty p: perProperties) {sb.append(" ?"+p.var);}
		sb.append("{");
		for(String pattern: wherePatterns) {sb.append(pattern);sb.append(" ");}
		for(ComponentProperty p: answerProperties)				{sb.append("?obs <"+p.uri+"> ?"+p.var+".");}
		for(ComponentProperty p: perProperties)					{sb.append("?obs <"+p+"> ?"+p.var);}
		sb.append("}");
		if(!orderLimitPatterns.isEmpty()) sb.append(orderLimitPatterns.iterator().next());
		return sb.toString();
	}

	void execute()
	{
		ResultSet rs = CubeSparql.LINKED_SPENDING.select(sparqlQuery());
		while(rs.hasNext())
		{

		}
	}
}