package org.aksw.autosparql.cube.template;

import static org.aksw.autosparql.cube.Trees.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.aksw.autosparql.commons.knowledgebase.DBpediaKnowledgebase;
import org.aksw.autosparql.commons.nlp.ner.DBpediaSpotlightNER;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.AggregateMapping;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.property.scorer.ScoreResult;
import org.aksw.autosparql.cube.restriction.Restriction;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import de.konradhoeffner.commons.Pair;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

@Log
public class CubeTemplator
{
	//	private final StanfordPartOfSpeechTagger tagger = StanfordPartOfSpeechTagger.INSTANCE;
	//	private final Preprocessor	pp = new Preprocessor(true);

	private static final double	THRESHOLD	= 0.3;
	static Properties props = new Properties();
	static StanfordCoreNLP pipeline;
	static
	{
		props.put("annotators", "tokenize, ssplit, pos, parse");
		pipeline = new StanfordCoreNLP(props);
	}
	static private DBpediaSpotlightNER ner = new DBpediaSpotlightNER();

	private final String question;
	private final Cube cube;

	public CubeTemplator(Cube cube, String question)
	{
		this.cube = cube;
		this.question = question;
	}

	public Map<String,Set<String>> entityLabels(Map<String, String> entityUri)
	{
		Map<String,Set<String>> entityLabels = new HashMap<>();
		for(String entity:entityUri.keySet())
		{
			Set<String> labels = new HashSet<>();
			String query = "select ?label {<"+entityUri.get(entity)+"> rdfs:label ?label}";
			QueryEngineHTTP qe = new QueryEngineHTTP(DBpediaKnowledgebase.getDbpediaEndpoint().getURL().toString(), query);
			ResultSet rs = qe.execSelect();
			while(rs.hasNext())
			{
				labels.add(rs.next().getLiteral("label").getLexicalForm());
			}
			entityLabels.put(entity, labels);
		}
		return entityLabels;
	}

	/** @return the template for the given question*/
	@SneakyThrows
	public CubeTemplate buildTemplate()
	{
		Annotation document = new Annotation(question);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		//		for(CoreMap sentence: sentences)
		{
			CoreMap sentence = sentences.get(0);
			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);
			System.out.println(tree);
			CubeTemplate template  = buildTemplate(tree);
			System.out.println(template.sparqlQuery());
			return template;
			// this is the Stanford dependency graph of the current sentence
			//		      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		}
	}

	enum AnswerType {ANY,QUANTITY_COUNTABLE,QUANTITY_UNCOUNTABLE,LOCATION,COMPARISON}

//	private void findQuestionWord(Tree tree)
//	{
//		//		Set<Tree> wp = preTerminals.stream().filter(l->l.label().value().equals("WP")).collect(Collectors.toSet());
//		//		preTerminals.removeAll(wp);
//		//		Set<String> questionWords = wp.stream().map(t->t.getChild(0).value()).collect(Collectors.toSet());
//	}

	/** determine aggregate reference, if existing, and remove from the tree **/
	private Optional<Aggregate> findAggregate(Tree tree)
	{
		Set<Aggregate> aggregates = new HashSet<Aggregate>();
		Set<Tree> subTrees = tree.subTrees();

		subTrees.stream().map(t->new Pair<Tree,Set<Aggregate>>(t,AggregateMapping.aggregatesReferenced(phrase(t))))
		.filter(pair->!pair.getB().isEmpty())
		.findFirst()
		.ifPresent(pair->
		{
			removeSubtree(tree, pair.getA());
			System.out.println(pair.getB());
			aggregates.addAll(pair.getB());
		});

		if(aggregates.isEmpty())
		{
			// TODO: check if this is really useful
			aggregates.addAll(AggregateMapping.aggregatesContained(phrase(tree)));
		}
		if(aggregates.isEmpty())
		{
			return Optional.empty();
		}
		else {return Optional.of(aggregates.iterator().next());}
	}

	static final Set<String> PREPOSITIONS = new HashSet<>(Arrays.asList("IN","TO","INTO","OF","ON","OVER","FOR","TO","FROM"));


	private Set<Restriction> findReferences(Tree tree, Set<String> possibleValues, Set<ComponentProperty> referencedProperties)
	{
		if(tree.children().length==1&&!tree.isPreTerminal()) return findReferences(tree.getChild(0),possibleValues,referencedProperties);
		Set<Restriction> restrictions = new HashSet<>();

		match(tree);
		if(!tree.isPreTerminal())
		{

		}

		return restrictions;
	}

