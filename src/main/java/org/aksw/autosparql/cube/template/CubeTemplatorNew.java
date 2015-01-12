package org.aksw.autosparql.cube.template;

import static org.aksw.autosparql.cube.Trees.phrase;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.property.scorer.ScoreResult;
import org.aksw.autosparql.cube.property.scorer.Scorers;
import edu.stanford.nlp.trees.Tree;

@RequiredArgsConstructor
@Log
public class CubeTemplatorNew
{
	private final Cube cube;
	private final String question;

	public CubeTemplate buildTemplate()
	{
		Tree root = StanfordNlp.parse(question);
		return visitRecursive(root).toTemplate();
	}

	CubeTemplateFragment visitRecursive(Tree tree)
	{
		while(!tree.isPreTerminal()&&tree.children().length==1)
		{
			// skipping down
			tree = tree.getChild(0);
		}
		log.info("visiting tree "+tree);
		MatchResult result = identify(tree);
		if(result.isEmpty())
		{
			return CubeTemplateFragment.combine(
					tree.getChildrenAsList().stream()
					.filter(c->!c.isLeaf())
					.map(this::visitRecursive)
					.collect(Collectors.toSet()));
		}
		else
		{
			log.info("identified for tree"+tree+":"+result);
			return result.toFragment(cube);
		}
	}

	MatchResult identify(Tree tree)
	{
		String phrase = phrase(tree);
		Map<ComponentProperty,Double> nameRefs = Scorers.scorePhraseProperties(cube,phrase);
		Map<ComponentProperty,ScoreResult> valueRefs = Scorers.scorePhraseValues(cube,phrase);
		return new MatchResult(phrase, nameRefs, valueRefs);
	}

}