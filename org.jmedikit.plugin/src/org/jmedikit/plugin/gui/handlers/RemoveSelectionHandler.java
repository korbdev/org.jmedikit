 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Implementiert den Command zur Auswahlaufhebung auf alles Schichten
 * 
 * @author rkorb
 *
 */
public class RemoveSelectionHandler {
	
	@Inject
	IEventBroker broker;
	
	/**
	 * Löst das Event {@link EventConstants#SELECTION_REMOVE_ALL} aus damit die Auswahl auf allen Bildschichten aufgehoben wird.
	 */
	@Execute
	public void execute() {
		broker.send(EventConstants.SELECTION_REMOVE_ALL, EventConstants.SELECTION_REMOVE_ALL);
	}
		
}