	private CubeTemplate buildPartialTemplate(Tree pp)
	{
		Set<Tree> checked = new HashSet<>();

		System.out.println(pp);
		Set<Tree> prepositions = pp.getChildrenAsList().stream().filter(c->c.isPreTerminal()&&PREPOSITIONS.contains(c.value())).collect(Collectors.toSet());
		removeChildren(pp, prepositions);
		Set<String> prepositionLabels = prepositions.stream().map(c->c.getChild(0).value()).collect(Collectors.toSet());
		System.out.println("prepositions: "+prepositionLabels);


		//		Set<Tree> nps = pp.getChildrenAsList().stream().filter(c->c.value().equals("NP")).collect(Collectors.toSet());
//
//		for(Tree np : nps)
//			{
//				Tree deepest = np;
//				System.out.println(deepest.children().length);
//				// in case of nested nps, i.e. (NP (NP ( ... ))
//				while(deepest.children().length==1&&!deepest.isPreTerminal())//&&deepest.children()[0].value().equals("NP"))
//				{
//					deepest = deepest.getChild(0);
//					checked.add(deepest);
//				}
//				System.out.println("np: "+deepest);
//				System.out.println("childs: "+deepest.getChildrenAsList());
//
////				Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>> matched = match(deepestNp);
////				restrictions.addAll(matched.a);
////				answerProperties.addAll(matched.b);
////				Set<Tree> subNps = deepestNp.subTrees().stream().filter(c->c.value().equals("NP")).collect(Collectors.toSet());
////				checkedNps.addAll(subNps);
//			}
//		checked.addAll(nps);

		return null;
//
	}

	private CubeTemplate buildTemplate(Tree tree)
	{
		Set<Restriction> restrictions = new HashSet<Restriction>();
		Set<Pair<ComponentProperty,Double>> answerProperties = new HashSet<>();
		Optional<Aggregate> aggregate = findAggregate(tree);

		Set<Tree> subTrees = tree.subTrees();
		Set<Tree> rest = new HashSet<>(subTrees);
		Set<Tree> pps = rest.stream().filter(l->l.label().value().equals("PP")).collect(Collectors.toSet());
		rest.removeAll(pps);

		//		List<String> ners = ner.getNamedEntitites(phrase(tree));
		//		ners.removeAll(AggregateMapping.INSTANCE.aggregateMap.keySet());
		//		System.out.println("NERS: "+ners);


		for(Tree pp : pps)
		{
			buildPartialTemplate(pp);
		}
//		Set<Tree> leftOverNps = tree.subTrees().stream().filter(c->c.value().equals("NP")).collect(Collectors.toSet());
//		System.out.println("*** NON PP NPS ***");
//		leftOverNps.removeAll(checkedNps);
//		for(Tree leftOverNp: leftOverNps)
//		{
//			Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>> matched = match(leftOverNp);
//			restrictions.addAll(matched.a);
//			answerProperties.addAll(matched.b);
//		}
//		// todo: multiple answer properties
//		ComponentProperty defaultAnswerProperty = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount");
//		ComponentProperty answerProperty = defaultAnswerProperty;
//		if(!answerProperties.isEmpty())
//		{
//			answerProperty = answerProperties.stream().max(Comparator.comparing(Pair::getB)).map(Pair::getA).get();
//		}
//		return new CubeTemplate(cube.uri, restrictions, answerProperty, aggregate);
		return null;
	}

	@AllArgsConstructor
	@EqualsAndHashCode
	class MatchResult
	{
		public final String phrase;
		/** the estimated probability that the phrase refers to a property with a given property label */
		public final Map<ComponentProperty,Double> nameRefs;
		/** the estimated probability that the phrase refers to a property with a given property value*/
		public final Map<ComponentProperty,ScoreResult> valueRefs;
//		public final Map<ComponentProperty,Double> valueRefs;

