package org.aksw.cubeqa;

import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingTest
{

	@Test
	public void test()
	{
		log.trace("testing log trace");
		log.debug("testing log debug");
		log.info("testing log info");
		log.warn("testing log warn");
		log.error("testing log error");
	}

}