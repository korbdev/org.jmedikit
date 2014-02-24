package org.jmedikit.lib.core;

public class DicomPatientItem extends ADicomTreeItem{
	
	/**
	 * Repräsentiert den Patientenknoten im DICOM-Baum und hat die Tiefe 1
	 * @param uid
	 */
	public DicomPatientItem(String uid) {
		super(uid);
		level = ADicomTreeItem.TREE_PATIENT_LEVEL;
	}

}
