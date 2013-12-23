 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;

public class RemoveSelectionHandler {
	
	@Inject
	IEventBroker broker;
	
	@Execute
	public void execute() {
		broker.send(EventConstants.SELECTION_REMOVE_ALL, EventConstants.SELECTION_REMOVE_ALL);
	}
		
}