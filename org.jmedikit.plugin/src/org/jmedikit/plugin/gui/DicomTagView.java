 
package org.jmedikit.plugin.gui;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.jmedikit.plugin.gui.events.EventConstants;

public class DicomTagView {
	
	List list;
	
	@Inject
	public DicomTagView() {

	}
	
	@PostConstruct
	public void createGui(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		list = new List(container, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		list.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.DICOM_TAGS_CHANGED) ArrayList<String> tags){
		list.removeAll();
		for(String tag : tags){
			list.add(tag);
		}
	}

}