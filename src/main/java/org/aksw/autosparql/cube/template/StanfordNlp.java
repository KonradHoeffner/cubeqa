package org.aksw.autosparql.cube.template;

import java.io.PrintStream;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.io.NullOutputStream;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordNlp
{
	static private final Properties props = new Properties();
	static private final StanfordCoreNLP pipeline;

	static
	{
		// disable logging
		// TODO do this more elegantly
		PrintStream err = System.err;
		System.setErr(new PrintStream(new NullOutputStream()));
		props.put("annotators", "tokenize, ssplit, pos, parse");
		pipeline = new StanfordCoreNLP(props);
		// enable logging
		System.setErr(err);
	}

	public static Tree parse(String sentence)
	{
		Annotation document = new Annotation(sentence);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		return sentences.get(0).get(TreeAnnotation.class);
	}
}