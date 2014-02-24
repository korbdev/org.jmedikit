package org.jmedikit.lib.core;

import java.util.ArrayList;
import java.util.List;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.util.SimpleImageFactory;

/**
 * 
 * Diese Klasse ist der Plug-in-Typ für zweidimensionale Algorithmen.
 * 
 * @author rkorb
 *
 */
public abstract class APlugIn2D extends APlugIn{

	/**
	 * 
	 * Die Methode process stellt den Startpunkt eines Plug-ins dar. Ein zweidimensionales Plug-in enthält als Parameter das Bild der aktuell
	 * im aktiven {@link DicomCanvas} angezeigten Schicht.
	 * 
	 * @param img aktuelles Bild des {@link DicomCanvas}
	 * @return
	 */
	public abstract AImage process(AImage img);
	

	@Override
	public int getPlugInType() {
		return APlugIn.PLUGIN_2D;
	}

	@Override
	public List<AImage> run(List<AImage> images, int index) throws IllegalAccessException {
		
		AImage sample = SimpleImageFactory.getAbstractImage(images.get(index).getImageType(), images.get(index).getWidth(), images.get(index).getHeight());
		copyValues(sample, images.get(index));
		
		//int x = sample.getWidth();
		//int y = sample.getHeight();
		
		AImage processedSingle = process(images.get(index));
		
		//wenn signifikaten Werte noch nicht gesetzt werden, dann muss es hier geschehen
		copyValues(processedSingle, sample);
		
		//deprecated: zu Beginn wurde auf gleiche Bilddimension geprüft
		/*if(x == processedSingle.getWidth() && y == processedSingle.getHeight()){
			copyValues(processedSingle, sample);
		}
		else{
			throw new IllegalAccessException("IMAGE DIMENSION DOES NOT MATCH SOURCE DIMENSION");
		}*/
		
		List<AImage> processed = new ArrayList<AImage>();
		processed.add(processedSingle);
		return processed;
	}

}
