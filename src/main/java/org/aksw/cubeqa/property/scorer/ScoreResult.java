package org.aksw.cubeqa.property.scorer;

import java.io.Serializable;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.Restriction;
import org.aksw.cubeqa.restriction.UriRestriction;
import org.aksw.cubeqa.restriction.ValueRestriction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/** Result of a scoring operation with property, value and score. */
@RequiredArgsConstructor
@Getter
@ToString
public class ScoreResult implements Serializable
{
	public final ComponentProperty property;
	public final String value;
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