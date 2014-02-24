package org.jmedikit.lib.core;

import java.util.List;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.util.SimpleImageFactory;

public abstract class APlugIn3D extends APlugIn{
	
	/**
	 * Startmethode für dreidimensionale Plug-in-Typen. Als Argumente sind der gesamte Bildstapel und der Index der aktuell im aktiven 
	 * {@link DicomCanvas} angezeigten Bildschicht.
	 * @param images
	 * @param index
	 * @return Liste der vom Plug-in bearbeiteten Bilder. Ergebnisliste muss die gleiche Länge vom Parameter images haben
	 */
	public abstract List<AImage> process(List<AImage> images, int index);
	
	@Override 
	public int getPlugInType(){
		return APlugIn.PLUGIN_3D;
	}
	
	@Override
	public List<AImage> run(List<AImage> images, int index) throws IllegalAccessException {

		AImage sample = SimpleImageFactory.getAbstractImage(images.get(index).getImageType(), images.get(index).getWidth(), images.get(index).getHeight());
		copyValues(sample, images.get(index));
		//int x = sample.getWidth();
		//int y = sample.getHeight();
		int z = images.size();
		
		List<AImage> processed = process(images, index);
		
		if(processed.size() != z){
			throw new IllegalAccessException("RESULT SIZE DOES NOT MATCH CLAIM SIZE");
		}
		else{
			for(int i = 0; i < processed.size(); i++){
				AImage copy = processed.get(i);
				copyValues(copy, sample);
				/*if(x == copy.getWidth() && y == copy.getHeight()){
					copyValues(copy, sample);
				}
				else{
					throw new IllegalAccessException("IMAGE DIMENSION DOES NOT MATCH SOURCE DIMENSION");
				}*/
			}
		}
		
		return processed;
	}

}
