package org.aksw.autosparql.cube.template;

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
	public void buildTemplates()
	{
		Annotation document = new Annotation(question);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences)
		{
			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);
			System.out.println(tree);
			findReferences(tree);

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

	private String phrase(Tree tree) {return tree.getLeaves().toString().replace(", ", " ").replaceAll("[\\[\\]]", "");}

	private void findReferences(Tree tree)
	{
		Set<Aggregate> aggregates = AggregateMapping.INSTANCE.find(question);
		if(!aggregates.isEmpty()) {System.out.println("AGGREGATES FOUND! "+aggregates);}
		//		List<Tree> preTerminals = tree.subTrees().stream().filter(Tree::isPreTerminal).collect(Collectors.toList());
		Set<Tree> subTrees = tree.subTrees();
		Set<Tree> rest = new HashSet<>(subTrees);
		Set<Tree> pps = rest.stream().filter(l->l.label().value().equals("PP")).collect(Collectors.toSet());
		rest.removeAll(pps);

		List<String> ners = ner.getNamedEntitites(phrase(tree));
		ners.removeAll(AggregateMapping.INSTANCE.aggregateMap.keySet());
		System.out.println("NERS: "+ners);

		for(Tree pp : pps)
		{
			Set<String> prepositions = new HashSet<>(Arrays.asList("IN","TO","INTO","OF","ON","OVER","FOR","TO","FROM"));

			System.out.println(pp);
			Set<String> prepos = pp.getChildrenAsList().stream().filter(c->c.isPreTerminal()&&prepositions.contains(c.value())).map(c->c.getChild(0).value()).collect(Collectors.toSet());
			System.out.println("preposition: "+prepos);
			Set<Tree> nps = pp.getChildrenAsList().stream().filter(c->c.value().equals("NP")).collect(Collectors.toSet());
			for(Tree np : nps)
			{
				Tree deepestNp = np;
				// in case of nested nps, i.e. (NP (NP ( ... ))
				while(deepestNp.children().length==1&&deepestNp.children()[0].value().equals("NP")) {deepestNp = deepestNp.getChild(0);}
				// can we match complete phrase?
				String phrase = phrase(deepestNp);
				System.out.println(phrase);
				// TODO: does it contain other pps? if so do that separately
				Set<Tree> subPps = Arrays.stream(deepestNp.children()).filter(child->child.value().equals("PP")).collect(Collectors.toSet());
				if(!subPps.isEmpty())
				{
				for(Tree subPp : subPps) {deepestNp.remove(subPp);}
				 phrase = phrase(deepestNp);
				log.info("removing prepositional phrases, rest: "+phrase);
				}
//				if(ners.contains(phrase)) {continue;} // already found but TODO use phrase for additional identification

				Optional<Pair<ComponentProperty,Double>> maxProperty = scorePhrase(phrase);

				if(!maxProperty.isPresent()||maxProperty.get().b<THRESHOLD)
				{
					log.info("found no correspondence for phrase "+phrase);

				} else
				{
					log.info("found correspondence of "+maxProperty.get().b+" with property "+maxProperty.get().a);
				}
				//				List<String> ners = ner.getNamedEntitites(phrase(np));
			}
			//			System.out.println(nps);


			//			Tree next = pp.parent().getNodeNumber(pp.nodeNumber(pp.parent())+1);
			//			System.out.println(next);
		}
	}

	private Optional<Pair<ComponentProperty, Double>> scorePhrase(String phrase)
	{
		for(ComponentProperty p: cube.properties.values())
		{
//			System.out.println(p);
//			System.out.println(p.scorer);
//			System.out.println(p.scorer.score(phrase));
		}
		return
				cube.properties.values().stream().map(p->new Pair<ComponentProperty,Double>(p, p.scorer.score(phrase)))
				.max(Comparator.comparing(Pair::getB));

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