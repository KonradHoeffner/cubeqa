package org.aksw.cubeqa.restriction;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aksw.cubeqa.property.ComponentProperty;

/** A restriction on the values of a component property. */
@RequiredArgsConstructor
@Getter
public abstract class Restriction
{
	final ComponentProperty property;

	public Set<String> wherePatterns() {return Collections.emptySet();}
	public Set<String> orderLimitPatterns() {return Collections.emptySet();}

	static AtomicInteger instanceCounter = new AtomicInteger(0);
	final String uniqueVar = "?v"+instanceCounter.getAndIncrement();
	static final String OBS_VAR = " ?obs ";
// TODO: why can property be null?
	@Override public int hashCode() {return property==null?0:property.hashCode();}

	@Override public boolean equals(Object o)
	{
		if(!o.getClass().equals(this.getClass())) {return false;}

		return property.equals(((Restriction)(o)).property);
	};

	@Override public String toString()
	{
		return "Restriction on property "+property+" with where patterns: "+wherePatterns()+" and order limit patterns "+orderLimitPatterns();
	}

}