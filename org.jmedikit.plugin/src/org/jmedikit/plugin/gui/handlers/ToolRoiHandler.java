 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.SelectionToolEvent;
import org.jmedikit.plugin.gui.tools.AToolFactory;
import org.jmedikit.plugin.gui.tools.RoiTool;
import org.jmedikit.plugin.gui.tools.SelectionToolFactory;

/**
 * Implementiert den Command zur Erzeugung des {@link RoiTool}-Werkzeugs
 * 
 * @author rkorb
 *
 */
public class ToolRoiHandler {
	
	@Inject
	IEventBroker eventBroker;
	
	/**
	 * Löst das Event {@link EventConstants#TOOL_CHANGED_ROI} aus. Es wird eine Instanz von {@link SelectionToolEvent} erzeugt, die 
	 * eine {@link SelectionToolFactory} sowie den Werkzeugnamen erhält.
	 */
	@Execute
	public void execute() {
		
		AToolFactory factory = new SelectionToolFactory();
		String tool = EventConstants.TOOL_CHANGED_ROI;

		eventBroker.post(EventConstants.TOOL_CHANGED_ROI, new SelectionToolEvent(factory, tool));
	}
}