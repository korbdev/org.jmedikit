package org.jmedikit.plugin.gui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.ADicomTreeItem;
import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.events.EventConstants;

public class ImageLoader implements IRunnableWithProgress{
	
	ArrayList<AImage> images;
	
	ADicomTreeItem selection;
	
	IEventBroker broker;
	
	IProgressMonitor monitor;
	
	public ImageLoader(String name, ADicomTreeItem selection, IEventBroker broker) {
		//super(name);
		images = new ArrayList<AImage>();
		this.selection = selection;
		this.broker = broker;
	}

	private void load(){
		//AImage img;
		System.out.println("Load Image");
		DicomObject toDraw;
		if(selection.getLevel() == ADicomTreeItem.TREE_OBJECT_LEVEL){
			toDraw = (DicomObject)selection;
			System.out.println(toDraw.getDepth());
			if(toDraw.getDepth() > 1){
				for(int depth = 0; depth < toDraw.getDepth(); depth++){
					AImage img = toDraw.getImage(depth);
					img.setTitle(toDraw.getUid()+"."+depth);
					images.add(depth, img);
				}
			}
			else{
				AImage img = toDraw.getImage(0);
				img.setTitle(toDraw.getUid());
				images.add(0, img);
			}
			monitor.worked(1);
		}
		else {
			for(int i = 0; i < selection.size(); i++){
				toDraw = (DicomObject) selection.getChild(i);
				if(toDraw.getDepth() > 1){
					for(int depth = 0; depth < toDraw.getDepth(); depth++){
						AImage img = toDraw.getImage(depth);
						img.setTitle(toDraw.getUid()+"."+depth);
						images.add(depth, img);
					}
				}
				else{
					AImage img = toDraw.getImage(0);
					img.setTitle(toDraw.getUid());
					images.add(i, img);
				}
				//System.out.println(toDraw.getDepth());
				//img = toDraw.getImage(0);
				//img.setTitle(toDraw.getUid());
				//images.add(i, img);
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
		//images.clear();
		//return Status.OK_STATUS;
	}
	
	public ArrayList<AImage> getImages(){
		return images;
	}

}
