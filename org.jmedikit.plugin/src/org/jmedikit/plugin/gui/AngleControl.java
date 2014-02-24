/**
 * Nicht im Einsatz, war ursprünglich die Oberfläche zur Angabe von Winkeln für die Multiplanare Rekonstruktion
 * 
 * @author rkorb
 *
 */

//package org.jmedikit.plugin.gui;

/*import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.jmedikit.plugin.gui.events.EventConstants;*/

/*public class AngleControl {
	//MPR nicht im Einsatz
	@Inject
	IEventBroker broker;
	
	private Composite controlContainer;
	
	private Spinner alpha, beta, gamma;
	
	private Listener angleChangedListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			int[] selection = new int[3];
			selection[0] = alpha.getSelection();
			selection[1] = beta.getSelection();
			selection[2] = gamma.getSelection();
			broker.send(EventConstants.ANGLE_CHANGED_ALPHA, selection);
		}
	};

	
	@PostConstruct
	public void constructGUI(Composite parent){
		parent.setLayout(new FillLayout());
		controlContainer = new Composite(parent, SWT.NONE);
		GridLayout controlContainerLayout = new GridLayout(3, true);
		controlContainer.setLayout(controlContainerLayout);
		
		int controlWidth = 35;
		int angleMax = 90;
		int angleMin = -90;
		
		alpha = new Spinner(controlContainer, SWT.NONE);
		GridData alphaData = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		alphaData.widthHint = controlWidth;
		alpha.setLayoutData(alphaData);
		alpha.setMinimum(angleMin);
		alpha.setMaximum(angleMax);
		
		beta = new Spinner(controlContainer, SWT.NONE);
		GridData betaData = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		betaData.widthHint = controlWidth;
		beta.setLayoutData(betaData);
		beta.setMinimum(angleMin);
		beta.setMaximum(angleMax);
		
		gamma = new Spinner(controlContainer, SWT.NONE);
		GridData gammaData = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		gammaData.widthHint = controlWidth;
		gamma.setLayoutData(gammaData);
		gamma.setMinimum(angleMin);
		gamma.setMaximum(angleMax);
		
		alpha.addListener(SWT.Selection, angleChangedListener);
		beta.addListener(SWT.Selection, angleChangedListener);
		gamma.addListener(SWT.Selection, angleChangedListener);
	}
}*/
