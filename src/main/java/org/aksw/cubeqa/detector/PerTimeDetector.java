package org.aksw.cubeqa.detector;

import java.util.Optional;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.restriction.RestrictionWithPhrase;
import org.apache.commons.lang.NotImplementedException;

/**  manages phrases like "per month" or "per year", "a year".
 * Program flow needs to be adapted because time units can also be dimensions and year which should get preferential treatment.
 * maybe put in priority values for each detector and scorer? or detectors can be overwritten?
 * Or detectors should apply only once with find of regexes on whole phrase for faster runtime and easier program flow?
 **/
public class PerTimeDetector extends Detector
{

	@Override public Optional<RestrictionWithPhrase> detect(Cube cube, String phrase)
	{
		throw new NotImplementedException();
	}

}