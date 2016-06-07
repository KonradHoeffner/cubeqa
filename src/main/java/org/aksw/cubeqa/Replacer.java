package org.aksw.cubeqa;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replacer
{
	static private Map<Pattern,Integer> numbers = new HashMap<>();

	static private Pattern pattern(String numberRef)
	{
		return Pattern.compile("([0-9]([.,][0-9]*)?) "+numberRef);
	}
	static
	{
		numbers.put(pattern("hundred"),100);
		numbers.put(pattern("thousand"),1000);
		numbers.put(pattern("million"),1000_000);
		numbers.put(pattern("billion"),1000_000_000);
	}

	public static String replace(String query)
	{
		for(Pattern p: numbers.keySet())
		{
			Matcher m = p.matcher(query);
			while(m.find())
			{
				double d = Double.valueOf(m.group(1).replace(',', '.'));
				query = query.replace(m.group(0), String.valueOf((int)(d*numbers.get(p))));
			}
		}
		return query;
	}
}