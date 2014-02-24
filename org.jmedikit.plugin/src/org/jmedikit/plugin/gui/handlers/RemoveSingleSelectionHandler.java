 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Implementiert den Command zur Auswahlaufhebung einer spezifischen Bildschicht
 * 
 * @author rkorb
 *
 */
public class RemoveSingleSelectionHandler {
	
	@Inject
	IEventBroker broker;
	
	/**
	 * Löst das Event {@link EventConstants#SELECTION_REMOVE_SINGLE} aus, damit die Auswahl auf einer Bildschicht aufgehoben wird.
	 */
	@Execute
	public void execute() {
		broker.send(EventConstants.SELECTION_REMOVE_SINGLE, EventConstants.SELECTION_REMOVE_SINGLE);
	}
		
}