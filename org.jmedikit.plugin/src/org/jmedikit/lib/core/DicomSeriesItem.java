package org.jmedikit.lib.core;

public class DicomSeriesItem extends ADicomTreeItem{

	/**
	 * Repräsentiert den Serienknoten im DICOM-Baum und hat die Tiefe 2
	 * @param uid
	 */
	public DicomSeriesItem(String uid) {
		super(uid);
		level = ADicomTreeItem.TREE_SERIES_LEVEL;
	}

}
