package org.jmedikit.plugin.gui.tools;

import org.jmedikit.plugin.gui.DicomCanvas;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Implementierung einer konkreten Fabrik
 * 
 * Verwendete Tools sollen alle einen Transformationscharakter haben.
 * (Skalierung, Rotation, Verschieben,...)
 * 
 * Verfügbare Tools sind die Konstanten dieser Klasse
 * 
 * @author rkorb
 *
 */
public class TransformationToolFactory extends AToolFactory{

	public static final String MOVE_TOOL = EventConstants.TOOL_CHANGED_MOVE;
	public static final String RESIZE_TOOL = EventConstants.TOOL_CHANGED_RESIZE;
	public static final String WINDOW_TOOL = EventConstants.TOOL_CHANGED_WINDOW;
	public static final String DEFAULT_TOOL = EventConstants.TOOL_CHANGED_DEFAULT;
	
	
	/**
	 * Gibt ein Tool abhängig von toolname zurück
	 * 
	 * Ist ein Tool nicht vorhanden wird eine IllegalArgumentException geworfen
	 * 
	 * @param String toolname Zu erzeugendes Tool
	 * @param DicomCanvas c Canvas, welches das Tool verwenden soll
	 * @return ATool 
	 */
	@Override
	protected ATool produce(String toolname, DicomCanvas c) {
		if(toolname.equals(DEFAULT_TOOL)){
			return new DefaultTool(c);
		}
		else if(toolname.equals(MOVE_TOOL)){
			return new MoveTool(c);
		}
		else if(toolname.equals(RESIZE_TOOL)){
			return new ResizeTool(c);
		}
		else if(toolname.equals(WINDOW_TOOL)){
			return new WindowTool(c); 
		}
		else throw new IllegalArgumentException("Tool "+toolname+" not available");
	}

}
