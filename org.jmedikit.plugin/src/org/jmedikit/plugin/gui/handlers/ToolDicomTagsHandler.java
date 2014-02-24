 
package org.jmedikit.plugin.gui.handlers;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.plugin.gui.ImageViewComposite;
import org.jmedikit.plugin.gui.ImageViewPart;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Implementiert den Command zur Darstellung der DICOM-Tags eines DICOM-Objekts
 * 
 * @author rkorb
 *
 */
public class ToolDicomTagsHandler {
	
	@Inject
	IEventBroker broker;
	
	/**
	 * Löst das Event {@link EventConstants#DICOM_TAGS_CHANGED} aus. Aus dem aktiven {@link ImageViewComposite} wird das aktuelle DICOM-Objekt
	 * ausgelesen und die Tags daraus ermittelt.
	 */
	@Execute
	public void execute() {
		//MPart toolcontrol = (MPart) service.find("org.jmedikit.plugin.imageview", app);
		//ImageViewPart ivp = (ImageViewPart) toolcontrol.getObject();
		ArrayList<String[]> tags = new ArrayList<String[]>();
		try {
			DicomObject obj = ImageViewPart.getActiveImageViewComposite().getCurrentDicomObject();
			tags = obj.getTags();
		} catch (Exception e) {
			String[] nodata = new String[]{null, null, null, null, null};
			tags.add(nodata);
			e.printStackTrace();
		}
		
		broker.post(EventConstants.DICOM_TAGS_CHANGED, tags);
	}
		
}