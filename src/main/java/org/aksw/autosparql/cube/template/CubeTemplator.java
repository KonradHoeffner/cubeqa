package org.aksw.autosparql.cube.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.aksw.autosparql.commons.knowledgebase.DBpediaKnowledgebase;
import org.aksw.autosparql.cube.Cube;
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

	public CubeTemplator()
	{

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
	public void buildTemplates(Cube cube, String question)
	{
		Annotation document = new Annotation(question);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
//			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
//				String text = token.get(TextAnnotation.class);
//				String pos = token.get(PartOfSpeechAnnotation.class);
//				String ner = token.get(NamedEntityTagAnnotation.class);
//			}

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

	private void findReferences(Tree tree)
	{

//		List<Tree> preTerminals = tree.subTrees().stream().filter(Tree::isPreTerminal).collect(Collectors.toList());
		Set<Tree> subTrees = tree.subTrees();
		Set<Tree> rest = new HashSet<>(subTrees);
		Set<Tree> pps = rest.stream().filter(l->l.label().value().equals("PP")).collect(Collectors.toSet());
		rest.removeAll(pps);

		for(Tree pp : pps)
		{
			System.out.println(pp);
			Set<String> in = pp.getChildrenAsList().stream().filter(c->c.isPreTerminal()&&c.value().equals("IN")).map(c->c.getChild(0).value()).collect(Collectors.toSet());
			System.out.println(in);

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