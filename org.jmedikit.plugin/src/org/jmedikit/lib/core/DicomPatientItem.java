package org.jmedikit.lib.core;

public class DicomPatientItem extends ADicomTreeItem{
	
	public DicomPatientItem(String uid) {
		super(uid);
		level = ADicomTreeItem.TREE_PATIENT_LEVEL;
	}

}
