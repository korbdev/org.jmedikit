package org.jmedikit.plugin.gui.tools;

import org.jmedikit.plugin.gui.DicomCanvas;
import org.jmedikit.plugin.gui.events.EventConstants;

public class SelectionToolFactory extends AToolFactory{

	public static final String POINT_TOOL = EventConstants.TOOL_CHANGED_POINT;
	
	@Override
	protected ATool produce(String toolname, DicomCanvas c) {
		if(toolname.equals(POINT_TOOL)){
			System.out.println("CREATE NEW POINT TOOL");
			return new PointTool(c);
		}
		else throw new IllegalArgumentException("Tool "+toolname+" not available");
	}

}
