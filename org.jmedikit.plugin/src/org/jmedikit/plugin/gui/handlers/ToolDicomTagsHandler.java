 
package org.jmedikit.plugin.gui.handlers;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.plugin.gui.ImageViewPart;
import org.jmedikit.plugin.gui.events.EventConstants;

public class ToolDicomTagsHandler {
	
	@Inject
	IEventBroker broker;
	
	@Execute
	public void execute() {
		//MPart toolcontrol = (MPart) service.find("org.jmedikit.plugin.imageview", app);
		//ImageViewPart ivp = (ImageViewPart) toolcontrol.getObject();
		ArrayList<String> tags = new ArrayList<String>();
		try {
			DicomObject obj = ImageViewPart.getActiveImageViewComposite().getCurrentDicomObject();
			tags = obj.getTags();
		} catch (Exception e) {
			tags.add("Dicom Tags sind f�r das aktuelle Bild nicht verf�gbar");
			e.printStackTrace();
		}
		
		broker.post(EventConstants.DICOM_TAGS_CHANGED, tags);
	}
		
}