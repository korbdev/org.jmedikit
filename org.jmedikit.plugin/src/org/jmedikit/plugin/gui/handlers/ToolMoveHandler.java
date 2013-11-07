 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;

public class ToolMoveHandler {
	
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public void execute() {
		System.out.println("Move");
		eventBroker.post(EventConstants.TOOL_CHANGED, "MoveTool");
	}
		
}