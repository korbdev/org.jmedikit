package org.jmedikit.lib.core;

public class DicomSeriesItem extends ADicomTreeItem{

	public DicomSeriesItem(String uid) {
		super(uid);
		level = ADicomTreeItem.TREE_SERIES_LEVEL;
	}

}
