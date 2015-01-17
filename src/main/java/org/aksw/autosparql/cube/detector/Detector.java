package org.aksw.autosparql.cube.detector;

import java.util.Optional;
import org.aksw.autosparql.cube.restriction.Restriction;

public interface Detector
{
	public Optional<Restriction> detect(String phrase);
}
