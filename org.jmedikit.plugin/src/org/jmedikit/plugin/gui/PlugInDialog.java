package org.jmedikit.plugin.gui;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * PlugInDialog stellt die Methoden zur Erstellung eines Plug-in-Optionsdialog zur Verf�gung 
 * und dient zur dynamischen Parameter�bergabe f�r die Plug-in-Entwicklung
 * 
 * @author rkorb
 *
 */
public class PlugInDialog extends TitleAreaDialog{

	public static final int OK_STATUS = TitleAreaDialog.OK;
	public static final int CANCEL_STATUS = TitleAreaDialog.CANCEL;
	
	/**
	 * Titel des Dialogfensters
	 */
	private String title;
	
	/**
	 * Beschreibung, die im Fenster angezeigt wird
	 */
	private String message;
	
	/**
	 * Eine Liste aller enthaltenen Dialogelemente
	 */
	public ArrayList<IPlugInDialogItem> items;
	
	/**
	 * Erzeugt einen leeren Dialog mit einem Titel und einer Beschreibung
	 * 
	 * @param title Titel
	 * @param message Beschreibung
	 */
	public PlugInDialog(String title, String message) {
		super(ImageViewPart.getPartShell());
		this.title = title;
		this.message = message;
		items = new ArrayList<IPlugInDialogItem>();
	}

	/**
	 * Gibt den Wert des Dialogelements mit dem im Parameter spezifizierten Namen zur�ck
	 * 
	 * @param name
	 * @return Wert des Dialogelements oder null wenn das Element nicht gefunden wird
	 */
	public Object getItemValue(String name){
		for(IPlugInDialogItem item : items){
			if(item.getName().equals(name)){
				return item.getValue();
			}
		}
		return null;
	}
	
	/**
	 * �ffnet das Dialogfenster und macht es f�r den Anwender sichtbar
	 */
	public int open(){
		return super.open();
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(title);
		setMessage(message);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		for(IPlugInDialogItem item : items){
			item.getSWTObject(container);
		}
		return area;
	}
	
	/**
	 * F�gt dem Dialog ein Feld {@link PlugInStringValue} f�r die Eingabe von Zeichenketten hinzu.
	 * 
	 * @see {@link PlugInStringValue}
	 * @param name Ein im Dialog einzigartiger Name
	 * @param defaultValue Der Standardwert des Dialogelements
	 */
	public void addStringItem(String name, String defaultValue){
		items.add(new PlugInStringValue(name, defaultValue));
	}
	
	/**
	 * F�gt dem Dialog ein Feld {@link PlugInIntegerValue} f�r die Eingabe von ganzahligen Werten hinzu.
	 * 
	 * @see {@link PlugInIntegerValue}
	 * @param name Ein im Dialog einzigartiger Name
	 * @param defaultValue Der Standardwert des Dialogelements
	 */
	public void addIntegerItem(String name, int defaultValue){
		items.add(new PlugInIntegerValue(name, defaultValue));
	}
	
	/**
	 * F�gt dem Dialog ein Feld {@link PlugInFloatValue} f�r die Eingabe von Flie�kommawerten hinzu.
	 * 
	 * @see {@link PlugInFloatValue}
	 * @param name Ein im Dialog einzigartiger Name
	 * @param defaultValue Der Standardwert des Dialogelements
	 */
	public void addFloatItem(String name, float defaultValue){
		items.add(new PlugInFloatValue(name, defaultValue));
	}
	
	/**
	 * F�gt dem Dialog ein Feld {@link PlugInSlider} f�r die Eingabe von Intervallen hinzu.
	 * 
	 * @see {@link PlugInSlider}
	 * @param name Ein im Dialog einzigartiger Name
	 * @param defaultValue Der Standardwert des Dialogelements
	 */
	public void addSlider(String name, int defaultValue, int min, int max, int increment, int digits){
		items.add(new PlugInSlider(name, defaultValue, min, max, increment, digits));
	}
	
	/**
	 * F�gt dem Dialog ein Feld {@link PlugInRadioGroup} f�r eine Listenauswahl hinzu.
	 * 
	 * @see {@link PlugInRadioGroup}
	 * @param name Ein im Dialog einzigartiger Name
	 * @param defaultValue Der Standardwert des Dialogelements
	 */
	public void addRadioGroup(String name, String[] labels, int selectedIndex){
		items.add(new PlugInRadioGroup(name, labels, selectedIndex));
	}
	
	/**
	 * F�gt dem Dialog ein Feld {@link PlugInCheckButton} f�r die Auswahl der Wahrheitswerte true und false hinzu.
	 * 
	 * @see {@link PlugInCheckButton}
	 * @param name Ein im Dialog einzigartiger Name
	 * @param defaultValue Der Standardwert des Dialogelements
	 */
	public void addCheckButton(String name, boolean defaultValue){
		items.add(new PlugInCheckButton(name, defaultValue));
	}
	
	/**
	 * F�gt dem Dialog ein Feld {@link PlugInFileDialog} f�r die Auswahl von Dateipfaden hinzu.
	 * 
	 * @see {@link PlugInFileDialog}
	 * @param name Ein im Dialog einzigartiger Name
	 * @param defaultValue Der Standardwert des Dialogelements
	 */
	public void addFileDialog(String name, String defaultValue){
		items.add(new PlugInFileDialog(name, defaultValue));
	}
}
