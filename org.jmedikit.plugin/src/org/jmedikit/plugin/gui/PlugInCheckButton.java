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
 * Der CheckButton symbolisiert ein Formualrelement für die Eingaben von den Wahrheitswerten true und false
 * 
 * @author rkorb
 *
 */
public class PlugInCheckButton implements IPlugInDialogItem{

	private String name;
	
	private boolean value;
	
	/**
	 * Erzeugt ein Formularelement
	 * 
	 * @param name Ein innerhalb des Dialogs eindeutiger Name
	 * @param defaultValue Standardwert des Elements
	 */
	public PlugInCheckButton(String name, boolean defaultValue) {
		this.name = name;
		value = defaultValue;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object o) {
		value = (boolean)o;
	}

	@Override
	public void getSWTObject(Composite parent) {
		Composite c = new Composite(parent, SWT.BORDER);
		GridLayout grid = new GridLayout(2, false);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true, 0, 0);
		c.setLayout(grid);
		c.setLayoutData(data); 
		
		Label label = new Label(c, SWT.NONE);
		GridData labelData = new GridData();
		labelData.widthHint = 100;
		label.setLayoutData(labelData);
		label.setText(name);
		
		final Button check = new Button(c, SWT.CHECK);
		GridData checkData = new GridData(SWT.FILL, SWT.CENTER, true, true);
		check.setLayoutData(checkData);
		check.setSelection(value);
		
		
		check.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				value = check.getSelection();
			}
		});
	}

}
