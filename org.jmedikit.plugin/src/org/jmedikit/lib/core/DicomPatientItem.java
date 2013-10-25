package org.jmedikit.lib.core;

public class DicomPatientItem extends DicomTreeItem{
	
	public DicomPatientItem(String uid) {
		super(uid);
		level = DicomTreeItem.TREE_PATIENT_LEVEL;
	}

}
