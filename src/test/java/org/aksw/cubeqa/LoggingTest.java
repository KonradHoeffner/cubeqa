package org.aksw.cubeqa;

import org.apache.log4j.Priority;
import org.junit.Test;
import lombok.extern.log4j.Log4j;

@Log4j
public class LoggingTest
{
	@SuppressWarnings("deprecation")
	@Test
	public void test()
	{
		for(Priority priority: Priority.getAllPossiblePriorities())
		{
			log.log(priority, "testing log."+priority);
		}

	}

}
