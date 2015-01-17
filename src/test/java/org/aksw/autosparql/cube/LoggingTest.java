package org.aksw.autosparql.cube;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Priority;
import org.junit.Test;

@Log4j
public class LoggingTest
{

	@Test
	public void test()
	{
		for(Priority priority: Priority.getAllPossiblePriorities())
		{
			log.log(priority, "testing log."+priority);
		}

	}

}
