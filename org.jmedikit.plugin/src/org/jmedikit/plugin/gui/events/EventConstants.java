package org.jmedikit.plugin.gui.events;

public class EventConstants {
	public final static String FILE_OPEN_LOCATION = "OPEN_LOCATION";
	public final static String FILE_IMPORT_FINISHED ="FILE_IMPORT_FINISHED";
	public final static String DICOMBROWSER_ITEM_SELECTION = "DICOM_BROWSER_SELECTION";
	
	public final static String TOOL_CHANGED_ALL = "TOOL_CHANGED/*";
	public final static String TOOL_CHANGED_DEFAULT = "TOOL_CHANGED/DEFAULT";
	public final static String TOOL_CHANGED_MOVE = "TOOL_CHANGED/MOVE";
	public final static String TOOL_CHANGED_RESIZE = "TOOL_CHANGED/RESIZE";
	public final static String TOOL_CHANGED_WINDOW = "TOOL_CHANGED/WINDOW";
	public final static String TOOL_CHANGED_POINT = "TOOL_CHANGED/POINT";
	public final static String TOOL_CHANGED_ROI = "TOOL_CHANGED/ROI";
	
	public final static String SELECTION_ALL = "SELECTION/*";
	public final static String SELECTION_REMOVE_ALL = "SELECTION/REMOVE_ALL";
	public final static String SELECTION_REMOVE_SINGLE = "SELECTION/REMOVE_SINGLE";
	
	public final static String ANGLE_CHANGED_ALL = "ANGLE_CHANGED/*";
	public final static String ANGLE_CHANGED_ALPHA = "ANGLE_CHANGED/ALPHA";
	public final static String ANGLE_CHANGED_BETA = "ANGLE_CHANGED/BETA";
	public final static String ANGLE_CHANGED_GAMMA = "ANGLE_CHANGED/GAMMA";
	
	public final static String ORIENTATION_CHANGED_ALL = "ORIENTATION_CHANGED/*";
	public final static String ORIENTATION_CHANGED_AXIAL = "ORIENTATION_CHANGED/AXIAL";
	public final static String ORIENTATION_CHANGED_CORONAL = "ORIENTATION_CHANGED/CORONAL";
	public final static String ORIENTATION_CHANGED_SAGITTAL = "ORIENTATION_CHANGED/SAGITTAL";
	
	public final static String IMAGES_LOADED = "IMAGES_LOADED";
	
	public final static String PLUG_IN_SELECTED = "PLUG_IN_SELECTED";
	public final static String PLUG_IN_ERROR = "PLUG_IN_ERROR";
	
	public final static String DICOM_TAGS_CHANGED = "DICOM_TAGS_CHANGED";
}
