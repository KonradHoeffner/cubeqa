package org.aksw.autosparql.cube.template;

import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.Cube;
import edu.stanford.nlp.trees.Tree;

@RequiredArgsConstructor
public class CubeTemplatorNew
{
	private final Cube cube;
	private final String question;

	CubeTemplate buildTemplate()
	{
		Tree tree = StanfordNlp.parse(question);
		return null;
	}
}