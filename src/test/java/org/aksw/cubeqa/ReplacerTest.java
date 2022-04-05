package org.aksw.cubeqa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ReplacerTest
{

	@Test public void testReplace()
	{
		String original = "Which 1.5 thousand agencies in the 3,7 million Maldives have proposed expenditure amounts of more than 1 billion Maldivian rufiyaa in 2015?";
		String replaced = "Which 1500 agencies in the 3700000 Maldives have proposed expenditure amounts of more than 1000000000 Maldivian rufiyaa in 2015?";
		assertEquals(replaced,Replacer.replace(original));
	}

}
