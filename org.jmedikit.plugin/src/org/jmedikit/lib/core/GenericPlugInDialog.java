package org.jmedikit.lib.core;

import org.jmedikit.plugin.gui.PlugInDialog;

/**
 * Mit Hilfe der GenericPlugInDialogs können Optionsdialoge in Plug-ins eingefügt werden und sind für eine
 * Instantiierung in den konkreten Plug-ins vorgesehen. So kann in den Optionen ein Dialog mit einem Slider, Integerwerten, RadioGroups
 * und anderen Elemente versehen werden, um dem Plug-in dynamisch vom Nutzer Parameter zu übergeben.
 * 
 * @author rkorb
 *
 */
public class GenericPlugInDialog extends PlugInDialog{

	/*
	 * Dieser Wert wird zurückgegeben, wenn der Anwender OK klickt
	 */
	public static final int OK_STATUS = PlugInDialog.OK_STATUS;
	
	/*
	 * Dieser Wert wird zurückgegeben, wenn der Anwender Cancel klickt
	 */
	public static final int CANCEL_STATUS = PlugInDialog.CANCEL_STATUS;
	
	//PlugInDialog dialog;
	
	/**
	 * Der Konstruktor erzeugt ein Dialogfenster mit einem Titel und einer Beschreibung. Die Methode open()
	 * zeigt den Dialog an.
	 * 
	 * @param title Titel
	 * @param message Beschreibung
	 */
	public GenericPlugInDialog(String title, String message) {
		super(title, message);
		//dialog = new PlugInDialog(title, message);
	}
	
	/*public int open(){
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
	}*/
}
