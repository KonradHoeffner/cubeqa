package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.template.Fragment;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

/**  manages phrases like "per month" or "per year", "a year".
 * Program flow needs to be adapted because time units can also be dimensions and year which should get preferential treatment.
 * maybe put in priority values for each detector and scorer? or detectors can be overwritten?
 * Or detectors should apply only once with find of regexes on whole phrase for faster runtime and easier program flow?
 **/
@Slf4j
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class PerTimeDetector extends Detector
{
	public static PerTimeDetector INSTANCE = new PerTimeDetector();
	protected static transient StringDistance similarity = new NGramDistance();

	@Override public Set<Fragment> detect(Cube cube, String phrase)
	{
		List<TimeUnit> timeUnits = getTimeUnits(cube);

		Set<Fragment> fragments = new HashSet<>();
		for(TimeUnit timeUnit: timeUnits)
		{
			for(Pattern pattern: timeUnit.patterns)
			{
				Matcher matcher = pattern.matcher(phrase);
				while(matcher.find())
				{
					Fragment fragment =  new Fragment(cube, matcher.group(0).trim());
					fragment.getPerProperties().add(timeUnit.property.get());
					fragments.add(fragment);
					phrase = phrase.replace(matcher.group(0), " ").replace("  "," ");
					log.debug("detected property "+timeUnit.property.get()+" with data type "+timeUnit.property.get().range);
				}
			}
		}
		return fragments;
	}

	/** unit of time, such as day, month or year */
	static class TimeUnit
	{
		public final Cube cube;
		public final Set<Pattern> patterns = new HashSet<>();
		public final Optional<ComponentProperty> property;

		/**
		 * @param cube
		 * @param label the surface form, e.g. "year"
		 * @param dataType the XSD datatype representing the time unit, e.g. XSD.gYear.
		 */
		public TimeUnit(Cube cube, String label, Resource dataType)
		{
			if(label==null) throw new IllegalArgumentException("label is null" );
			this.cube=cube;
			List<String> prePatterns = Arrays.asList("per "+label,label+"ly");
			for(String prePattern: prePatterns)
			{
				patterns.add(Pattern.compile("(?i)(^|[\\s,])"+prePattern+"($|[\\s.])"));
//				patterns.add(Pattern.compile("(?i)[^\\s,]"+prePattern+"[\\s,.$]"));
			}
			Set<ComponentProperty> candidates = cube.properties.values().stream().filter(p->dataType.getURI().equals(p.range)).collect(Collectors.toSet());
			if(candidates.isEmpty())
			{
				property = Optional.empty();
				return;
			}
			ComponentProperty bestCandidate;
			if(candidates.size()==1)
			{
				bestCandidate = candidates.iterator().next();
			} else
			{
				//			 multiple properties with the right data type, which one has the highest string similarity?
				bestCandidate = candidates.stream().max(Comparator.comparing(
						p->p.labels.stream().max(Comparator.comparing(l->similarity.getDistance(label, l))).get())).get();
			}
			property = Optional.of(bestCandidate);
		}
	}

	static Map<Cube,List<TimeUnit>> cubeToTimeUnits = new HashMap<>();

	static private List<TimeUnit> getTimeUnits(Cube cube)
	{
		List<TimeUnit> timeUnits = cubeToTimeUnits.get(cube);
		if(timeUnits==null)
		{
			timeUnits = Arrays.asList(
					new TimeUnit(cube,"day",XSD.gDay),
					new TimeUnit(cube,"month",XSD.gMonth),
					new TimeUnit(cube,"year",XSD.gYear)
					).stream().filter(tu->tu.property.isPresent()).collect(Collectors.toList());
			cubeToTimeUnits.put(cube, timeUnits);
		}
		return timeUnits;
	}
}