package org.jmedikit.plugin.gui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * StatusControl zeigt den Fortschritt beim Import der DICOM-Objekte, nachdem ein Order ausgewählt wurde
 * 
 * @author rkorb
 *
 */
public class StatusControl implements IProgressMonitor{
	
	private ProgressBar bar;
	
	@Inject
	IEventBroker broker;
	
	@Inject
	private UISynchronize sync;
	
	@PostConstruct
	public void constructGUI(Composite parent){
		parent.setLayout(new FillLayout());
		bar = new ProgressBar(parent, SWT.SMOOTH);
		System.out.println(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void beginTask(final String name, final int totalWork) {
		sync.syncExec(new Runnable() {
			
			@Override
			public void run() {
				bar.setMaximum(totalWork);
				bar.setToolTipText(name);
			}
		});
	}

	@Override
	public void done() {
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				bar.setSelection(0);
				broker.send(EventConstants.FILE_IMPORT_FINISHED, true);
			}
		});
		System.out.println("finished");
	}

	@Override
	public void internalWorked(double work) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCanceled(boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTaskName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subTask(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void worked(final int work) {
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				bar.setSelection(bar.getSelection()+work);
			}
		});
		System.out.println("Worked ");
	}
}
