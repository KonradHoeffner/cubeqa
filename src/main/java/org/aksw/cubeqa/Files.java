package org.aksw.cubeqa;

import java.io.File;

/** Determines the location of output files. Prefered location is cacheFolder config. */
public class Files
{
	/**	Returns Tests if this code runs from inside a jar file.
	 * @return true if running inside a jar, otherwise false */
	public static boolean isRunningInJar()
	{
		String className = Files.class.getName().replace('.', '/');
		String classJar = Files.class.getResource("/" + className + ".class").toString();
		return classJar.startsWith("jar:");
	}
		
	/** 
	 * @param name the folder name
	 * @return a folder local to the code, if running outside a jar, otherwise a folder in the temporary directory.
	 * Warning: temporary directories may be deleted at system reboot. */
	public static File localFolder(String name)
	{
		if(Config.INSTANCE.folder!=null) return new File(Config.INSTANCE.folder,"name");
		return new File(new File(isRunningInJar()?System.getProperty("java.io.tmpdir"):"."),name);
	}
	
}