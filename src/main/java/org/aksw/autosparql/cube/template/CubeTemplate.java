package org.aksw.autosparql.cube.template;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.property.ComponentProperty.PropertyType;
import org.aksw.autosparql.cube.restriction.Restriction;
import com.hp.hpl.jena.query.ResultSet;
import de.konradhoeffner.commons.Pair;

@RequiredArgsConstructor
public class CubeTemplate
{
	final Cube cube;

	final Set<Restriction> restrictions;
	final Set<ComponentProperty> answerProperties;
	final Set<ComponentProperty> perProperties;
	final Set<Aggregate> aggregates;

	boolean isComplete()
	{
		return !answerProperties.isEmpty();
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

	Set<ComponentProperty> allProperties()
	{
		Set<ComponentProperty> properties = new HashSet<>(answerProperties);
		properties.addAll(perProperties);
		// TODO restrictions should never have null properties, investiage
		restrictions.stream().map(Restriction::getProperty).forEach(properties::add);
		return properties;
	}

	public static Pair<Double,Double> precisionRecallProperties(CubeTemplate standard, CubeTemplate candidate)
	{
		System.out.println(candidate.allProperties().iterator().next().propertyType);
		Set<ComponentProperty> found = candidate.allProperties();

		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
		Set<ComponentProperty> foundCorrect = new HashSet<>(found);
		Set<ComponentProperty> correct = standard.allProperties();
		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
		foundCorrect.retainAll(correct);
		if(found.size()==0||correct.size()==0) return null;
		System.out.println(found);
		System.out.println(correct);
		return new Pair((double)foundCorrect.size()/found.size(),(double)foundCorrect.size()/correct.size());
	}

	public static Pair<Double,Double> precisionRecallDimensions(CubeTemplate standard, CubeTemplate candidate)
	{
		System.out.println(candidate.allProperties().iterator().next().propertyType);
		Set<ComponentProperty> found = candidate.allProperties()
// TODO how can p be null??
		.stream().filter(p->p!=null&&p.propertyType==PropertyType.ATTRIBUTE).collect(Collectors.toSet());
		Set<ComponentProperty> foundCorrect = new HashSet<>(found);
		Set<ComponentProperty> correct = standard.allProperties()
		.stream().filter(p->p!=null&&p.propertyType==PropertyType.ATTRIBUTE).collect(Collectors.toSet());
		foundCorrect.retainAll(correct);
		if(found.size()==0||correct.size()==0) return null;
		System.out.println(found);
		System.out.println(correct);
		return new Pair((double)foundCorrect.size()/found.size(),(double)foundCorrect.size()/correct.size());
	}

	public static Pair<Double,Double> precisionRecallRestrictions(CubeTemplate standard, CubeTemplate candidate)
	{
		System.out.println(candidate.allProperties().iterator().next().propertyType);
		Set<Restriction> found = candidate.restrictions;

		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
		Set<Restriction> foundCorrect = new HashSet<>(found);
		Set<Restriction> correct = standard.restrictions;
		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
		foundCorrect.retainAll(correct);
		if(found.size()==0||correct.size()==0) return null;
//		System.out.println(found);
//		System.out.println(correct);
		return new Pair((double)foundCorrect.size()/found.size(),(double)foundCorrect.size()/correct.size());
	}

}