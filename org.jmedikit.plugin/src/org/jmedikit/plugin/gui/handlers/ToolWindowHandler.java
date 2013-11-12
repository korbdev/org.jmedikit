 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.TransformationToolEvent;
import org.jmedikit.plugin.tools.TransformationToolFactory;

public class ToolWindowHandler {
	
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public void execute() {
		TransformationToolFactory factory = new TransformationToolFactory();
		String tool = EventConstants.TOOL_CHANGED_WINDOW;
		eventBroker.post(EventConstants.TOOL_CHANGED_WINDOW, new TransformationToolEvent(factory, tool));
	}
		
}