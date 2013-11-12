package org.jmedikit.plugin.gui;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.plugin.gui.events.AToolEvent;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.TransformationToolEvent;
import org.jmedikit.plugin.tools.TransformationToolFactory;


public class ImageViewPart {
	
	@Inject
	private IResourcePool resourcePool;
	
	@Inject
	private IEventBroker broker;
	
	private Composite parent;
	
	private ImageViewComposite active;
	
	private AToolEvent toolevent;
	
	public ImageViewPart(){
		toolevent = new TransformationToolEvent(new TransformationToolFactory(), TransformationToolFactory.MOVE_TOOL);
		System.out.println("Construktor ImageViewPart ");
	}
	
	@PostConstruct
	public void createGUI(final Composite parent){
		this.parent = parent;
		this.parent.setLayout(new GridLayout(2, false));
	}
	
	public Image getImageFromResourcePool(String image){
		return resourcePool.getImageUnchecked(image);
	}
	
	public void setActive(ImageViewComposite active){
		System.out.println(active.getTitle());
		this.active = active;
	}
	
	public IEventBroker getEventBroker(){
		return broker;
	}
	
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.DICOMBROWSER_ITEM_SELECTION) final DicomTreeItem selection){
		if(selection.getLevel() == DicomTreeItem.TREE_SERIES_LEVEL || selection.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
			Display.getCurrent().syncExec(new Runnable() {
				
				@Override
				public void run() {
					if(parent.getChildren().length < 4){
						active = new ImageViewComposite(parent, SWT.NO_SCROLL|SWT.BORDER, selection.getUid(), selection, resourcePool, ImageViewPart.this);
						active.setTool(toolevent.getFactory(), toolevent.getTool());
						parent.layout();
					}
				}
			});	
		}
	}
	
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.TOOL_CHANGED_ALL) AToolEvent event){
		
		toolevent = event;
		for(Control c : parent.getChildren()){
			if(c instanceof ImageViewComposite){
				System.out.println("Change tool to "+event.getTool());
				((ImageViewComposite) c).setTool(event.getFactory(), event.getTool());
			}
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
