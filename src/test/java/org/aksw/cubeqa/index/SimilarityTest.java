package org.aksw.cubeqa.index;

import static org.junit.Assert.*;
import org.junit.Test;

public class SimilarityTest
{

	@Test public void testSimilarity()
	{
		System.out.println(Similarity.similarity("amounts extended", "amounts extended"));
		System.out.println(Similarity.similarity("extended amounts", "amounts extended"));
		System.out.println(Similarity.similarity("Extended Amounts", "amounts extended"));
		System.out.println(Similarity.similarity("extended amount", "amounts extended"));
	}

}
