package org.jmedikit.lib.io;

import java.io.File;

/**
 * Diese Klasse ist für den Import-Vorgang eines DICOM-Baumes innerhalb eines Plug-ins vorgesehen. Sie stellt eine Methode zum Einlesen
 * eines Verzeichnisses bereit.
 * 
 * @author rkorb
 *
 */
public class PlugInDicomImporter extends DicomImporter{

	public PlugInDicomImporter(File f) {
		super("silent", f);
	}

}
