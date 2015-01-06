package de.konradhoeffner.commons;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;

public class StdOutConsoleHandler extends ConsoleHandler
{
	protected void setOutputStream(OutputStream out) throws SecurityException
	{
		super.setOutputStream(System.out);
	}
}