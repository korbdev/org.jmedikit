package org.jmedikit.plugin.gui.events;

import org.jmedikit.plugin.gui.tools.AToolFactory;

public class SelectionToolEvent extends AToolEvent{

	public SelectionToolEvent(AToolFactory factory, String tool) {
		super(factory, tool);
	}

}
