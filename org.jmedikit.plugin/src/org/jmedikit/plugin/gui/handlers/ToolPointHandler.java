 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.SelectionToolEvent;
import org.jmedikit.plugin.gui.tools.SelectionToolFactory;

public class ToolPointHandler {
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public void execute() {
		SelectionToolFactory factory = new SelectionToolFactory();
		String tool = EventConstants.TOOL_CHANGED_POINT;
		System.out.println("PointTool called");
		eventBroker.post(EventConstants.TOOL_CHANGED_POINT, new SelectionToolEvent(factory, tool));
	}
		
}