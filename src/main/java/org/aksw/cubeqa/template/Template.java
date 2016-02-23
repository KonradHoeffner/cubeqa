package org.aksw.cubeqa.template;

import java.util.*;
import java.util.stream.Collectors;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.detector.Aggregate;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.Restriction;
import lombok.RequiredArgsConstructor;

/** Template for a data cube query. */
@RequiredArgsConstructor
public class Template
{
	final Cube cube;

	final Set<Restriction> restrictions;
	final Set<ComponentProperty> answerProperties;
	final Set<ComponentProperty> perProperties;
	final Set<Aggregate> aggregates;

	/** true, iff it contains at least one answer property */
	boolean isComplete()
	{
		return !answerProperties.isEmpty();
	}

	/** Generates a SPARQL query out of the template.*/
	public String sparqlQuery()
	{
		if(!isComplete()) {throw new IllegalStateException("not complete");}
		List<String> wherePatterns = restrictions.stream().flatMap(r->r.wherePatterns().stream()).collect(Collectors.toList());
		wherePatterns.add("?obs qb:dataSet <"+cube.uri+">.");
		wherePatterns.add("?obs a qb:Observation.");

		List<String> orderLimitPatterns = restrictions.stream().flatMap(r->r.orderLimitPatterns().stream()).collect(Collectors.toList());
		if(orderLimitPatterns.size()>1) {
			throw new IllegalArgumentException("more than one orderlimit pattern");
		}

		StringBuilder sb = new StringBuilder();
		//		System.out.println(Cube.FINLAND_AID().properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount").var);
		//		answerProperties.forEach(action)
		//		System.out.println(answerProperties.iterator().next());
		//		System.out.println(answerProperties.iterator().next()==Cube.FINLAND_AID().properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount"));
		String resultDef;
		ComponentProperty answerProperty = answerProperties.iterator().next();
		switch(answerProperty.answerType)
		{
			case UNCOUNTABLE: resultDef = "xsd:decimal(?"+answerProperties.iterator().next().var+")";break;
			case COUNTABLE: resultDef = "xsd:integer(?"+answerProperties.iterator().next().var+")";break;
			case ENTITY:
			case TEMPORAL:
			resultDef = "distinct(?"+answerProperties.iterator().next().var+")";break;
			default: resultDef = "distinct(?"+answerProperties.iterator().next().var+")";
		}

		if(!aggregates.isEmpty()) {resultDef = aggregates.iterator().next()+"("+resultDef+")";}
		sb.append("select "+resultDef+" ");
		perProperties.removeAll(answerProperties);
		for(ComponentProperty p: perProperties) {sb.append(" ?"+p.var);}
		sb.append("\n{\n");
		for(String pattern: wherePatterns) {sb.append(pattern);sb.append("\n");}
		for(ComponentProperty p: answerProperties)				{sb.append("?obs <"+p.uri+"> ?"+p.var+".");sb.append("\n");}
		for(ComponentProperty p: perProperties)					{sb.append("?obs <"+p.uri+"> ?"+p.var+".");sb.append("\n");}
		// those properties are used in order limit patterns and need to have their own triple pattern as well
		List<ComponentProperty> otherProperties = restrictions.stream().map(Restriction::getProperty).collect(Collectors.toList());
		// TODO remove temporal here also? seems to add too much
		otherProperties.removeAll(answerProperties);
		otherProperties.removeAll(perProperties);
		for(ComponentProperty p: otherProperties)					{sb.append("?obs <"+p.uri+"> ?"+p.var+".");sb.append("\n");}
		sb.append("}");
		if(!orderLimitPatterns.isEmpty())
		{
			sb.append(orderLimitPatterns.iterator().next());
		}
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

	//	public static Pair<Double,Double> precisionRecallProperties(CubeTemplate standard, CubeTemplate candidate)
	//	{
	//		log.debug("property type: "+candidate.allProperties().iterator().next().propertyType);
	//		Set<ComponentProperty> found = candidate.allProperties();
	//
	//		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
	//		Set<ComponentProperty> foundCorrect = new HashSet<>(found);
	//		Set<ComponentProperty> correct = standard.allProperties();
	//		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
	//		foundCorrect.retainAll(correct);
	//		if(found.size()==0||correct.size()==0) return null;
	//		log.debug("found: "+found);
	//		log.debug("correct: "+correct);
	//		return new Pair<Double, Double>((double)foundCorrect.size()/found.size(),(double)foundCorrect.size()/correct.size());
	//	}
	//
	//	public static Pair<Double,Double> precisionRecallDimensions(CubeTemplate standard, CubeTemplate candidate)
	//	{
	//		log.debug("property type: "+candidate.allProperties().iterator().next().propertyType);
	//		Set<ComponentProperty> found = candidate.allProperties()
	//// TODO how can p be null??
	//		.stream().filter(p->p!=null&&p.propertyType==PropertyType.ATTRIBUTE).collect(Collectors.toSet());
	//		Set<ComponentProperty> foundCorrect = new HashSet<>(found);
	//		Set<ComponentProperty> correct = standard.allProperties()
	//		.stream().filter(p->p!=null&&p.propertyType==PropertyType.ATTRIBUTE).collect(Collectors.toSet());
	//		foundCorrect.retainAll(correct);
	//		if(found.size()==0||correct.size()==0) return null;
	//		log.debug("found: "+found);
	//		log.debug("correct: "+correct);
	//		return new Pair((double)foundCorrect.size()/found.size(),(double)foundCorrect.size()/correct.size());
	//	}
	//
	//	public static Pair<Double,Double> precisionRecallRestrictions(CubeTemplate standard, CubeTemplate candidate)
	//	{
	//		Set<Restriction> found = candidate.restrictions;
	//
	//		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
	//		Set<Restriction> foundCorrect = new HashSet<>(found);
	//		Set<Restriction> correct = standard.restrictions;
	//		//.stream().filter(p->p.propertyType==PropertyType.DIMENSION).collect(Collectors.toSet());
	//		foundCorrect.retainAll(correct);
	//		if(found.size()==0||correct.size()==0) return null;
	//		return new Pair((double)foundCorrect.size()/found.size(),(double)foundCorrect.size()/correct.size());
	//	}

}