		public void join(MatchResult otherResult)
		{
			Set<ComponentProperty> nameValue = this.nameRefs.keySet();
			nameValue.retainAll(otherResult.valueRefs.keySet());
//			nameValue.retainAll(otherResult.valueRefs.stream().map(ScoreResult::getProperty).collect(Collectors.toSet()));

			nameValue.stream().map(property->new Pair<>(property,nameRefs.get(property)*valueRefs.get(property).score))
			.max(Comparator.comparing(Pair::getB));
		}
	}

//	private Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>> match(Tree ref)
	private MatchResult match(Tree ref)
	{
		Set<Restriction> restrictions = new HashSet<Restriction>();
//		Set<Pair<ComponentProperty,Double>> answerProperties = new HashSet<>();
		// can we match complete phrase?
		String phrase = phrase(ref);
		System.out.println("matching phrase "+ phrase);
		// TODO: does it contain other pps? if so do that separately
		Set<Tree> subPps = Arrays.stream(ref.children()).filter(child->child.value().equals("PP")).collect(Collectors.toSet());
		if(!subPps.isEmpty())
		{
			for(Tree subPp : subPps) {removeChild(ref,subPp);}
			phrase = phrase(ref);
			log.info("removing prepositional phrases, rest: "+phrase);
		}
		//				if(ners.contains(phrase)) {continue;} // already found but TODO use phrase for additional identification

		String finalPhrase = phrase;
		Map<ComponentProperty,Double> nameRefs = scorePhraseProperties(finalPhrase);
		nameRefs.entrySet().forEach(e->
		{
			log.info("found PROPERTY NAME correspondence of "+e.getKey()+" between phrase "+finalPhrase+" with property "+e.getValue());
		});

		Map<ComponentProperty,ScoreResult> valueRefs = scorePhraseValues(phrase);

		if(valueRefs.isEmpty())
		{
			if(nameRefs.isEmpty()) {log.info("found no correspondence for phrase "+phrase);}
		} else
		valueRefs.values().forEach(result->
		{
			log.info("found PROPERTY VALUE correspondence of "+result.score+" between prase "+finalPhrase+" with property "+result.property
					+" and value '"+result.value+"' Restriction: "+result.toRestriction());
		});

//		if(nameRefs.isPresent()&&valueRefs.isPresent()) // both are present, select the best match
//		{
//			if(nameRefs.get().b<valueRefs.get().score)
//			{
//				restrictions.add(valueRefs.get().toRestriction());
//			} else
//			{
//				answerProperties.add(nameRefs.get());
//			}
//		} else // add both as at most one is not empty anyways
//		{
//			valueRefs.ifPresent(v->restrictions.add(v.toRestriction()));
//			nameRefs.ifPresent(p->answerProperties.add(p));
//		}
//
//		return new Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>>(restrictions,answerProperties);
		return new MatchResult(finalPhrase, nameRefs, valueRefs);
	}

	private Map<ComponentProperty,ScoreResult> scorePhraseValues(String phrase)
	{
		return
				cube.properties.values().stream().map(p->p.scorer.score(phrase)).filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toMap(result->result.property, result->result));
//				.max(Comparator.comparing(ScoreResult::getScore));
	}

	private Map<ComponentProperty, Double> scorePhraseProperties(String phrase)
	{
		return
				cube.properties.values().stream().map(p->new Pair<ComponentProperty,Double>(p, p.match(phrase)))
				.filter(p->p.b>0.8).collect(Collectors.toMap(p->p.a, p->p.b));
	}

	private static boolean isTag(Tree tree, String tag) {return tree.label().value().equals(tag);}

	private Set<String> findComponents(Tree tree)
	{
		// recursively add all
		Set<String> components = tree.getChildrenAsList().stream().map(this::findComponents).flatMap(Collection::stream).collect(Collectors.toSet());
		//		System.out.println(tree);
		//		System.out.println("tree "+tree);
		//		System.out.println("penn "+tree.pennString());
		//				if(!tree.isLeaf()) System.out.println("label "+tree.label());
		if((!tree.isLeaf())&&isTag(tree,"PP")) System.out.println(tree);
		//				System.out.println(tree.getLeaves());
		//		tree.taggedYield().forEach(t->System.out.print(t.tag()));
		//		System.out.println();

		//		System.out.println(tree.taggedLabeledYield());

		//		return components;
		return components;
	}

//	/** transforms e.g. (PP .. (PP ..)) into (PP ..) (PP ..) */
//	private static void flattenTree(Tree tree, String tag)
//	{
//		throw new UnsupportedOperationException();
//		if(isTag(tree,tag)) throw new IllegalArgumentException("tree may not be a "+tag+" itself: "+tree);
//		for(Tree child: tree.children()) {flattenTree(child,tag,tree,Optional.empty());}
//	}
//
//	private static void flattenTree(Tree tree, String tag, Tree lastNonTag,Optional<Tree> firstTagged)
//	{
//		if(isTag(tree,tag))
//		{
//			if(firstTagged.isPresent())
//			{
//				//
//			} else
//			{
//				firstTagged = tree;
//			}
//		} else
//		{
//			lastNonTag=tree;
//		}
//		for(Tree child: tree.children()) {flattenTree(child,tag,tree,firstTagged);}
//	}

//boolean flattened;
//Tree[] ppa = pps.toArray(new Tree[0]);
//do
//{
//	flattened = false;
//	outer:
//	for(int i=0;i<ppa.length;i++)
//		for(int j=0;j<ppa.length;j++)
//		{
//			if(i==j) continue;
//			if(strictContainsRecursive(ppa[i], ppa[j]))
//			{
//				ppa[i]
//				flattened = true;
//				break outer;
//			}
//		}
//
//} while(flattened);
}