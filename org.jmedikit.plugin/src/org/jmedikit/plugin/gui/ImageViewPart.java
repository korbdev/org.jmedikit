package org.jmedikit.plugin.gui;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jmedikit.lib.core.ADicomTreeItem;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.events.AToolEvent;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.TransformationToolEvent;
import org.jmedikit.plugin.gui.tools.TransformationToolFactory;
import org.jmedikit.plugin.util.IObserver;


public class ImageViewPart {
	
	@Inject
	private IResourcePool resourcePool;
	
	@Inject
	private IEventBroker broker;
	
	//@Inject
	//private Shell shell;
	
	@Inject
	private static Shell staticShell;
	
	
	private Composite parent;
	
	private static ImageViewComposite active;
	
	private AToolEvent toolevent;
	
	//private ArrayList<AbstractImage> images;
	
	private ArrayList<ImageViewComposite> children;
	
	private ADicomTreeItem selection;
	
	public ImageViewPart(){
		
		children = new ArrayList<ImageViewComposite>();
		toolevent = new TransformationToolEvent(new TransformationToolFactory(), TransformationToolFactory.DEFAULT_TOOL);
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
		ImageViewPart.active = active;
	}
	
	public IEventBroker getEventBroker(){
		return broker;
	}
	
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.DICOMBROWSER_ITEM_SELECTION) final ADicomTreeItem selection){
		if(selection.getLevel() == ADicomTreeItem.TREE_SERIES_LEVEL || selection.getLevel() == ADicomTreeItem.TREE_OBJECT_LEVEL){
			Display.getCurrent().syncExec(new Runnable() {
				
				@Override
				public void run() {
					if(parent.getChildren().length < 4){
						ImageViewPart.this.selection = selection;
						try {
							new ProgressMonitorDialog(staticShell).run(true, false, new ImageLoader("ImageLoader", selection, broker));
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
	public void getNotifiedImagesLoaded(@UIEventTopic(EventConstants.IMAGES_LOADED) ArrayList<AImage> images){
		//System.out.println(images.size());
		//ArrayList<AbstractImage> loaded = new ArrayList<AbstractImage>(images);
		//images.clear();
		active = new ImageViewComposite(parent, SWT.NO_SCROLL|SWT.BORDER, selection.getUid(), selection, new ArrayList<AImage>(images), resourcePool, ImageViewPart.this);
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
		if(active != null){
			System.out.println("Changed");
			if(type.equals(EventConstants.ORIENTATION_CHANGED_AXIAL)){
				newIndex = active.getCanvas().getMaxAxialIndex();
				active.getCanvas().recalculateImages(AImage.AXIAL);
				active.setSliderMaximum(newIndex-1);
			}
			else if(type.equals(EventConstants.ORIENTATION_CHANGED_CORONAL)){
				newIndex = active.getCanvas().getMaxCoronalIndex();
				active.getCanvas().recalculateImages(AImage.CORONAL);
				active.setSliderMaximum(newIndex-1);
			}
			else if(type.equals(EventConstants.ORIENTATION_CHANGED_SAGITTAL)){
				newIndex = active.getCanvas().getMaxSagittalIndex();
				active.getCanvas().recalculateImages(AImage.SAGITTAL);
				active.setSliderMaximum(newIndex-1);
			}
			active.getCanvas().setMaxCurrentIndex(newIndex);
		}	
	}
	
	@Inject
	@Optional
	public void getNotifiedSelectionEvent(@UIEventTopic(EventConstants.SELECTION_ALL) String type){
		if(type.equals(EventConstants.SELECTION_REMOVE_ALL)){
			active.removeSelection();
		}
		else if(type.equals(EventConstants.SELECTION_REMOVE_SINGLE)){
			active.removeSingleSelection();
		}
	}
	
	@Inject
	@Optional
	public void getNotifiedPlugInEvent(@UIEventTopic(EventConstants.PLUG_IN_SELECTED) String mainClassName){
		System.out.println("EVENT "+mainClassName);
		active.getCanvas().runPlugIn(mainClassName);
	}
	
	//public Shell getShell(){
	//	return shell;
	//}
	
	public static Shell getPartShell(){
		return staticShell;
	}
	
	public static ImageViewComposite getActiveImageViewComposite(){
		return active;
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
			System.out.println(child.toString()+" "+imageViewComposite.toString());
			if(child.getTitle().equals(active.getTitle()) && child != imageViewComposite){
				child.removeObserver(imageViewComposite);
			}
		}
		if(imageViewComposite.equals(active)){
			//active = null;
		}
		children.remove(imageViewComposite);
	}
}
