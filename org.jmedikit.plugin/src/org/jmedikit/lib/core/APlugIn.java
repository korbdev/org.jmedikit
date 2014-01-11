package org.jmedikit.lib.core;

import org.jmedikit.lib.image.AImage;

public abstract class APlugIn {

	public static final int OPTION_PROCESS_ALL = 1;
	
	private AImage processingImage;
	
	private int options;
	
	protected abstract int options();
	
	public int initialize(){
		options = options();
		return options;
	}
	
	protected abstract AImage process(final AImage img);
	
	public AImage run(AImage img){
		processingImage = img;
		AImage result = process(processingImage);
		
		//Pruefung ob vom Benutzer Bildorientierung gesetzt wurde
		//wenn nicht, wird die Orientierung des Ursprungsbild auf das Ergebnisbild uebertragen
		if(!result.getInitializedOrientation()){
			result.copySignificantAttributes(img);
			result.setColumnImageOrientation(img.getColumnImageOrientation());
			result.setRowImageOrientation(img.getRowImageOrientation());
			result.setImagePosition(img.getImagePosition());
		}
		
		return result;
	}
	
	public int getOptions(){
		return options;
	}

}
