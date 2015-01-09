package org.aksw.autosparql.cube.template;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.restriction.Restriction;

@RequiredArgsConstructor
public class CubeTemplateFragment
{
	final String cubeUri;

	final Set<Restriction> restrictions;
	final Set<ComponentProperty> answerProperties;
	final Set<ComponentProperty> perProperties = new HashSet<>();
	final Set<Aggregate> aggregates;

	Set<MatchResult> matchResults;

	public void union(CubeTemplateFragment f)
	{
		if(!cubeUri.equals(f.cubeUri)) throw new IllegalArgumentException("cube uri different");
		// TODO join restrictions if possible (e.g. intervals for numericals, detect impossibilities)
		restrictions.addAll(f.restrictions);
		answerProperties.addAll(answerProperties);
		aggregates.addAll(f.aggregates);

	}

	CubeTemplate toTemplate()
	{
		return new CubeTemplate(cubeUri, restrictions, answerProperties, aggregates);
	}
}