package org.jmedikit.plugin.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * FloatValue repr‰sentiert ein Dialog-Element zur Eingabe von Flieﬂkommawerten
 * 
 * @author rkorb
 *
 */
public class PlugInFloatValue implements IPlugInDialogItem{

	private String name;
	
	private float value;
	
	/**
	 * Erzeugt ein Formularelement
	 * 
	 * @param name Ein innerhalb des Dialogs eindeutiger Name
	 * @param defaultValue Standardwert des Elements
	 */
	public PlugInFloatValue(String name, float defaultValue){
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
		value = (float)o;
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
		
		final Text text = new Text(c, SWT.SINGLE | SWT.BORDER);
		GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, true);
		text.setLayoutData(textData);
		text.setText(value+"");
		
		
		text.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String newString = text.getText();
				if(newString.equals("")){
					value = Float.NaN;
				}
				else value = Float.parseFloat(newString);
				System.out.println(value);
			}
		});
	}

}
