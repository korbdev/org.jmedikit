package org.jmedikit.plugin.gui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.plugin.gui.events.EventConstants;

import com.sun.xml.internal.fastinfoset.stax.events.EventBase;

public class ImageLoader implements IRunnableWithProgress{
	
	ArrayList<AbstractImage> images;
	
	DicomTreeItem selection;
	
	IEventBroker broker;
	
	IProgressMonitor monitor;
	
	public ImageLoader(String name, DicomTreeItem selection, IEventBroker broker) {
		//super(name);
		images = new ArrayList<AbstractImage>();
		this.selection = selection;
		this.broker = broker;
	}

	private void load(){
		AbstractImage img;
		System.out.println("Load Image");
		DicomObject toDraw;
		if(selection.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
			toDraw = (DicomObject)selection;
			img = toDraw.getImage(0);
			images.add(0, img);
			monitor.worked(1);
		}
		else {
			for(int i = 0; i < selection.size(); i++){
				toDraw = (DicomObject) selection.getChild(i);
				img = toDraw.getImage(0);
				images.add(i, img);
				monitor.worked(1);
				monitor.subTask((i+1)+" Bilder von "+selection.size()+" geladen");
			}
		}
	}
	
	@Override
	public void run(IProgressMonitor monitor) {
		System.out.println("ImageLoading");
		this.monitor = monitor;
		this.monitor.beginTask("Lade Dicomdaten...", selection.size());
		System.out.println("start");
		load();
		this.monitor.done();
		broker.send(EventConstants.IMAGES_LOADED, images);
		//return Status.OK_STATUS;
	}
	
	public ArrayList<AbstractImage> getImages(){
		return images;
	}

}