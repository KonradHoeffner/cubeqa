package org.aksw.autosparql.cube.property.scorer;

import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class ObjectPropertyScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		Optional<ScoreResult> score =
				cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country").scorer.score("Tajikistan");
		System.out.println(score.get().toRestriction());
//		assertTrue(score.isPresent()&&score.get().value.equals(anObject)&&score.get().score>0.6);
	}
}