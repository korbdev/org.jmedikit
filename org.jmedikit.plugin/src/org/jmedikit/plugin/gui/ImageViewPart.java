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
import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.events.AToolEvent;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.events.TransformationToolEvent;
import org.jmedikit.plugin.gui.tools.DefaultTool;
import org.jmedikit.plugin.gui.tools.TransformationToolFactory;
import org.jmedikit.plugin.util.IObserver;

/**
 * Diese Klasse bildet die Grundlage der Bildanzeige und enth�lt mehrere Bedienelemente der Klasse {@link ImageViewComposite} zur Bildverwaltung, Orientierung und Navigation in medizinischen Bilddaten.
 * 
 * @author rkorb
 *
 */
public class ImageViewPart {
	
	/**
	 * Bestimmt die maximale Anzahl an gleichzeitig ge�ffneten Serien. Ein {@link ImageViewComposite} entspricht einer ge�ffneten Serie bzw. einem Bildstapel.
	 */
	public static final int MAX_CHILD_COMPOSITES = 4;
	
	/**
	 * Iconverwaltung
	 */
	@Inject
	private IResourcePool resourcePool;
	
	/**
	 * Eventmanager
	 */
	@Inject
	private IEventBroker broker;
	
	//@Inject
	//private Shell shell;
	
	/**
	 * Fenster in dem der ImageViewPart ausgef�hrt wird
	 */
	@Inject
	private static Shell staticShell;
	
	/**
	 * Das Elternelement der Benutzeroberfl�che
	 */
	private Composite parent;
	
	/**
	 * Der aktive Bildstapel, wird bei der Ausf�hrung von Plug-ins angesprochen
	 */
	private static ImageViewComposite active;
	
	/**
	 * Toolevent aus der Werkzeugleiste
	 */
	private AToolEvent toolevent;
	
	//private ArrayList<AbstractImage> images;
	
	/**
	 * Liste der ge�ffneten Bildstapel
	 */
	private ArrayList<ImageViewComposite> children;
	
	/**
	 * Zuletzt ausgew�hltes Element des DICOM-Baumes
	 */
	private ADicomTreeItem selection;
	
	/**
	 * Erzeugt eine Liste der Kindelemente von {@link ImageViewComposite} und das Standardwerkzeug {@link DefaultTool}.
	 */
	public ImageViewPart(){
		children = new ArrayList<ImageViewComposite>();
		toolevent = new TransformationToolEvent(new TransformationToolFactory(), TransformationToolFactory.DEFAULT_TOOL);
	}
	
	/**
	 * Erzeugt die Benutzeroberfl�che
	 * @param parent
	 */
	@PostConstruct
	public void createGUI(final Composite parent){
		this.parent = parent;
		this.parent.setLayout(new GridLayout(2, false));
	}
	
	/**
	 * Holt ein Icon aus der Iconverwaltung. Die verf�gbaren Icon, die erzeugt werden k�nnen sind als Konstanten in der Klasse {@link ImageProvider} hinterlegt.
	 * @param image Name des Icons
	 * @return
	 */
	public Image getImageFromResourcePool(String image){
		return resourcePool.getImageUnchecked(image);
	}
	
	/**
	 * Setzt das {@link ImageViewComposite} aus dem Parameter als aktives Element
	 * @param active
	 */
	public void setActive(ImageViewComposite active){
		System.out.println(active.getTitle());
		ImageViewPart.active = active;
	}
	
	/**
	 * Gibt den Eventmanager zur�ck
	 * @return
	 */
	public IEventBroker getEventBroker(){
		return broker;
	}
	
