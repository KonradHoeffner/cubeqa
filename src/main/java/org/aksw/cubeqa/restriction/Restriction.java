package org.aksw.cubeqa.restriction;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.*;
import org.aksw.cubeqa.property.ComponentProperty;

/** A restriction on the values of a component property. */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(exclude={"uniqueVar"})
public abstract class Restriction
{
	final ComponentProperty property;

	public Set<String> wherePatterns() {return Collections.emptySet();}
	public Set<String> orderLimitPatterns() {return Collections.emptySet();}

	final String uniqueVar = "?v"+instanceCounter.getAndIncrement();

	static AtomicInteger instanceCounter = new AtomicInteger(0);
	static final String OBS_VAR = " ?obs ";

	@Override public String toString()
	{
		return "Restriction on property "+property+" with where patterns: "+wherePatterns()+" and order limit patterns "+orderLimitPatterns();
	}

}