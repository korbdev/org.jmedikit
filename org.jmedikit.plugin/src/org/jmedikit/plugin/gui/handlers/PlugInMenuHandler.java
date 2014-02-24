package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * Implementiert den Command zum Plug-in-Menü
 * 
 * @author rkorb
 *
 */
public class PlugInMenuHandler {
	
	@Inject
	IEventBroker broker;
	
	/**
	 * Löst das Event {@link EventConstants#PLUG_IN_SELECTED} aus und übergibt den Namen der Hauptklasse des Plug-ins, damit diese folgend instatiiert
	 * werden kann
	 * @param item
	 */
	@Execute
	public void execute(MMenuItem item) {
		String pluginMainClass = item.getElementId();
		broker.send(EventConstants.PLUG_IN_SELECTED, pluginMainClass);
	}
}
