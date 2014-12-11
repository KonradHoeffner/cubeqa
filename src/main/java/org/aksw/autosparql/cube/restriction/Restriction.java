package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import org.aksw.autosparql.cube.ComponentProperty;

public abstract class Restriction
{
	ComponentProperty property;

	public Set<String> wherePatterns() {return Collections.emptySet();}
	public Set<String> orderLimitPatterns() {return Collections.emptySet();}
}