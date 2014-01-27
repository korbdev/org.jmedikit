 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.TransformationToolEvent;
import org.jmedikit.plugin.gui.tools.AToolFactory;
import org.jmedikit.plugin.gui.tools.TransformationToolFactory;

public class ToolMoveHandler {
	
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public void execute() {
		AToolFactory factory = new TransformationToolFactory();
		String tool = EventConstants.TOOL_CHANGED_MOVE;
		System.out.println("MoveTool called");
		eventBroker.post(EventConstants.TOOL_CHANGED_MOVE, new TransformationToolEvent(factory, tool));
	}
		
}