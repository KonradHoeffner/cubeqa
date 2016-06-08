package org.kohsuke.args4j;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

/** Co-opt args4j CmdLineParser to accept parameter maps instead.  */
public class MapParser
{
	private CmdLineParser parser;

	public MapParser(CmdLineParser parser) {this.parser=parser;}

	@SneakyThrows
	public void parse(Map<String,Object> args)
	{
		parser.parseArgument(
		args.entrySet().stream().map(e->"-"+e.getKey()+"="+e.getValue()).collect(Collectors.toList()));
	}
}