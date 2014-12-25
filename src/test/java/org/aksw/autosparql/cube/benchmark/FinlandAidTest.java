package org.aksw.autosparql.cube.benchmark;

import static org.junit.Assert.*;
import org.aksw.autosparql.cube.Algorithm;
import org.junit.Test;

public class FinlandAidTest
{

	@Test public void test()
	{
		Algorithm a = new Algorithm(FinlandAid.CUBE_NAME);
		a.answer(FinlandAid.questions.get(25));
		//		for(String question: FinlandAid.questions)
//		{
//			a.answer(question);
//		}
	}

}
