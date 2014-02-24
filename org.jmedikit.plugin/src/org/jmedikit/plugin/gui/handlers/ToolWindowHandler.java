 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.TransformationToolEvent;
import org.jmedikit.plugin.gui.tools.AToolFactory;
import org.jmedikit.plugin.gui.tools.TransformationToolFactory;
import org.jmedikit.plugin.gui.tools.WindowTool;

/**
 * Implementiert den Command zur Erzeugung des {@link WindowTool}-Werkzeugs
 * 
 * @author rkorb
 *
 */
public class ToolWindowHandler {
	
	@Inject
	IEventBroker eventBroker;
	
	/**
	 * Löst das Event {@link EventConstants#TOOL_CHANGED_WINDOW} aus. Es wird eine Instanz von {@link TransformationToolEvent} erzeugt, die 
	 * eine {@link TransformationToolFactory} sowie den Werkzeugnamen erhält.
	 */
	@Execute
	public void execute() {
		AToolFactory factory = new TransformationToolFactory();
		String tool = EventConstants.TOOL_CHANGED_WINDOW;
		eventBroker.post(EventConstants.TOOL_CHANGED_WINDOW, new TransformationToolEvent(factory, tool));
	}
		
}