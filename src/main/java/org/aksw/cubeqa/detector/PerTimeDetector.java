package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.RestrictionWithPhrase;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Level;
import org.apache.lucene.search.spell.*;
import com.hp.hpl.jena.vocabulary.XSD;

/**  manages phrases like "per month" or "per year", "a year".
 * Program flow needs to be adapted because time units can also be dimensions and year which should get preferential treatment.
 * maybe put in priority values for each detector and scorer? or detectors can be overwritten?
 * Or detectors should apply only once with find of regexes on whole phrase for faster runtime and easier program flow?
 **/
@Log4j
public class PerTimeDetector extends Detector
{
	static final String[][] dataTypes =
		{
		{"per day",XSD.gDay.getURI()},
		{"per month",XSD.gMonth.getURI()},
		{"per year",XSD.gYear.getURI()},
		};

	protected static transient StringDistance similarity = new NGramDistance();

	static public final PerTimeDetector INSTANCE = new PerTimeDetector();

	@Override public Optional<RestrictionWithPhrase> detect(Cube cube, String phrase)
	{
		for(String[] dataType: dataTypes)
		{
			if(phrase.equalsIgnoreCase(dataType[0]))
			{
				// does a property with this time interval exist directly?
				Set<ComponentProperty> candidates = cube.properties.values().stream().filter(p->p.range.equals(dataType[1])).collect(Collectors.toSet());
				if(candidates.isEmpty())
				{
					// TODO try to calculate time interval out of others with conversion
					return Optional.empty();
				}
				ComponentProperty bestCandidate;
				if(candidates.size()==1)
				{
					bestCandidate = candidates.iterator().next();
				} else
				{
//					 multiple properties with the right data type, which one has the highest string similarity?
					bestCandidate = candidates.stream().max(Comparator.comparing(
							p->p.labels.stream().max(Comparator.comparing(l->similarity.getDistance(dataType[1], l))).get())).get();
				}
				log.setLevel(Level.ALL);
				log.debug("best candidate for phrase "+phrase+": "+bestCandidate);
				return Optional.of(bestCandidate);
			}
		}
		return Optional.empty();
	}

}