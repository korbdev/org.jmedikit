/**
 * Diese Klasse ist aktuell nicht im Einsatz
 */

/*package org.jmedikit.lib.image;

import java.util.ArrayList;

import org.jmedikit.lib.core.BilinearInterpolation;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.ADicomTreeItem;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.lib.util.SimpleImageFactory;
import org.jmedikit.lib.util.Vector3D;
import org.jmedikit.plugin.gui.DicomCanvas;

public class MultiplanarReconstruction {
	
	private ArrayList<AImage> images;
	
	private AImage sampleImage;
	
	private Vector3D<Float> firstSlice;
	private Vector3D<Float> nthSlice;
	
	
	private DicomCanvas canvas;
	
	private float translation;
	
	public MultiplanarReconstruction(DicomCanvas canvas){
		this.canvas = canvas;
		firstSlice = canvas.loadImage(0).getImagePosition();
		nthSlice = canvas.loadImage(canvas.getItem().size()-1).getImagePosition();
	}
	
	public AImage calculateAxialView(int index){
		AImage img = canvas.getSampleImage();
		if(img.mprType.equals(AImage.SAGITTAL)){
			return sagittalToAxial(index, img);
		}
		else if(img.mprType.equals(AImage.CORONAL)){
			return coronalToAxial(index, img);
		}
		else return img;
	}
	
	public AImage calculateCoronalView(int index){
		AImage img = canvas.getSampleImage();
		System.out.println("IMG MPR "+img.mprType);
		if(img.mprType.equals(AImage.AXIAL)){
			return axialToCoronal(index, img);
		}
		else if(img.mprType.equals(AImage.SAGITTAL)){
			return sagittalToCoronal(index, img);
		}
		else return img;
	}
	
	public AImage calculateSagittalView(int index){
		AImage img = canvas.getSampleImage();
		
		if(img.mprType.equals(AImage.AXIAL)){
			return axialToSagittal(index, img);
		}
		else if(img.mprType.equals(AImage.CORONAL)){
			return coronalToSagittal(index, img);
		}
		else return img;
	}
	
	public boolean needsSignChange(Vector3D<Float> firstStackSlice, Vector3D<Float> nthStackSlice, Vector3D<Float> normale){
		Vector3D<Float> grothDirection = new Vector3D<Float>(0f, 0f, 0f, 1f);
		
		//Es erfolgt eine Pruefung, ob der Bildstapel in Richtung der Normalen ansteigt
		int dominantNormalIndex = getDominantIndex(normale);
		if(firstStackSlice.get(dominantNormalIndex) < nthStackSlice.get(dominantNormalIndex)){
			grothDirection.set(dominantNormalIndex, 1f);
		}
		else grothDirection.set(dominantNormalIndex, -1f);
		
		if((grothDirection.get(dominantNormalIndex) < 0 && normale.get(dominantNormalIndex) > 0) || (grothDirection.get(dominantNormalIndex) > 0 && normale.get(dominantNormalIndex) < 0)){
			//Vorzeichenwechsel auf dominanter Achse, da Normale und Stapel und verschiedene Richtung wachsen
			return true;
		}
		else return false;
	}
	
	public AImage axialToCoronal(int index, AImage img){
		
		Vector3D<Float> rotation = new Vector3D<Float>(90f, 0f, 0f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		//Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		//Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
		System.out.println(firstSlice.toString()+",..."+nthSlice.toString()+",...Normal "+N.toString());
		if(needsSignChange(firstSlice, nthSlice, N)){
			System.out.println("Vorzeichenwechsel");
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateX(deg2rad(rotation.x), R);
		Vector3D<Float> C_rotated = Vector3D.rotateX(deg2rad(rotation.x), C);
		
		//Es folgt eine Normalisierung auf die Länge 1
		Vector3D<Float> rotated_normal = Vector3D.crossProduct(R_rotated, C_rotated);
		rotated_normal.setVector(
				rotated_normal.x/Vector3D.length(rotated_normal), 
				rotated_normal.y/Vector3D.length(rotated_normal), 
				rotated_normal.z/Vector3D.length(rotated_normal)
		);
		
		R_rotated.setVector(
				R_rotated.x/Vector3D.length(R_rotated),	 //x-Anteil 
				R_rotated.y/Vector3D.length(R_rotated),  //y-Anteil
				R_rotated.z/Vector3D.length(R_rotated)   //z-Anteil
		); 
		
		C_rotated.setVector(
				C_rotated.x/Vector3D.length(C_rotated),	 //x-Anteil 
				C_rotated.y/Vector3D.length(C_rotated),  //y-Anteil
				C_rotated.z/Vector3D.length(C_rotated)   //z-Anteil
		);
		System.out.println("DOWN   "+R_rotated.toString()+", "+C_rotated.toString());
		
		//System.out.println("GROTH "+grothDirection.toString()+" Rota "+rotation.toString()+" RotaNorm "+rotated_normal.toString());
		
		int xLength = img.width;
		int yLength = canvas.getItem().size();
		int zLength = img.height;
		System.out.println("YLEN "+yLength);
		AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
		
		//for(int z = 0; z < zLength; z++){
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, (((DicomObject) canvas.getItem().getChild(y)).getSimplePixel(x, index, 0)));
					}
				}
				
				//BilinearInterpolation interpolation = new BilinearInterpolation(result);
				//result = interpolation.resample(result.width, result.height, img.width, img.height);
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
			//}
			return result;
	}
	
	public AImage axialToSagittal(int index, AImage img){
		
		Vector3D<Float> rotation = new Vector3D<Float>(90f, 90f, 0f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		//Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		//Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();

		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateY(rotation.y, R));
		Vector3D<Float> C_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateY(rotation.y, C));
		
		//Es folgt eine Normalisierung auf die Länge 1
		Vector3D<Float> rotated_normal = Vector3D.crossProduct(R_rotated, C_rotated);
		rotated_normal.setVector(
				rotated_normal.x/Vector3D.length(rotated_normal), 
				rotated_normal.y/Vector3D.length(rotated_normal), 
				rotated_normal.z/Vector3D.length(rotated_normal)
		);
		
		R_rotated.setVector(
				R_rotated.x/Vector3D.length(R_rotated),	 //x-Anteil 
				R_rotated.y/Vector3D.length(R_rotated),  //y-Anteil
				R_rotated.z/Vector3D.length(R_rotated)   //z-Anteil
		); 
		
		C_rotated.setVector(
				C_rotated.x/Vector3D.length(C_rotated),	 //x-Anteil 
				C_rotated.y/Vector3D.length(C_rotated),  //y-Anteil
				C_rotated.z/Vector3D.length(C_rotated)   //z-Anteil
		);
		
		int xLength = img.height;
		int yLength = canvas.getItem().size();
		int zLength = img.width;
		
		AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
		
		//for(int z = 0; z < zLength; z++){
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						//result.setPixel(x, y, canvas.loadImage(y).getPixel(index, x));
						result.setPixel(x, y, (((DicomObject) canvas.getItem().getChild(y)).getSimplePixel(index, x, 0)));
					}
				}
				
				//BilinearInterpolation interpolation = new BilinearInterpolation(result);
				//result = interpolation.resample(result.width, result.height, img.width, img.height);
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
			//}
			return result;
	}
	
	public AImage coronalToAxial(int index, AImage img){
		
		Vector3D<Float> rotation = new Vector3D<Float>(90f, 0f, 0f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		//Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		//Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();

		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateX(deg2rad(rotation.x), R);
		Vector3D<Float> C_rotated = Vector3D.rotateX(deg2rad(rotation.x), C);
		
		//Es folgt eine Normalisierung auf die Länge 1
		Vector3D<Float> rotated_normal = Vector3D.crossProduct(R_rotated, C_rotated);
		rotated_normal.setVector(
				rotated_normal.x/Vector3D.length(rotated_normal), 
				rotated_normal.y/Vector3D.length(rotated_normal), 
				rotated_normal.z/Vector3D.length(rotated_normal)
		);
		
		R_rotated.setVector(
				R_rotated.x/Vector3D.length(R_rotated),	 //x-Anteil 
				R_rotated.y/Vector3D.length(R_rotated),  //y-Anteil
				R_rotated.z/Vector3D.length(R_rotated)   //z-Anteil
		); 
		
		C_rotated.setVector(
				C_rotated.x/Vector3D.length(C_rotated),	 //x-Anteil 
				C_rotated.y/Vector3D.length(C_rotated),  //y-Anteil
				C_rotated.z/Vector3D.length(C_rotated)   //z-Anteil
		);
		System.out.println("DOWN   "+R_rotated.toString()+", "+C_rotated.toString());
		
		//System.out.println("GROTH "+grothDirection.toString()+" Rota "+rotation.toString()+" RotaNorm "+rotated_normal.toString());
		
		int xLength = img.width;
		int yLength = canvas.getItem().size();
		int zLength = img.height;
		
		AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
		
		//for(int z = 0; z < zLength; z++){
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						//result.setPixel(x, y, canvas.loadImage(y).getPixel(x, index));
						result.setPixel(x, y, (((DicomObject) canvas.getItem().getChild(y)).getSimplePixel(x, index, 0)));
					}
				}
				
				//BilinearInterpolation interpolation = new BilinearInterpolation(result);
				//result = interpolation.resample(result.width, result.height, img.width, img.height);
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
			//}
			return result;
	}
	
	public AImage coronalToSagittal(int index, AImage img){

		Vector3D<Float> rotation = new Vector3D<Float>(0f, 0f,90f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		//Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		//Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
	
		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateZ(rotation.z, R);
		Vector3D<Float> C_rotated = Vector3D.rotateZ(rotation.z, C);
		
		//Es folgt eine Normalisierung auf die Länge 1
		Vector3D<Float> rotated_normal = Vector3D.crossProduct(R_rotated, C_rotated);
		rotated_normal.setVector(
				rotated_normal.x/Vector3D.length(rotated_normal), 
				rotated_normal.y/Vector3D.length(rotated_normal), 
				rotated_normal.z/Vector3D.length(rotated_normal)
		);
		
		R_rotated.setVector(
				R_rotated.x/Vector3D.length(R_rotated),	 //x-Anteil 
				R_rotated.y/Vector3D.length(R_rotated),  //y-Anteil
				R_rotated.z/Vector3D.length(R_rotated)   //z-Anteil
		); 
		
		C_rotated.setVector(
				C_rotated.x/Vector3D.length(C_rotated),	 //x-Anteil 
				C_rotated.y/Vector3D.length(C_rotated),  //y-Anteil
				C_rotated.z/Vector3D.length(C_rotated)   //z-Anteil
		);
		
		int xLength = canvas.getItem().size();
		int yLength = img.height;
		int zLength = img.width;
		
		AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
		
		//for(int z = 0; z < zLength; z++){
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, canvas.loadImage(x).getPixel(index, y));
						result.setPixel(x, y, (((DicomObject) canvas.getItem().getChild(x)).getSimplePixel(index, y, 0)));
					}
				}
				
				//BilinearInterpolation interpolation = new BilinearInterpolation(result);
				//result = interpolation.resample(result.width, result.height, img.width, img.height);
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
			//}
			return result;
	}

	public AImage sagittalToAxial(int index, AImage img){

		Vector3D<Float> rotation = new Vector3D<Float>(90f, 0f, -90f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		//Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		//Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();

		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateZ(rotation.z, R));
		Vector3D<Float> C_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateZ(rotation.z, C));
		
		//Es folgt eine Normalisierung auf die Länge 1
		Vector3D<Float> rotated_normal = Vector3D.crossProduct(R_rotated, C_rotated);
		rotated_normal.setVector(
				rotated_normal.x/Vector3D.length(rotated_normal), 
				rotated_normal.y/Vector3D.length(rotated_normal), 
				rotated_normal.z/Vector3D.length(rotated_normal)
		);
		
		R_rotated.setVector(
				R_rotated.x/Vector3D.length(R_rotated),	 //x-Anteil 
				R_rotated.y/Vector3D.length(R_rotated),  //y-Anteil
				R_rotated.z/Vector3D.length(R_rotated)   //z-Anteil
		); 
		
		C_rotated.setVector(
				C_rotated.x/Vector3D.length(C_rotated),	 //x-Anteil 
				C_rotated.y/Vector3D.length(C_rotated),  //y-Anteil
				C_rotated.z/Vector3D.length(C_rotated)   //z-Anteil
		);
		
		int xLength = canvas.getItem().size();
		int yLength = img.width;
		int zLength = img.height;
		
		AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
		
		//for(int z = 0; z < zLength; z++){
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						//result.setPixel(x, y, canvas.loadImage(x).getPixel(y, index));
						result.setPixel(x, y, (((DicomObject) canvas.getItem().getChild(x)).getSimplePixel(y, index, 0)));
					}
				}
				
				//BilinearInterpolation interpolation = new BilinearInterpolation(result);
				//result = interpolation.resample(result.width, result.height, img.width, img.height);
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
			//}
			return result;
	}
	
	public AImage sagittalToCoronal(int index, AImage img){

		Vector3D<Float> rotation = new Vector3D<Float>(0f, 0f,90f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		//Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		//Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
	
		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateZ(rotation.z, R);
		Vector3D<Float> C_rotated = Vector3D.rotateZ(rotation.z, C);
		
		//Es folgt eine Normalisierung auf die Länge 1
		Vector3D<Float> rotated_normal = Vector3D.crossProduct(R_rotated, C_rotated);
		rotated_normal.setVector(
				rotated_normal.x/Vector3D.length(rotated_normal), 
				rotated_normal.y/Vector3D.length(rotated_normal), 
				rotated_normal.z/Vector3D.length(rotated_normal)
		);
		
		R_rotated.setVector(
				R_rotated.x/Vector3D.length(R_rotated),	 //x-Anteil 
				R_rotated.y/Vector3D.length(R_rotated),  //y-Anteil
				R_rotated.z/Vector3D.length(R_rotated)   //z-Anteil
		); 
		
		C_rotated.setVector(
				C_rotated.x/Vector3D.length(C_rotated),	 //x-Anteil 
				C_rotated.y/Vector3D.length(C_rotated),  //y-Anteil
				C_rotated.z/Vector3D.length(C_rotated)   //z-Anteil
		);
		
		int xLength = canvas.getItem().size();
		int yLength = img.height;
		int zLength = img.width;
		
		AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
		
		//for(int z = 0; z < zLength; z++){
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, canvas.loadImage(x).getPixel(index, y));
						result.setPixel(x, y, (((DicomObject) canvas.getItem().getChild(x)).getSimplePixel(index, y, 0)));
					}
				}
				
				//BilinearInterpolation interpolation = new BilinearInterpolation(result);
				//result = interpolation.resample(result.width, result.height, img.width, img.height);
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
			//}
			System.out.println("done");
			return result;
	}*/
	
	/**
	 * Methode zur Umrechnung von Gradmass nach Bogenmass
	 * 
	 * @param alpha in Grad
	 * @return radiant
	 */
	/*private float deg2rad(float alpha){
		return (float) (((2f*Math.PI)/360f)*alpha);
	}*/
	
	/**
	 * Bestimmt mittels der Werte des Vektors v den groessten <p>Betrag</p> von V
	 * 
	 * @param v
	 * @return
	 */
	/*private int getDominantIndex(Vector3D<Float> v){
		float x = Math.abs(v.x);
		float y = Math.abs(v.y);
		float z = Math.abs(v.z);
		
		int max = 0;
		float maxNumber = x;
		
		if(y > maxNumber){
			max = 1;
			maxNumber = y;
		}
		if(z > maxNumber){
			max = 2;
			maxNumber = z;
		}
		return max;
	}*/
	
	/**
	 * Bestimmt mittels der Werte des Vektors v den kleinsten <p>Betrag</p> von V
	 * 
	 * @param v
	 * @return
	 */
	/*private int getRecessiveIndex(Vector3D<Float> v){
		float x = Math.abs(v.x);
		float y = Math.abs(v.y);
		float z = Math.abs(v.z);
		
		int min = 0;
		float minNumber = x;
		
		if(y < minNumber){
			min = 1;
			minNumber = y;
		}
		if(z < minNumber){
			min = 2;
			minNumber = z;
		}
		
		//System.out.println("Minimum of "+v.toString()+" = " + min);
		return min;
	}
	
	public void setTranslation(float t){
		translation = t;
	}
	
	public float getTranslation(){
		return translation;
	}
	
	//public AbstractImage getImageData(int index){

	//}
}*/