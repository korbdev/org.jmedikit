package org.jmedikit.lib.core;

import java.io.File;
import java.io.IOException;


public class DicomObject extends ADicomObject{

	public DicomObject(File path) throws IOException{
		super(path);
		level = ADicomTreeItem.TREE_OBJECT_LEVEL;
	}
}
