package org.jmedikit.lib.core;

public class DicomStudyItem extends ADicomTreeItem{

	/**
	 * Repräsentiert den Studienknoten im DICOM-Baum und hat die Tiefe 3
	 * @param uid
	 */
	public DicomStudyItem(String uid) {
		super(uid);
		level = ADicomTreeItem.TREE_STUDY_LEVEL;
	}

}
