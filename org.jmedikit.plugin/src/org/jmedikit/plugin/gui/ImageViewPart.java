package org.jmedikit.plugin.gui;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.util.IObserver;
import org.jmedikit.plugin.gui.events.AToolEvent;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.TransformationToolEvent;
import org.jmedikit.plugin.gui.tools.TransformationToolFactory;


public class ImageViewPart {
	
	@Inject
	private IResourcePool resourcePool;
	
	@Inject
	private IEventBroker broker;
	
	@Inject
	private Shell shell;
	
	@Inject
	//private UISynchronize sync;
	
	private Composite parent;
	
	private ImageViewComposite active;
	
	private AToolEvent toolevent;
	
	//private ArrayList<AbstractImage> images;
	
	private ArrayList<ImageViewComposite> children;
	
	private DicomTreeItem selection;
	
	public ImageViewPart(){
		children = new ArrayList<ImageViewComposite>();
		System.out.println("CONSTRUCT IVP");
		toolevent = new TransformationToolEvent(new TransformationToolFactory(), TransformationToolFactory.MOVE_TOOL);
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
						ImageViewPart.this.selection = selection;
						try {
							new ProgressMonitorDialog(shell).run(true, false, new ImageLoader("ImageLoader", selection, broker));
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});	
		}
	}
	
	@Inject
	@Optional
	public void getNotifiedImagesLoaded(@UIEventTopic(EventConstants.IMAGES_LOADED) ArrayList<AbstractImage> images){
		//System.out.println(images.size());
		//ArrayList<AbstractImage> loaded = new ArrayList<AbstractImage>(images);
		//images.clear();
		active = new ImageViewComposite(parent, SWT.NO_SCROLL|SWT.BORDER, selection.getUid(), selection, new ArrayList<AbstractImage>(images), resourcePool, ImageViewPart.this);
		active.getCanvas().setFocus();
		active.setTool(toolevent.getFactory(), toolevent.getTool());
		images.clear();
		for(ImageViewComposite child : children){
			if(child.getTitle().equals(active.getTitle())){
				child.registerObserver((IObserver)active);
				active.registerObserver(child);
			}
		}
		children.add(active);
		parent.layout();
	}
	
	@Inject
	@Optional
	public void getNotifiedToolSelection(@UIEventTopic(EventConstants.TOOL_CHANGED_ALL) AToolEvent event){
		
		toolevent = event;
		System.out.println(event.getTool());
		for(Control c : parent.getChildren()){
			if(c instanceof ImageViewComposite){
				((ImageViewComposite) c).setTool(event.getFactory(), event.getTool());
			}
		}
	}
	
	@Inject
	@Optional
	public void getNotifiedAngleChanged(@UIEventTopic(EventConstants.ANGLE_CHANGED_ALL) int[] angle){
		//active.getCanvas().setAngles(angle[0], angle[1], angle[2]);
		//int index = active.getCanvas().recalculateImages(angle[0], angle[1], angle[2]);
		System.out.println("IP");
		//active.setSliderMaximum(index);
	}
	
	@Inject
	@Optional
	public void getNotifiedOrientationChanged(@UIEventTopic(EventConstants.ORIENTATION_CHANGED_ALL) String type){
		int newIndex = 1;
		if(type.equals(EventConstants.ORIENTATION_CHANGED_AXIAL)){
			newIndex = active.getCanvas().getMaxAxialIndex();
			active.getCanvas().recalculateImages(AbstractImage.AXIAL);
			active.setSliderMaximum(newIndex-1);
		}
		else if(type.equals(EventConstants.ORIENTATION_CHANGED_CORONAL)){
			newIndex = active.getCanvas().getMaxCoronalIndex();
			active.getCanvas().recalculateImages(AbstractImage.CORONAL);
			active.setSliderMaximum(newIndex-1);
		}
		else if(type.equals(EventConstants.ORIENTATION_CHANGED_SAGITTAL)){
			newIndex = active.getCanvas().getMaxSagittalIndex();
			active.getCanvas().recalculateImages(AbstractImage.SAGITTAL);
			active.setSliderMaximum(newIndex-1);
		}
		active.getCanvas().setMaxCurrentIndex(newIndex);
	}
	
	public Shell getShell(){
		return shell;
	}
	/** CLASSLOADER TEST CODE
		Class klass = RawImageInputStream.class;

	    CodeSource codeSource = klass.getProtectionDomain().getCodeSource();

	    if ( codeSource != null) {

	        System.out.println(codeSource.getLocation());

	    }else System.out.println("LOADED");
	 */

	public void deleteChild(ImageViewComposite imageViewComposite) {
		for(ImageViewComposite child : children){
			if(child.getTitle().equals(active.getTitle()) && child != imageViewComposite){
				child.removeObserver(imageViewComposite);
			}
		}
		children.remove(imageViewComposite);
	}
}
