package org.jmedikit.plugin.gui.handlers;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.jmedikit.plugin.gui.events.EventConstants;

public class OpenLocationHandler {

	@Inject
	IEventBroker eventBroker;
	
	/**
	 * Zeigt einen Dateibrowser
	 * 
	 * @param parent
	 */
	@Execute
	public void openLocatione(Shell parent){
		DirectoryDialog dialog = new DirectoryDialog(parent);
		String location = dialog.open();
		eventBroker.post(EventConstants.FILE_OPEN_LOCATION, location);
		//File f = new File(location);
		//System.out.println(f.isDirectory());
	}
}