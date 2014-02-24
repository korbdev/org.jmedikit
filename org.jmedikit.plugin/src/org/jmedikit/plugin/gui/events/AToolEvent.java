package org.jmedikit.plugin.gui.events;

import org.jmedikit.plugin.gui.tools.AToolFactory;

/**
 * Ein AToolEvent wird nach einer Werkzeugwahl des Benutzers eingesetzt. Es enthält den Names des Werkzeugtyps, der erzeugt werden muss mit der zugehörigen Fabrik
 * zur Objekterzeugung
 * 
 * @author rkorb
 *
 */
public abstract class AToolEvent {
	private AToolFactory factory;

	private String tool;
	
	/**
	 * Erzeugt ein Event mit Werkzeugfabrik und Namen des Werkzeugtyps
	 * 
	 * @param factory Fabrik zur Werkzeugerzeugung
	 * @param tool Name des Werkzeugtyps
	 */
	public AToolEvent(AToolFactory factory, String tool){
		this.factory = factory;
		this.tool = tool;
	}
	
	public AToolFactory getFactory() {
		return factory;
	}

	public String getTool() {
		return tool;
	}
}