	/**
	 * Diese Methode wird ausgef�hrt, wenn das Event {@link EventConstants#DICOMBROWSER_ITEM_SELECTION} eintritt. L�sst nach M�glichkeit eine neue Zeichenfl�che erzeugen. 
	 * H�ngt sowohl von der Konstante {@link ImageViewPart#MAX_CHILD_COMPOSITES}, als auch der Tiefe der Auswahl ab. 
	 * Reagiert auf die Auswahl einer Serie oder eines DICOM-Objekts aus dem Baum.  Ist das gew�hlte Baumelement von der Tiefe Serie oder Objekt, wird der Bildladeprozess
	 * angestossen.
	 * 
	 * @param selection Auswahl des DICOM-Baumes
	 */
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.DICOMBROWSER_ITEM_SELECTION) final ADicomTreeItem selection){
		if(selection.getLevel() == ADicomTreeItem.TREE_SERIES_LEVEL || selection.getLevel() == ADicomTreeItem.TREE_OBJECT_LEVEL){
			Display.getCurrent().syncExec(new Runnable() {
				
				@Override
				public void run() {
					if(parent.getChildren().length < MAX_CHILD_COMPOSITES){
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
	
	/**
	 * Diese Methode wird ausgef�hrt, wenn das Event {@link EventConstants#IMAGES_LOADED} eintritt. Es wird ein neues Kindelement von {@link ImageViewComposite} erzeugt und als
	 * aktives Element markiert. Gleiche Datens�tze werden als Beobachter registiert und das Subjekt registriert sich selbst als Beobachter dieser Datens�tze.
	 * @param images
	 */
	@Inject
	@Optional
	public void getNotifiedImagesLoaded(@UIEventTopic(EventConstants.IMAGES_LOADED) ArrayList<AImage> images){
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
	
	/**
	 * Diese Methode wird ausgef�hrt, wenn das Gruppen-Event {@link EventConstants#ORIENTATION_CHANGED_ALL} eintritt. Darauf werden die Werkzeuge
	 * entsprechend der Auswahl des Nuzters in allen Kindelementen des Typs {@link ImageViewComposite} aktualisiert.
	 * @param event WerkzeugEvent, enth�lt Erzeugerfabrik und Werkzeugtyp
	 */
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
	
	//Relikt aus dem MPR-Versuch
	/*@Inject
	@Optional
	public void getNotifiedAngleChanged(@UIEventTopic(EventConstants.ANGLE_CHANGED_ALL) int[] angle){
		//active.getCanvas().setAngles(angle[0], angle[1], angle[2]);
		//int index = active.getCanvas().recalculateImages(angle[0], angle[1], angle[2]);
		System.out.println("IP");
		//active.setSliderMaximum(index);
	}*/
	
	/**
	 * Diese Methode wird ausgef�hrt, wenn das Gruppen-Event {@link EventConstants#ORIENTATION_CHANGED_ALL} eintritt.
	 * St��t die Ebenenrekonstruktion des Bildstapels an.
	 *
	 * @param type Ebenenrekonstruktionstyp
	 */
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
	
	/**
	 * Diese Methode wird ausgef�hrt, wenn das Gruppen-Event {@link EventConstants#SELECTION_ALL} eintritt. Je nach Gruppenelement wird entweder
	 * die Auswahl aller Bildschichten oder der aktuellen Bildschicht gel�scht.
	 * @param type
	 */
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
	
	/**
	 * Diese Methode wird ausgef�hrt, wenn das Event {@link EventConstants#PLUG_IN_SELECTED} eintritt. Die passiert zum Beispiel dann, wenn
	 * der Anwender ein Plug-in aus der Liste w�hlt. Plug-in-Ausf�hrung wird angestossen
	 * 
	 * @param mainClassName Name der Hauptklasse des Plug-ins
	 */
	@Inject
	@Optional
	public void getNotifiedPlugInEvent(@UIEventTopic(EventConstants.PLUG_IN_SELECTED) String mainClassName){
		System.out.println("EVENT "+mainClassName);
		active.getCanvas().runPlugIn(mainClassName);
	}
	
	//public Shell getShell(){
	//	return shell;
	//}
	
	/**
	 * Gibt das Fenser des Elternelement zur�ck. Wird ben�tigt, wenn Kindelemente eigene Fenster erzeugen wollen. Zum Beispiel die Vollbildansicht
	 * im {@link ImageViewComposite}
	 * 
	 * @return Shell des Elternelements
	 */
	public static Shell getPartShell(){
		return staticShell;
	}
	
	/**
	 * Gibt das derzeit aktive Element der {@link ImageViewComposite}s zur�ck
	 * 
	 * @return
	 */
	public static ImageViewComposite getActiveImageViewComposite(){
		return active;
	}

	/**
	 * L�scht das in den Parameter �bergebene {@link ImageViewComposite}-Kindelement aus der Liste. Das Element wird ebenfalls als Beobachter aus 
	 * den betreffenden Subjekten entfernt.
	 * 
	 * @param imageViewComposite
	 */
	public void deleteChild(ImageViewComposite imageViewComposite) {
		for(ImageViewComposite child : children){
			//System.out.println(child.toString()+" "+imageViewComposite.toString());
			if(child.getTitle().equals(active.getTitle()) && child != imageViewComposite){
				child.removeObserver(imageViewComposite);
			}
		}
		//if(imageViewComposite.equals(active)){
			//active = null;
		//}
		children.remove(imageViewComposite);
	}
}
