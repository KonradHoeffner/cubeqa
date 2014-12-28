package org.aksw.autosparql.cube.template;

import static org.aksw.autosparql.cube.Trees.*;
import java.util.*;
import java.util.stream.Collectors;
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

	@SneakyThrows
	public CubeTemplate buildTemplates()
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

	private void findQuestionWord(Tree tree)
	{
		//		Set<Tree> wp = preTerminals.stream().filter(l->l.label().value().equals("WP")).collect(Collectors.toSet());
		//		preTerminals.removeAll(wp);
		//		Set<String> questionWords = wp.stream().map(t->t.getChild(0).value()).collect(Collectors.toSet());
	}

	/** determine aggregate reference, if existing, and remove from the tree **/
	private Optional<Aggregate> findAggregate(Tree tree)
	{
		Set<Aggregate> aggregates = new HashSet<Aggregate>();
		Set<Tree> subTrees = tree.subTrees();

		subTrees.stream().map(t->new Pair<Tree,Set<Aggregate>>(t,AggregateMapping.aggregatesReferenced(phrase(tree))))
		.filter(pair->!pair.getB().isEmpty())
		.findFirst()
		.ifPresent(pair->
		{
			removeSubtree(tree, pair.getA());
			System.out.println(pair.getB());
			aggregates.addAll(pair.getB());
		});

		if(aggregates.isEmpty()) {return Optional.empty();}
		else {return Optional.of(aggregates.iterator().next());}
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
		Set<Tree> checkedNps = new HashSet<>();
		for(Tree pp : pps)
		{
			Set<String> prepositions = new HashSet<>(Arrays.asList("IN","TO","INTO","OF","ON","OVER","FOR","TO","FROM"));

			System.out.println(pp);
			Set<String> prepos = pp.getChildrenAsList().stream().filter(c->c.isPreTerminal()&&prepositions.contains(c.value())).map(c->c.getChild(0).value()).collect(Collectors.toSet());
			System.out.println("preposition: "+prepos);
			Set<Tree> nps = pp.getChildrenAsList().stream().filter(c->c.value().equals("NP")).collect(Collectors.toSet());
			checkedNps.addAll(nps);
			for(Tree np : nps)
			{
				Tree deepestNp = np;
				// in case of nested nps, i.e. (NP (NP ( ... ))
				while(deepestNp.children().length==1&&deepestNp.children()[0].value().equals("NP"))
				{
					deepestNp = deepestNp.getChild(0);
					checkedNps.add(deepestNp);
				}
				Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>> matched = match(deepestNp);
				restrictions.addAll(matched.a);
				answerProperties.addAll(matched.b);
				Set<Tree> subNps = deepestNp.subTrees().stream().filter(c->c.value().equals("NP")).collect(Collectors.toSet());
				checkedNps.addAll(subNps);
			}
		}
		Set<Tree> leftOverNps = tree.subTrees().stream().filter(c->c.value().equals("NP")).collect(Collectors.toSet());
		System.out.println("*** NON PP NPS ***");
		leftOverNps.removeAll(checkedNps);
		for(Tree leftOverNp: leftOverNps)
		{
			Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>> matched = match(leftOverNp);
			restrictions.addAll(matched.a);
			answerProperties.addAll(matched.b);
		}
		// todo: multiple answer properties
		ComponentProperty defaultAnswerProperty = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount");
		ComponentProperty answerProperty = defaultAnswerProperty;
		if(!answerProperties.isEmpty())
		{
			answerProperty = answerProperties.stream().max(Comparator.comparing(Pair::getB)).map(Pair::getA).get();
		}
		return new CubeTemplate(cube.uri, restrictions, answerProperty, aggregate);
	}

	private Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>> match(Tree ref)
	{
		Set<Restriction> restrictions = new HashSet<Restriction>();
		Set<Pair<ComponentProperty,Double>> answerProperties = new HashSet<>();
		// can we match complete phrase?
		String phrase = phrase(ref);
		System.out.println(phrase);
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
		Optional<Pair<ComponentProperty,Double>> referencedProperty = scorePhraseProperties(finalPhrase);
		referencedProperty.ifPresent(p->
		{
			log.info("found PROPERTY correspondence of "+p.b+" between prase "+finalPhrase+" with property "+p.a);
		});

		Optional<ScoreResult> referencedPropertyValue = scorePhraseValues(phrase);

		if(!referencedPropertyValue.isPresent())
		{
			log.info("found no correspondence for phrase "+phrase);
		} else
		{
			ScoreResult result = referencedPropertyValue.get();
			log.info("found VALUE correspondence of "+result.score+" between prase "+finalPhrase+" with property "+result.property
					+" and value '"+result.value+"' Restriction: "+result.toRestriction());

		}
		if(referencedProperty.isPresent()&&referencedPropertyValue.isPresent()) // both are present, select the best match
		{
			if(referencedProperty.get().b<referencedPropertyValue.get().score)
			{
				restrictions.add(referencedPropertyValue.get().toRestriction());
			} else
			{
				answerProperties.add(referencedProperty.get());
			}
		} else // add both as at most one is not empty anyways
		{
			referencedPropertyValue.ifPresent(v->restrictions.add(v.toRestriction()));
			referencedProperty.ifPresent(p->answerProperties.add(p));
		}

		return new Pair<Set<Restriction>,Set<Pair<ComponentProperty,Double>>>(restrictions,answerProperties);
	}

	private Optional<ScoreResult> scorePhraseValues(String phrase)
	{
		return
				cube.properties.values().stream().map(p->p.scorer.score(phrase)).filter(Optional::isPresent).map(Optional::get)
				.max(Comparator.comparing(ScoreResult::getScore));
	}

	private Optional<Pair<ComponentProperty, Double>> scorePhraseProperties(String phrase)
	{
		return
				cube.properties.values().stream().map(p->new Pair<ComponentProperty,Double>(p, p.match(phrase)))
				.filter(p->p.b>0.8).max(Comparator.comparing(Pair::getB));
	}

	private static boolean isTag(Tree tree, String tag) {return tree.label().value().equals(tag);}
	//
	//	/** transforms e.g. (PP .. (PP ..)) into (PP ..) (PP ..) */
	//	private static void flattenTree(Tree tree, String tag)
	//	{
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
}