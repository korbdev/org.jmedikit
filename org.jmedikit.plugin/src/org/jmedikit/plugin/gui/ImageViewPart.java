package org.jmedikit.plugin.gui;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.plugin.gui.events.EventConstants;

public class ImageViewPart {
	
	@Inject
	private IResourcePool resourcePool;
	
	private Composite parent;
	
	@PostConstruct
	public void createGUI(final Composite parent){
		this.parent = parent;
		this.parent.setLayout(new GridLayout(2, false));
	}
	
	public Image getImageFromResourcePool(String image){
		return resourcePool.getImageUnchecked(image);
	}
	
	//Labels cant get Focus
	@Focus
	public void setFocus(){
		//canvas.setFocus();
	}
	
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.DICOMBROWSER_ITEM_SELECTION) final DicomTreeItem selection){
		if(selection.getLevel() == DicomTreeItem.TREE_SERIES_LEVEL || selection.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
			Display.getCurrent().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if(parent.getChildren().length < 4){
						ImageViewComposite single = new ImageViewComposite(parent, SWT.NO_SCROLL, selection.getUid(), selection, resourcePool);
						parent.layout();
					}
				}
			});	
		}
	}
	
	/** CLASSLOADER TEST CODE
		Class klass = RawImageInputStream.class;

	    CodeSource codeSource = klass.getProtectionDomain().getCodeSource();

	    if ( codeSource != null) {

	        System.out.println(codeSource.getLocation());

	    }else System.out.println("LOADED");
	 */
}
