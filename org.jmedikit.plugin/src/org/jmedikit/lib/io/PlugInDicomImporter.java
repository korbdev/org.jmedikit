package org.jmedikit.lib.io;

import java.io.File;

public class PlugInDicomImporter extends DicomImporter{

	public PlugInDicomImporter(File f) {
		super("silent", f);
	}

}
