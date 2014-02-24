package org.jmedikit.plugin.gui;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.ADicomTreeItem;
import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.events.EventConstants;

/**
 * ImageLoader definiert einen Prozess zum Laden der Bilddaten
 * 
 * @author rkorb
 *
 */
public class ImageLoader implements IRunnableWithProgress{
	
	/**
	 * Geladener Bildstapel
	 */
	private ArrayList<AImage> images;
	
	/**
	 *  Ausgewähltes Element im Baum
	 */
	private ADicomTreeItem selection;
	
	/**
	 * Eventmanager zur Kommunikation der Parts
	 */
	private IEventBroker broker;
	
	/**
	 * Fortschrittsanzeige
	 */
	private IProgressMonitor monitor;
	
	/**
	 * 
	 * @param name Name
	 * @param selection Ausgewähltes Element des Baumes
	 * @param broker Eventmanager
	 */
	public ImageLoader(String name, ADicomTreeItem selection, IEventBroker broker) {
		//super(name);
		images = new ArrayList<AImage>();
		this.selection = selection;
		this.broker = broker;
	}

	/**
	 * Methode zum laden der Bilder. Abhängig vom Baumelement wird ein einzelnes Bild oder der ganze Bildstapel geladen.
	 */
	private void load(){
		DicomObject toDraw;
		//Bei direkter Objektauswahl wird nur das einzelne Bildgeladen
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
		//Auswahl einer Serie und der ganze Bildstapel wird aus dem Baumelement ausgelesen
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

				monitor.worked(1);
				monitor.subTask((i+1)+" Bilder von "+selection.size()+" geladen");
			}
		}
	}
	
	/**
	 * Stößt den Ladeprozess der Bilder an. Bilder werden nach dem Einlesen Räumlich sortiert. Nach erfolgreichem Laden wird das Event
	 * {@link EventConstants#IMAGES_LOADED} ausgelöst.
	 * 
	 * @param Fortschrittsanzeige
	 */
	@Override
	public void run(IProgressMonitor monitor) {
		//System.out.println("ImageLoading");
		this.monitor = monitor;
		this.monitor.beginTask("Lade Dicomdaten...", selection.size());
		//System.out.println("start");
		load();
		Collections.sort(images);
		this.monitor.done();
		broker.send(EventConstants.IMAGES_LOADED, images);
		//images.clear();
		//return Status.OK_STATUS;
	}
	
	/**
	 * 
	 * Kann nach einem erfolgreichen Ladeprozess aufgerufen werden, und gibt den geladenen Bildstapel zurück.
	 * 
	 * @return Bildstapel
	 */
	public ArrayList<AImage> getImages(){
		return images;
	}

}
