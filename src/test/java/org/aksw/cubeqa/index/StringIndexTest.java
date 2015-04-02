package org.aksw.cubeqa.index;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.Scorer;
import org.junit.Test;

public class StringIndexTest
{

	@Test public void test()
	{
		Cube cube = Cube.getInstance("finland-aid");
		Scorer scorer = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name").scorer;
		System.out.println(scorer.score("Finnish Red Cross"));

	}

}
