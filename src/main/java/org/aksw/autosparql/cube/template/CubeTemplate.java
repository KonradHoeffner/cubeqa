package org.aksw.autosparql.cube.template;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.ComponentProperty;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.restriction.Restriction;
import com.hp.hpl.jena.query.ResultSet;

@AllArgsConstructor
public class CubeTemplate
{
	final String cubeUri;
	final Set<Restriction> restrictions;
	final ComponentProperty answerProperty;
	final Optional<Aggregate> aggregate;

	String sparqlQuery()
	{
		Set<String> wherePatterns = restrictions.stream().flatMap(r->r.wherePatterns().stream()).collect(Collectors.toSet());
		wherePatterns.add("?obs qb:dataSet <"+cubeUri+">. ?obs a qb:Observation.");

		Set<String> orderLimitPatterns = restrictions.stream().flatMap(r->r.orderLimitPatterns().stream()).collect(Collectors.toSet());
		if(orderLimitPatterns.size()>1) throw new IllegalArgumentException("more than one orderlimit pattern");

		StringBuilder sb = new StringBuilder();
		String resultDef = "xsd:decimal(?result)";
		if(aggregate.isPresent()) {resultDef = aggregate.get()+"("+resultDef+")";}
		sb.append("select "+resultDef+" {");
		for(String pattern: wherePatterns) {sb.append(pattern);sb.append(" ");}
		sb.append("?obs <"+answerProperty.uri+"> ?result.");
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