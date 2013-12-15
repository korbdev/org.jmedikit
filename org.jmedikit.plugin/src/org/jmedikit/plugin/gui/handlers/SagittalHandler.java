 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;

public class SagittalHandler {
	@Inject
	IEventBroker broker;
	
	@Execute
	public void execute() {
		broker.post(EventConstants.ORIENTATION_CHANGED_SAGITTAL, EventConstants.ORIENTATION_CHANGED_SAGITTAL);
	}
}