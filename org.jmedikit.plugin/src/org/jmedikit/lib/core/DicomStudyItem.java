package org.jmedikit.lib.core;

public class DicomStudyItem extends ADicomTreeItem{

	public DicomStudyItem(String uid) {
		super(uid);
		level = ADicomTreeItem.TREE_STUDY_LEVEL;
	}

}
