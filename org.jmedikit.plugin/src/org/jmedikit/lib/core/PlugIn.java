package org.jmedikit.lib.core;

import org.jmedikit.lib.image.AImage;

public abstract class PlugIn {

	private AImage processingImage;
	
	protected abstract AImage process(final AImage img);
	
	public AImage run(AImage img){
		processingImage = img;
		AImage result = process(processingImage);

		//Pruefung ob vom Benutzer Bildorientierung gesetzt wurde
		//wenn nicht, wird die Orientierung des Ursprungsbild auf das Ergebnisbild uebertragen
		if(!result.getInitializedOrientation()){
			result.copySignificantAttributes(processingImage);
		}
		
		return result;
	}
}