package org.jmedikit.plugin.gui.tools;

import org.jmedikit.plugin.gui.DicomCanvas;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Implementierung einer konkreten Fabrik
 * 
 * Verwendete Tools sollten einen Auswahlcharakter besitzen
 * (Punktauswahl, Rechteckauswahl,...)
 * 
 * Verfügbare Tools sind die Konstanten dieser Klasse
 * 
 * @author rkorb
 *
 */
public class SelectionToolFactory extends AToolFactory{

	public static final String POINT_TOOL = EventConstants.TOOL_CHANGED_POINT;
	
	public static final String ROI_TOOL = EventConstants.TOOL_CHANGED_ROI;
	/**
	 * Gibt ein Tool abhängig von toolname zurück
	 * 
	 * Ist ein Tool nicht vorhanden wird eine IllegalArgumentException geworfen
	 * 
	 * @param String toolname Zu erzeugendes Tool
	 * @param DicomCanvas c Canvas, welches das Tool verwenden soll
	 * 
	 * @return ATool 
	 */
	@Override
	protected ATool produce(String toolname, DicomCanvas c) {
		if(toolname.equals(POINT_TOOL)){
			return new PointTool(c);
		}
		else if(toolname.equals(ROI_TOOL)){
			return new RoiTool(c);
		}
		else throw new IllegalArgumentException("Tool "+toolname+" not available");
	}

}
