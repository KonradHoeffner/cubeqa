//package org.aksw.cubeqa.index;
//
//import static org.junit.Assert.*;
//import org.aksw.cubeqa.Cube;
//import org.aksw.cubeqa.property.ComponentProperty;
//import org.aksw.cubeqa.property.scorer.Scorer;
//import org.aksw.cubeqa.property.scorer.StringScorer;
//import org.junit.Test;
//
//public class StringIndexTest
//{
//
//	@Test public void test()
//	{
//		Cube cube = Cube.getInstance("finland-aid");
//		StringScorer scorer = (StringScorer) cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name").scorer;
//		scorer.index = null;
//		System.out.println(index.getStringsWithScore("Finnish Red Cross"));
//	}
//
//}