package org.aksw.cubeqa;

import java.util.*;

public class Stopwords
{
	static public final Set<String> STOPWORDS =
			new HashSet<>(Arrays.asList(
			   "a", "an", /*"and",*/ "are", "as", "at", "be", "but", "by",
			      "for", "if", "in", "into", "is", "it",
			      "no", "not", "of", "on", "or", "such",
			      "that", "the", "their", "then", "there", "these",
			      "they", "this", "to", "was", "will", "with"
			      ,"does","do","did"));

	static public final Set<String> QUESTION_WORDS =
			new HashSet<>(Arrays.asList(
			      "what","how many","how","is","why","will","where","when"));

	static public final Set<String> PROPERTY_WORDS =
			new HashSet<>(Arrays.asList(
			      "reference","recipient"));

	static public final Set<String> FINLAND_AID_WORDS =
			new HashSet<>(Arrays.asList(
			      "aid"));

	public static String remove(String s, Set<String> words)
	{
		for(String word: words)
		{
			s=s.replaceAll("(?i) "+word+" "," ");
			s=s.replaceAll("(?i)^"+word+" ","");
			s=s.replaceAll("(?i) "+word+"$","");
			s=s.replaceAll("  "," ");
		}
		return s.trim();
	}

}
