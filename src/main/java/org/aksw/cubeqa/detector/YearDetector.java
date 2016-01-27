//package org.aksw.cubeqa.detector;
//
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.aksw.cubeqa.Cube;
//import org.aksw.cubeqa.property.ComponentProperty;
//import org.aksw.cubeqa.template.CubeTemplateFragment;
//import org.apache.lucene.search.spell.NGramDistance;
//import org.apache.lucene.search.spell.StringDistance;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.vocabulary.XSD;
//
///** maps "year" to a restriction with all xsd:gYear properties.**/
//@Slf4j
//@AllArgsConstructor(access=AccessLevel.PRIVATE)
//public class YearDetector extends Detector
//{
//	public static YearDetector INSTANCE = new YearDetector();
//
//	@Override public Set<CubeTemplateFragment> detect(Cube cube, String phrase)
//	{
////		if(phrase.contains(s))
//
//		List<TimeUnit> timeUnits = getTimeUnits(cube);
//
//		Set<CubeTemplateFragment> fragments = new HashSet<>();
//		for(TimeUnit timeUnit: timeUnits)
//		{
//			for(Pattern pattern: timeUnit.patterns)
//			{
//				Matcher matcher = pattern.matcher(phrase);
//				while(matcher.find())
//				{
//					CubeTemplateFragment fragment =  new CubeTemplateFragment(cube, matcher.group(0));
//					fragment.getPerProperties().add(timeUnit.property.get());
//					fragments.add(fragment);
//					phrase = phrase.replace(matcher.group(0), "").replace("  "," ");
//					log.debug("detected property "+timeUnit.property.get()+" with data type "+timeUnit.property.get().range);
//				}
//			}
//		}
//		return fragments;
//	}
//
//	static class TimeUnit
//	{
//		public final Cube cube;
//		public final Set<Pattern> patterns = new HashSet<>();
//		public final Optional<ComponentProperty> property;
//
//		public TimeUnit(Cube cube, String name, Resource dataType)
//		{
//			this.cube=cube;
//			List<String> prePatterns = Arrays.asList("per "+name,name+"ly");
//			for(String prePattern: prePatterns)
//			{
//				patterns.add(Pattern.compile("(?i)(^|[\\s,])"+prePattern+"($|[\\s.])"));
////				patterns.add(Pattern.compile("(?i)[^\\s,]"+prePattern+"[\\s,.$]"));
//			}
//			Set<ComponentProperty> candidates = cube.properties.values().stream().filter(p->p.range.equals(dataType.getURI())).collect(Collectors.toSet());
//			if(candidates.isEmpty())
//			{
//				property = Optional.empty();
//				return;
//			}
//			ComponentProperty bestCandidate;
//			if(candidates.size()==1)
//			{
//				bestCandidate = candidates.iterator().next();
//			} else
//			{
//				//			 multiple properties with the right data type, which one has the highest string similarity?
//				bestCandidate = candidates.stream().max(Comparator.comparing(
//						p->p.labels.stream().max(Comparator.comparing(l->similarity.getDistance(name, l))).get())).get();
//			}
//			property = Optional.of(bestCandidate);
//		}
//	}
//
//	static Map<Cube,List<TimeUnit>> cubeToTimeUnits = new HashMap<>();
//
//	static private List<TimeUnit> getTimeUnits(Cube cube)
//	{
//		List<TimeUnit> timeUnits = cubeToTimeUnits.get(cube);
//		if(timeUnits==null)
//		{
//			timeUnits = Arrays.asList(
//					new TimeUnit(cube,"day",XSD.gDay),
//					new TimeUnit(cube,"month",XSD.gMonth),
//					new TimeUnit(cube,"year",XSD.gYear)
//					).stream().filter(tu->tu.property.isPresent()).collect(Collectors.toList());
//			cubeToTimeUnits.put(cube, timeUnits);
//		}
//		return timeUnits;
//	}
//}