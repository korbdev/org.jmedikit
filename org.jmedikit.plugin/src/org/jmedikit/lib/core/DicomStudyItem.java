package org.jmedikit.lib.core;

public class DicomStudyItem extends DicomTreeItem{

	public DicomStudyItem(String uid) {
		super(uid);
		level = DicomTreeItem.TREE_STUDY_LEVEL;
	}

}
