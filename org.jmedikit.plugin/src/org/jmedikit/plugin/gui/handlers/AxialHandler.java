 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Implementierung des Commands für den Aufruf der axialen Ebenenrekonstruktion
 * 
 * @author rkorb
 *
 */
public class AxialHandler {
	
	@Inject
	IEventBroker broker;
	
	/**
	 * Löst das Gruppen-Event {@link EventConstants#ORIENTATION_CHANGED_ALL}
	 *  mit dem konkreten Typ {@link EventConstants#ORIENTATION_CHANGED_AXIAL} auf.
	 */
	@Execute
	public void execute() {
		broker.post(EventConstants.ORIENTATION_CHANGED_AXIAL, EventConstants.ORIENTATION_CHANGED_AXIAL);
	}
		
}