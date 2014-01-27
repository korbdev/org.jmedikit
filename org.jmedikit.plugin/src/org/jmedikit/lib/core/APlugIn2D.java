package org.jmedikit.lib.core;

import java.util.ArrayList;
import java.util.List;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.util.SimpleImageFactory;

public abstract class APlugIn2D extends APlugIn{

	public abstract AImage process(AImage img);
	
	@Override
	public int getPlugInType() {
		return APlugIn.PLUGIN_2D;
	}

	@Override
	public List<AImage> run(List<AImage> images, int index) throws IllegalAccessException {
		
		AImage sample = SimpleImageFactory.getAbstractImage(images.get(index).getImageType(), images.get(index).getWidth(), images.get(index).getHeight());
		copyValues(sample, images.get(index));
		
		int x = sample.getWidth();
		int y = sample.getHeight();
		
		AImage processedSingle = process(images.get(index));
		
		System.out.println("X/Y "+x+" x "+y);
		System.out.println("W/H "+processedSingle.getWidth()+" x "+processedSingle.getHeight());
		
		if(x == processedSingle.getWidth() && y == processedSingle.getHeight()){
			copyValues(processedSingle, sample);
		}
		else{
			throw new IllegalAccessException("IMAGE DIMENSION DOES NOT MATCH SOURCE DIMENSION");
		}
		
		List<AImage> processed = new ArrayList<AImage>();
		processed.add(processedSingle);
		return processed;
	}

}
