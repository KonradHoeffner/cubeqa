package org.aksw.cubeqa.deprecated;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aksw.cubeqa.template.CubeTemplate;

/**A single benchmark question along with its correct template.*/
@RequiredArgsConstructor
@ToString
class BenchmarkElementOld {
	public final String question;
	public final boolean doable;
	public final CubeTemplate template;
}