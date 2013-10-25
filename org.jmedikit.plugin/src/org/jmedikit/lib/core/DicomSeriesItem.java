package org.jmedikit.lib.core;

public class DicomSeriesItem extends DicomTreeItem{

	public DicomSeriesItem(String uid) {
		super(uid);
		level = DicomTreeItem.TREE_SERIES_LEVEL;
	}

}
