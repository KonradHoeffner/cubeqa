package org.aksw.cubeqa.index;

import static org.junit.Assert.*;
import org.junit.Test;
import org.tartarus.snowball.ext.PorterStemmer;

public class StemmerTest
{

	@Test public void stemmerTest()
	{
		 PorterStemmer stemmer = new PorterStemmer();
		 stemmer.setCurrent("strengthening civil society");
		 stemmer.stem();
		 System.out.println(stemmer.getCurrent());
	}

}
