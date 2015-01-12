package org.aksw.autosparql.cube;

import org.junit.Test;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;

public class ComponentPropertyTest
{

	@Test public void testMatch()
	{
//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
//		System.out.println(amount.range);
//		System.out.println(amount.labels);
//		System.out.println(amount.match("extended amounts"));
		System.out.println(new QGramsDistance().getSimilarity("Amounts extended", "extended amounts"));
		System.out.println(new CosineSimilarity().getSimilarity("Amounts extended", "extended amounts"));
		System.out.println(new SmithWatermanGotohWindowedAffine().getSimilarity("Amounts extended", "extended amounts"));
		System.out.println(new MongeElkan().getSimilarity("Amounts extended", "extended amounts"));

//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/amount");

	}

//	@Test public void testGetInstance()
//	{
//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
//		System.out.println(amount.range);
//		System.out.println(amount.labels);
//
//	}

}
