 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.SelectionToolEvent;
import org.jmedikit.plugin.gui.tools.AToolFactory;
import org.jmedikit.plugin.gui.tools.SelectionToolFactory;

public class ToolRoiHandler {
	
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public void execute() {
		
		AToolFactory factory = new SelectionToolFactory();
		String tool = EventConstants.TOOL_CHANGED_ROI;

		eventBroker.post(EventConstants.TOOL_CHANGED_ROI, new SelectionToolEvent(factory, tool));
	}
}