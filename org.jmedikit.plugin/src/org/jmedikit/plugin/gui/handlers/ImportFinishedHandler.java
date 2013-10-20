package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;

public class ImportFinishedHandler {
	@Inject
	IEventBroker eventBroker;
	

	@Execute
	public void importfinished(){
		System.out.println("Finished");
	}
}
