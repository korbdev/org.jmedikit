 
package org.jmedikit.plugin.gui;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;

public class ConsoleView {
	
	@Inject
	private Logger logger;
	
	@Inject
	public ConsoleView() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void createGUI(Composite parent) {
		logger.error("HALLO ERROR");
	}
	
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
}