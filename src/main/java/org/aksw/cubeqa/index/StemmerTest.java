package org.aksw.cubeqa.index;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.LancasterStemmer;

public class StemmerTest
{
	@Test public void stemmerTest()
	{
//		 PorterStemmer stemmer = new PorterStemmer();
		LancasterStemmer stemmer = new LancasterStemmer();
		assertEquals(stemmer.stem("egyptian"),stemmer.stem("egypt"));
//		stemmer.
		assertEquals(stemmer.stem("strengthening civil society"),stemmer.stem("strengthen civil society"));
//		 stemmer.setCurrent("strengthening civil society");
//		 stemmer.stem();
//		 assertEquals(stemmer.getCurrent(),"strengthening civil societi");
//		 stemmer.setCurrent("egyptian");
//		 stemmer.stem();
//		 assertEquals(stemmer.getCurrent(),"egypt");

	}
}