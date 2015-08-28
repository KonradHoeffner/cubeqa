package org.aksw.cubeqa.template;

import java.io.PrintStream;
import java.util.List;
import java.util.Properties;
import lombok.extern.log4j.Log4j;
import edu.stanford.nlp.io.NullOutputStream;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

/** Stanford Core NLP utility class. */
@Log4j
public class StanfordNlp
{
	static private final StanfordCoreNLP parser;
//	static private final StanfordCoreNLP lemmatizer;

	static
	{
		// disable logging
		// TODO do this more elegantly
		PrintStream err = System.err;
		System.setErr(new PrintStream(new NullOutputStream()));
		{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, parse");
		parser = new StanfordCoreNLP(props);
		}
//		{
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, pos, lemma");
//		lemmatizer = new StanfordCoreNLP(props);
//		}
		// enable logging
		System.setErr(err);
	}

	public static Tree parse(String sentence)
	{
		log.trace("parsing sentence: "+sentence);
		Annotation document = new Annotation(sentence);
		parser.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		//		return sentences.get(0).get(BasicDependenciesAnnotation.class);
		return sentences.get(0).get(TreeAnnotation.class);
	}

//	public static String lemmatize(String text)
//	{
//		Annotation document = lemmatizer.process(text);
//StringBuilder sb = new StringBuilder();
//		for(CoreMap sentence: document.get(SentencesAnnotation.class))
//		{
//			for(CoreLabel token: sentence.get(TokensAnnotation.class))
//			{
//				String word = token.get(TextAnnotation.class);
//				String lemma = token.get(LemmaAnnotation.class);
////				System.out.println("lemmatized version :" + lemma);
//				sb.append(" "+lemma);
//			}
//		}
//		return sb.toString().substring(1);
//	}
}