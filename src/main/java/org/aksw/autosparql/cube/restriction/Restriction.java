package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.tbsl.algorithm.exploration.Index.newSpecialSQliteIndex;

@RequiredArgsConstructor
public abstract class Restriction
{
	final ComponentProperty property;

	public Set<String> wherePatterns() {return Collections.emptySet();}
	public Set<String> orderLimitPatterns() {return Collections.emptySet();}

	static AtomicInteger instanceCounter = new AtomicInteger(0);
	final String uniqueVar = "?v"+instanceCounter.getAndIncrement();
	static final String OBS_VAR = " ?obs ";

	@Override public String toString()
	{
		return "Restriction on property "+property+" with where patterns: "+wherePatterns()+" and order limit patterns "+orderLimitPatterns();
	}
}