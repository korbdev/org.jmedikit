package org.jmedikit.plugin.tools;

import org.jmedikit.plugin.gui.DicomCanvas;
import org.jmedikit.plugin.gui.events.EventConstants;

public class TransformationToolFactory extends AToolFactory{

	public static final String MOVE_TOOL = EventConstants.TOOL_CHANGED_MOVE;
	public static final String RESIZE_TOOL = EventConstants.TOOL_CHANGED_RESIZE;
	public static final String WINDOW_TOOL = EventConstants.TOOL_CHANGED_WINDOW;
	
	@Override
	protected ATool produce(String toolname, DicomCanvas c) {
		if(toolname.equals(MOVE_TOOL)){
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
