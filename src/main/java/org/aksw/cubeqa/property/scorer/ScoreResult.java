package org.aksw.cubeqa.property.scorer;

import java.io.Serializable;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.*;
import lombok.*;

/** Result of a scoring operation with property, value and score. */
@RequiredArgsConstructor
@Getter
@ToString
public class ScoreResult implements Serializable
{
	public final ComponentProperty property;
	public final String value;

	/** 0 - not a match at all , 1 - perfect match */
	public final double score;

	public Restriction toRestriction()
	{
		if(property.scorer instanceof ObjectPropertyScorer)
		{
			return new UriRestriction(property, value);
		}
//		if(property.scorer instanceof DatatypePropertyScorer)
//		{
			return new ValueRestriction(property, value);
//		}

	}
}