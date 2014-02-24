package org.jmedikit.lib.core;

import java.io.File;
import java.io.IOException;


public class DicomObject extends ADicomObject{
	
	/**
	 * Repräsentiert ein konkretes DICOM-Objekt mit den Methoden von {@link ADicomObject}. Für die Darstellung im DICOM-Baum haben Objekte die
	 * Tiefe 4 und entsprechen der Objektebene
	 * 
	 * @param path Pfad zur DICOM-Datei
	 * @throws IOException
	 */
	public DicomObject(File path) throws IOException{
		super(path);
		level = ADicomTreeItem.TREE_OBJECT_LEVEL;
	}
}
