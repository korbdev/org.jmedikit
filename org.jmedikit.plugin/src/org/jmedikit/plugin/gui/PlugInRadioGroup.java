package org.jmedikit.plugin.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Eine RadioGroup dient zur Auswahl eines Elements aus einer vorgegebenen Liste
 * 
 * @author rkorb
 *
 */
public class PlugInRadioGroup implements IPlugInDialogItem{

	private String name;
	
	private String value;

	private String[] labels;
	
	private int selectedIndex;
	
	/**
	 * Erzeugt eine RadioGroup
	 * 
	 * @param name Ein innerhalb des Dialogs eindeutiger Name
	 * @param labels String-Array der Listenelemente
	 * @param selectedIndex asugewählter Index
	 */
	public PlugInRadioGroup(String name, String[] labels, int selectedIndex) {
		this.name = name;
		value = labels[selectedIndex];
		this.selectedIndex = selectedIndex;
		this.labels = labels;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object o) {
		value = (String)o;
	}

	@Override
	public void getSWTObject(Composite parent) {
		Composite c = new Composite(parent, SWT.BORDER);
		GridLayout grid = new GridLayout(1, true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		c.setLayout(grid);
		c.setLayoutData(data);
		
		Label label = new Label(c, SWT.NONE);
		label.setText(name);
		
		Composite radios = new Composite(c, SWT.NONE);
		GridLayout radiosLayout = new GridLayout(6, true);
		GridData radiosData = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		radios.setLayout(radiosLayout);
		radios.setLayoutData(radiosData);
		
		for(int i = 0; i < labels.length; i++){
			Label l = new Label(radios, SWT.NONE);
			l.setText(labels[i]);
			Button b = new Button(radios, SWT.RADIO);
			if(i == selectedIndex){
				b.setSelection(true);
			}
			RadioListener listener = new RadioListener(labels[i]);
			b.addListener(SWT.Selection, listener);
		}
	}
	private class RadioListener implements Listener{

		protected String val;
		
		RadioListener(String val){
			this.val = val;
		}
		
		@Override
		public void handleEvent(Event event) {
			PlugInRadioGroup.this.value = val;
		}
		
	}
}
