package org.jmedikit.plugin.gui.events;

import org.jmedikit.plugin.tools.AToolFactory;

public class AToolEvent {
	private AToolFactory factory;

	private String tool;
	
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
