 
package org.jmedikit.plugin.gui;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class DicomNetwork {

	@PostConstruct
	public void postConstruct(Composite parent) {
		
		//definiert das Layout des Elternelements
		//2 Spalten mit gleicher Breite
		GridLayout pGrid = new GridLayout(2, true);
		GridData parentData = 
				new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayout(pGrid);
		parent.setLayoutData(parentData);
		
		//GUI
		
		//Suchfeld
		Text search = new Text(parent, SWT.BORDER);
        search.setText("");
		//Suchbutton
		Button button = new Button(parent, SWT.NONE);
		button.setText("Suche PACS");
		
	}	
}