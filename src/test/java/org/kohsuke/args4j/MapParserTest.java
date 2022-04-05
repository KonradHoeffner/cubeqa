package org.kohsuke.args4j;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MapParserTest
{
	@Option(name="-testStringAbc")
	String testStringAbc;

	@Option(name="-testStringNull")
	String testStringNull;

	@Option(name="-testInt")
	int testInt;

	@Option(name="-testFloat")
	float testFloat;

	enum Number {ONE,TWO,THREE}
	@Option(name="-testEnum")
	Number testEnum;


	@Test
	public void test()
	{
		MapParser parser = new MapParser(new CmdLineParser(this));
		Map<String,Object> parameters = new HashMap<>();
		parameters.put("testStringAbc","abc");
		parameters.put("testInt","7");
		parameters.put("testFloat",0.7);
		parameters.put("testEnum",Number.THREE);
		parser.parse(parameters);
		assertEquals(testStringAbc,"abc");
		assertEquals(testStringNull,null);
		assertEquals(testInt,7);
		assertEquals(testFloat,0.7,0.00001);
		assertEquals(testEnum,Number.THREE);
	}

}