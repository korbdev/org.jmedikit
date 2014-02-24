package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Dieser Handler implementiert den Command zum Öffnen des Datei-Dialogs, um ein Verzeichnis mit DICOM-Dateien einzulesen
 * 
 * @author rkorb
 *
 */
public class OpenLocationHandler {

	@Inject
	IEventBroker eventBroker;
	
	/**
	 * Zeigt einen Dateibrowser und löst das Event {@link EventConstants#FILE_OPEN_LOCATION} aus, nachdem ein Verzeichnis gewählt wurde
	 * 
	 * @param parent
	 */
	@Execute
	public void openLocation(Shell parent){
		DirectoryDialog dialog = new DirectoryDialog(parent);
		String location = dialog.open();
		eventBroker.post(EventConstants.FILE_OPEN_LOCATION, location);
	}
}
