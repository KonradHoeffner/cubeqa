package org.aksw.autosparql.cube.template;

import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.aksw.autosparql.commons.knowledgebase.DBpediaKnowledgebase;
import org.aksw.autosparql.commons.nlp.ner.DBpediaSpotlightNER;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.AggregateMapping;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class CubeTemplator
{
	//	private final StanfordPartOfSpeechTagger tagger = StanfordPartOfSpeechTagger.INSTANCE;
	//	private final Preprocessor	pp = new Preprocessor(true);

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
				// can we match complete phrase?
								String phrase = phrase(np);
								System.out.println(phrase);
				if(ners.contains(phrase)) {continue;} // already found but TODO use phrase for additional identification


				ComponentProperty maxProperty = cube.properties.values().stream().max(Comparator.comparing(p->p.scorer.score(phrase))).get();


				//				List<String> ners = ner.getNamedEntitites(phrase(np));

				// can we match the whole thing?
			}
			//			System.out.println(nps);


			//			Tree next = pp.parent().getNodeNumber(pp.nodeNumber(pp.parent())+1);
			//			System.out.println(next);
		}



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