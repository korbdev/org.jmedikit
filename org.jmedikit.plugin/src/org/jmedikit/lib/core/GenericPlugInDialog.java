package org.jmedikit.lib.core;

public class GenericPlugInDialog {

	PlugInDialog dialog;
	
	public GenericPlugInDialog(){
		dialog = new PlugInDialog();
	}
	
	public void addInteger(String name){
		dialog.addInt(name, 0);
	}
	
	public int open(){
		return dialog.open();
	}
}
