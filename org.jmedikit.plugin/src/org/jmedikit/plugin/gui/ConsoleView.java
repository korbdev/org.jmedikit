 
package org.jmedikit.plugin.gui;


import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.util.ImageProvider;

public class ConsoleView {
	
	
	@Inject
	IEventBroker broker;
	
	Text errorMsg;
	
	@Inject
	private IResourcePool resourcePool;
	
	Image clearImage;
	
	@Inject
	public ConsoleView() {

	}
	
	@PostConstruct
	public void createGUI(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		clearImage = resourcePool.getImageUnchecked(ImageProvider.CONSOLE_CLEAR);
		
		errorMsg = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		errorMsg.setLayoutData(new GridData(GridData.FILL_BOTH));
		Color errorColor = new Color(Display.getCurrent(), new RGB(255, 0, 0));
		Font f = new Font(Display.getCurrent(), "Courier New", 11, SWT.BOLD);
		errorMsg.setFont(f);
		errorMsg.setForeground(errorColor);

		ToolBar tools = new ToolBar(container, SWT.VERTICAL);
		tools.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		ToolItem clear = new ToolItem(tools, SWT.NONE);
		clear.setImage(clearImage);
		
		clear.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				errorMsg.setText("");
			}
		});
		//errorColor.dispose();
		//f.dispose();
	}

	@Inject
	@Optional
	public void getNotifiedPlugInError(@UIEventTopic(EventConstants.PLUG_IN_ERROR) String error){
		if(errorMsg.getText().equals("")){
			errorMsg.setText(error);
		}
		else errorMsg.setText(errorMsg.getText() + "\n" + error);
		
	}
	
}