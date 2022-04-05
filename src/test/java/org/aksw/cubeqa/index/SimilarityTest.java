package org.aksw.cubeqa.index;

import org.junit.jupiter.api.Test;

public class SimilarityTest
{

	@Test public void testSimilarity()
	{
		System.out.println(Similarity.similarity("amounts extended", "amounts extended"));
		System.out.println(Similarity.similarity("extended amounts", "amounts extended"));
		System.out.println(Similarity.similarity("Extended Amounts", "amounts extended"));
		System.out.println(Similarity.similarity("extended amount", "amounts extended"));
		System.out.println(Similarity.similarity("Philippines", "Phillipines"));
		System.out.println(Similarity.similarity("Malaysia, Philippines", "Phillipines"));
	}

}
