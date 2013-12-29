package org.jmedikit.lib.core;

import org.jmedikit.plugin.gui.PlugInDialog;

public class GenericPlugInDialog{

	public static final int OK_STATUS = PlugInDialog.OK_STATUS;
	public static final int CANCEL_STATUS = PlugInDialog.CANCEL_STATUS;
	
	PlugInDialog dialog;
	
	public GenericPlugInDialog(String title, String message) {
		dialog = new PlugInDialog(title, message);
	}
	
	public int open(){
		return dialog.open();
	}
	
	public Object getItemValue(String name){
		return dialog.getItemValue(name);
	}
	
	public void addStringValue(String name, String defaultValue){
		dialog.addStringItem(name, defaultValue);
	}
	
	public void addIntegerValue(String name, int defaultValue){
		dialog.addIntegerItem(name, defaultValue);
	}
	
	public void addFloatValue(String name, float defaultValue){
		dialog.addFloatItem(name, defaultValue);
	}
	
	public void addSlider(String name, int defaultValue, int min, int max, int increment, int digits){
		dialog.addSlider(name, defaultValue, min, max, increment, digits);
	}
	
	public void addRadioGroup(String name, String[] labels, int selectedIndex){
		dialog.addRadioGroup(name, labels, selectedIndex);
	}

	public void addCheckButton(String name, boolean defaultValue){
		dialog.addCheckButton(name, defaultValue);
	}
	
	public void addFileDialog(String name, String defaultValue){
		dialog.addFileDialog(name, defaultValue);
	}
}
