package org.aksw.openqa.component.answerformulation.queryparser.impl;

import java.util.Map;
import org.aksw.openqa.component.answerformulation.AbstractQueryParserFactory;
import org.aksw.openqa.component.answerformulation.IQueryParser;

public class CubeQaQueryParserFactory extends AbstractQueryParserFactory
{
	@Override public IQueryParser create(Map<String, Object> params)
	{
		return create(CubeQaQueryParser.class, params);
	}
}