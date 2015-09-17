package org.aksw.cubeqa.property.scorer;

import java.util.stream.Collectors;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class StringScorerTest
{
	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		StringScorer scorer= new StringScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name"));
			System.out.println(scorer.valueStream().filter(n->n.asLiteral().getLexicalForm().contains("Red Cross")).collect(Collectors.toSet()));
//			System.out.println(scorer.loadOrCreateIndex().getStringsWithScore("Finnish Red Cross"));
//			assertTrue(scorer.score("Finnish Red Cross").get().score==1);
//			System.out.println(scorer.unsafeScore("Finnfund"));
//			System.out.println(scorer.unsafeScore("Fida International"));
//			System.out.println(scorer.unsafeScore("Fida"));
		}
	}