package org.aksw.cubeqa.detector;

import java.util.EnumSet;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aksw.cubeqa.AnswerType;
import org.aksw.cubeqa.Cube;
import de.konradhoeffner.commons.Pair;

/** Expected Answer Type and focus concept */
@RequiredArgsConstructor
@ToString
public class Focus
{
	final AnswerType expectedAnswerType;

	public Focus findFocus(String question, Cube cube)
	{
		Optional<Pair<String,EnumSet<AnswerType>>> eaq = AnswerType.eatAndQuestionWord(question);
		if(!eaq.isPresent())
		{
			// default
			return new Focus(AnswerType.UNCOUNTABLE);
		}
		EnumSet<AnswerType> eats = eaq.get().b;
		// now search for the focus element


		return null;
	}
}
