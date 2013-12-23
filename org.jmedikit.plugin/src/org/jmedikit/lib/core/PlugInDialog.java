package org.jmedikit.lib.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jmedikit.Activator;
import org.jmedikit.plugin.gui.ImageViewPart;

public class PlugInDialog extends Dialog{

	private Composite container;
	
	Map<String, Integer> intValues;
	
	public PlugInDialog() {
		//super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		super(ImageViewPart.getPartShell());
		intValues = new HashMap<String, Integer>();
	}
	

	@Override
	  public Control createDialogArea(Composite parent) {
	    container = (Composite) super.createDialogArea(parent);
	    for(String s : intValues.keySet()){
	    	Text text = new Text(container, SWT.NONE);
	    	text.setText(s);
	    }
	    /*Button button = new Button(container, SWT.PUSH);
	    button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
	        false));
	    button.setText("Press me");
	    button.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(SelectionEvent e) {
	        System.out.println("Pressed");
	      }
	    });*/

	    return container;
	  }

	  // overriding this methods allows you to set the
	  // title of the custom dialog
	  /*@Override
	  public void configureShell(Shell newShell) {
	    super.configureShell(newShell);
	    newShell.setText("Selection dialog");
	  }*/
	  

	  public void addInt(String name, int defaultValue){
		  intValues.put(name, defaultValue);
	  }
	  
	  @Override
	  public int open(){
		  return super.open();
	  }
}