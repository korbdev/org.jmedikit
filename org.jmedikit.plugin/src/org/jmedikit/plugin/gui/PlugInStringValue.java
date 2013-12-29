package org.jmedikit.plugin.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class PlugInStringValue implements IPlugInDialogItem{
	
	private String stringValue;
	
	private String name;
	
	public PlugInStringValue(String name, String defaultValue) {
		this.name = name;
		this.stringValue = defaultValue;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public Object getValue() {
		return stringValue;
	}

	@Override
	public void setValue(Object o) {
		String s = (String)o;
		stringValue = s;
	}

	@Override
	public void getSWTObject(Composite parent) {
		//Label label = new Label(parent, SWT.NONE);
		//label.setText("LABELTEST");
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
		
		final Text text = new Text(c, SWT.SINGLE | SWT.BORDER);
		GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, true);
		text.setLayoutData(textData);
		text.setText(stringValue);
		
		
		text.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String newString = text.getText();
				stringValue = newString;
			}
		});
	}
}
