package org.aksw.autosparql.cube;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.aksw.autosparql.commons.knowledgebase.DBpediaKnowledgebase;
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

	Properties props = new Properties();
	StanfordCoreNLP pipeline;

	public CubeTemplator()
	{
		props.put("annotators", "tokenize, ssplit, pos, parse");
		pipeline = new StanfordCoreNLP(props);
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
		//		String tagged = tagger.tag(pp.replacements(question));
		//		System.out.println("Tagged input: " + tagged);
		//		tagged = pp.findNEs(tagged,question);
		//		Map<String,Set<String>> entityLabels = entityLabels(pp.ner.entityUri);
		//		System.out.println(entityLabels);
		//		//		tagged = pp.lowercase(tagged,question);
		//		tagged = pp.ascii(tagged);
		//		System.out.println("Tagged input: " + tagged);
		//		String condensed = pp.condense(pp.condenseNominals(tagged));
		//		System.out.println("condensed: " + condensed);

		// read some text in the text variable
		String text = question;// Add your text here!

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
			}

			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);
//			System.out.println(tree);
			findComponents(tree);

			// this is the Stanford dependency graph of the current sentence
			//		      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		}

	}

	private Set<String> findComponents(Tree tree)
	{
		// recursively add all
				Set<String> components = tree.getChildrenAsList().stream().map(this::findComponents).flatMap(Collection::stream).collect(Collectors.toSet());
//		System.out.println(tree);
		//		System.out.println("tree "+tree);
		//		System.out.println("penn "+tree.pennString());
//				if(!tree.isLeaf()) System.out.println("label "+tree.label());
				if((!tree.isLeaf())&&tree.label().value().equals("PP")) System.out.println(tree);
//				System.out.println(tree.getLeaves());
		//		tree.taggedYield().forEach(t->System.out.print(t.tag()));
		//		System.out.println();

		//		System.out.println(tree.taggedLabeledYield());

		//		return components;
		return components;
	}
}