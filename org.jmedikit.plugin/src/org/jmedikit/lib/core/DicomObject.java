package org.jmedikit.lib.core;

import java.io.File;
import java.io.IOException;


public class DicomObject extends AbstractDicomObject{

	public DicomObject(File path) throws IOException{
		super(path);
		level = DicomTreeItem.TREE_OBJECT_LEVEL;
	}
}
