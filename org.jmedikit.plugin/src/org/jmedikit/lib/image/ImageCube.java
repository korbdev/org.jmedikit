package org.jmedikit.lib.image;

import java.util.ArrayList;

import org.jmedikit.lib.util.Point2D;
import org.jmedikit.lib.util.SimpleImageFactory;
import org.jmedikit.lib.util.Vector3D;

/**
 * ImageCube berechnet Ebenenrekonstruktionen. Wird dem Konstruktor ein Bildstapel �bergeben, kann Anhand dieser Bilder die axiale, coronale und sagittale
 * Ansicht berechnet werden. 
 * 
 * @author rkorb
 *
 */
public class ImageCube {
	
	/**
	 * Bildstapel der Rekonstruiert wird
	 */
	private ArrayList<AImage> images;
	
	/**
	 * Rotation um die X-Achse angabe im Bogenmass
	 */
	//private float rX;
	
	/**
	 * Rotation um die Y-Achse angabe im Bogenmass
	 */
	//private float rY;
	
	/**
	 * Rotation um die Z-Achse angabe im Bogenmass
	 */
	//private float rZ;
	
	//public float m;
	
	//public int slicesIndex;

	//private float translation;
	
	/**
	 * Der Konstruktor nimmt eine Liste von Bildern entgegen, die je nach Quell- und Zieldarstellung rekonstruiert werden.
	 * 
	 * @param images Bildstapel der Rekonstruiert werden soll
	 */
	public ImageCube(ArrayList<AImage> images){
		this.images = images;
	}
	

	/**
	 * <p>Berechnet die axiale Ebenenrekonstruktion</p>
	 * <p>Ist der Bildstapel bereits in axialer Darstellung, wird nur der Stapel ohne Berechnung zur�ckgegeben</p>
	 * 
	 * @return rekonstruierter Bildstapel
	 */
	public ArrayList<AImage> calculateAxialView(){
		AImage img = images.get(0);

		if(img.mprType.equals(AImage.SAGITTAL)){
			return sagittalToAxial(img);
		}
		else if(img.mprType.equals(AImage.CORONAL)){
			return coronalToAxial(img);
		}
		else return images;
	}
	
	/**
	 * <p>Berechnet die coronalen Ebenenrekonstruktion</p>
	 * <p>Ist der Bildstapel bereits in coronaler Darstellung, wird nur der Stapel ohne Berechnung zur�ckgegeben</p>
	 * 
	 * @return rekonstruierter Bildstapel
	 */
	public ArrayList<AImage> calculateCoronalView(){
		AImage img = images.get(0);
		
		if(img.mprType.equals(AImage.AXIAL)){
			return axialToCoronal(img);
		}
		else if(img.mprType.equals(AImage.SAGITTAL)){
			return sagittalToCoronal(img);
		}
		else return images;
	}
	
	/**
	 * <p>Berechnet die sagittale Ebenenrekonstruktion</p>
	 * <p>Ist der Bildstapel bereits in sagittaler Darstellung, wird nur der Stapel ohne Berechnung zur�ckgegeben</p>
	 * 
	 * @return rekonstruierter Bildstapel
	 */
	public ArrayList<AImage> calculateSagittalView(){
		AImage img = images.get(0);
		
		if(img.mprType.equals(AImage.AXIAL)){
			return axialToSagittal(img);
		}
		else if(img.mprType.equals(AImage.CORONAL)){
			return coronalToSagittal(img);
		}
		else return images;
	}
	
	private boolean needsSignChange(Vector3D<Float> firstStackSlice, Vector3D<Float> nthStackSlice, Vector3D<Float> normale){
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
	
	private ArrayList<AImage> axialToCoronal(AImage img){
		ArrayList<AImage> recalculatedImages = new ArrayList<AImage>();
		
		Vector3D<Float> rotation = new Vector3D<Float>(90f, 0f, 0f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
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
		
		//Es folgt eine Normalisierung auf die L�nge 1
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
		
		int xLength = img.width;
		int yLength = images.size();
		int zLength = img.height;
		
		for(int z = 0; z < zLength; z++){

			AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
			
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, images.get(y).getPixel(x, z));
					}
				}
				
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
				recalculatedImages.add(result);
			}
			return recalculatedImages;
	}
	
	private ArrayList<AImage> axialToSagittal(AImage img){
		ArrayList<AImage> recalculatedImages = new ArrayList<AImage>();
		
		Vector3D<Float> rotation = new Vector3D<Float>(90f, 90f, 0f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();

		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateY(rotation.y, R));
		Vector3D<Float> C_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateY(rotation.y, C));
		
		//Es folgt eine Normalisierung auf die L�nge 1
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
		int yLength = images.size();
		int zLength = img.width;
		
		for(int z = 0; z < zLength; z++){

			AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
			
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, images.get(y).getPixel(z, x));
					}
				}
				
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);

				recalculatedImages.add(result);
			}
			return recalculatedImages;
	}
	
	private ArrayList<AImage> coronalToAxial(AImage img){
		ArrayList<AImage> recalculatedImages = new ArrayList<AImage>();
		
		Vector3D<Float> rotation = new Vector3D<Float>(90f, 0f, 0f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();

		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateX(deg2rad(rotation.x), R);
		Vector3D<Float> C_rotated = Vector3D.rotateX(deg2rad(rotation.x), C);
		
		//Es folgt eine Normalisierung auf die L�nge 1
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
		
		int xLength = img.width;
		int yLength = images.size();
		int zLength = img.height;
		
		for(int z = 0; z < zLength; z++){

			AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
			
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, images.get(y).getPixel(x, z));
					}
				}
				
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
				recalculatedImages.add(result);
			}
			return recalculatedImages;
	}
	
	private ArrayList<AImage> coronalToSagittal(AImage img){
		ArrayList<AImage> recalculatedImages = new ArrayList<AImage>();
		
		Vector3D<Float> rotation = new Vector3D<Float>(0f, 0f, 90f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
	
		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateZ(rotation.z, R);
		Vector3D<Float> C_rotated = Vector3D.rotateZ(rotation.z, C);
		
		//Es folgt eine Normalisierung auf die L�nge 1
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
		
		int xLength = images.size();
		int yLength = img.height;
		int zLength = img.width;
		
		for(int z = 0; z < zLength; z++){
	
			AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
			
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, images.get(x).getPixel(z, y));
					}
				}
				
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
	
				recalculatedImages.add(result);
			}
			return recalculatedImages;
	}

	private ArrayList<AImage> sagittalToAxial(AImage img){
		ArrayList<AImage> recalculatedImages = new ArrayList<AImage>();
		
		Vector3D<Float> rotation = new Vector3D<Float>(90f, 0f, -90f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();

		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateZ(rotation.z, R));
		Vector3D<Float> C_rotated = Vector3D.rotateX(rotation.x,Vector3D.rotateZ(rotation.z, C));
		
		//Es folgt eine Normalisierung auf die L�nge 1
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
		
		int xLength = images.size();
		int yLength = img.width;
		int zLength = img.height;
		
		for(int z = 0; z < zLength; z++){

			AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
			
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, images.get(x).getPixel(y, z));
					}
				}
				
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);

				recalculatedImages.add(result);
			}
			return recalculatedImages;
	}
	
	private ArrayList<AImage> sagittalToCoronal(AImage img){
		ArrayList<AImage> recalculatedImages = new ArrayList<AImage>();
		
		Vector3D<Float> rotation = new Vector3D<Float>(0f, 0f,90f, 1f);
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();
		Vector3D<Float> N = Vector3D.crossProduct(R, C);
		
		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
	
		if(needsSignChange(firstSlice, nthSlice, N)){
			rotation.x = rotation.x*-1;
			rotation.y = rotation.y*-1;
			rotation.z = rotation.z*-1;
		}
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateZ(rotation.z, R);
		Vector3D<Float> C_rotated = Vector3D.rotateZ(rotation.z, C);
		
		//Es folgt eine Normalisierung auf die L�nge 1
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
		
		int xLength = images.size();
		int yLength = img.height;
		int zLength = img.width;
		
		for(int z = 0; z < zLength; z++){
	
			AImage result = SimpleImageFactory.getAbstractImage(img.imageType, xLength, yLength);
			
				for(int y = 0; y < yLength; y++){
					for(int x = 0; x < xLength; x++){
						result.setPixel(x, y, images.get(x).getPixel(z, y));
					}
				}
				
				result.imagePosition = I;
				result.rowVector = R_rotated;
				result.columnVector = C_rotated;
				result.copySignificantAttributes(img);
	
				recalculatedImages.add(result);
			}
			return recalculatedImages;
	}

	/*public ArrayList<AImage> calc3D(int index){
		
		AImage img = images.get(index);
		ArrayList<AImage> recalculatedImages = new ArrayList<AImage>();
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();

		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
		
		//Vector3D<Float> rotationCenter = getSpaceCoordinates(I, R, C, img.pixelSpacing, img.getWidth()/2, img.getHeight()/2);
		
		Vector3D<Float> maxDistance = Vector3D.substract(nthSlice, firstSlice);
		
		float maxImageDistanceLength = Vector3D.length(new Vector3D<Float>((float) img.getWidth(), (float) img.getHeight(), 0f, 1f));
		
		int maxIndex = getDominantIndex(maxDistance);
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateZ(rZ, Vector3D.rotateY(rY, Vector3D.rotateX(rX, R)));
		Vector3D<Float> C_rotated = Vector3D.rotateZ(rZ, Vector3D.rotateY(rY, Vector3D.rotateX(rX, C)));
		
		//System.out.println("I "+I.toString());
		//I = I_rotated;

		//Vector3D<Float> I_trans = Vector3D.translate(rotationCenter.x, rotationCenter.y, rotationCenter.z, I);
		//System.out.println("ITRANS "+I_trans.toString());
		//Vector3D<Float> I_rotated = Vector3D.rotateZ(rZ, Vector3D.rotateY(rY, Vector3D.rotateX(rX, I_trans)));
		//System.out.println("IROT "+I_rotated.toString());
		//I = Vector3D.translate(-rotationCenter.x, -rotationCenter.y, -rotationCenter.z, I_rotated);
		
		//I = I_rotated;
		//System.out.println("I f"+I.toString());
		
		
		//Es folgt eine Normalisierung auf die L�nge 1
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

		System.out.println(rotated_normal.toString());
		
		//Vorberechnung
		ArrayList<ArrayList<Vector3D<Float>>> boundingBoxes = precalculateBoundingBoxes(R, C);
		
		System.out.println("Normale "+rotated_normal.toString());
		
		Vector3D<Float> tl = boundingBoxes.get(0).get(0);
		Vector3D<Float> tr = boundingBoxes.get(0).get(1);
		Vector3D<Float> bl = boundingBoxes.get(0).get(2);
		Vector3D<Float> br = boundingBoxes.get(0).get(3);
		
		System.out.println("TRTL "+Vector3D.substract(tr, tl).toString()+", "+(Vector3D.substract(tr, tl).x/img.pixelSpacing.x)+", "+img.width);
		System.out.println("BLTL "+Vector3D.substract(bl, tl).toString()+", "+(Vector3D.substract(bl, tl).y/img.pixelSpacing.y)+", "+img.height);
		System.out.println("BRTL "+Vector3D.substract(br, tl).toString());
		
		System.out.println("DOT N TRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(tr, tl)));
		System.out.println("DOT N BLTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(bl, tl)));
		System.out.println("DOT N BRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(br, tl)));
		
		System.out.println("ANGLE N TRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(tr, tl))/(Vector3D.length(rotated_normal)*Vector3D.length(Vector3D.substract(tr, tl))));
		System.out.println("ANGLE N BLTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(bl, tl))/(Vector3D.length(rotated_normal)*Vector3D.length(Vector3D.substract(bl, tl))));
		System.out.println("ANGLE N BRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(br, tl))/(Vector3D.length(rotated_normal)*Vector3D.length(Vector3D.substract(br, tl))));
		
		//Bildgr��e berechnen
		//wenn die Normale in X-Richtung zeigt vertausche Bildbreite und Bildhoehe
		int width;
		int height;
		
		int normalDirection = getDominantIndex(rotated_normal);
		System.out.println("NormalDir "+normalDirection);
		if(normalDirection == 0){
			width = img.height;
			height = img.width;
		}
		else{
			width = img.width;
			height = img.height;
		}
		//Maximalen Index bestimmen
		//durch die obere rechte und untere linke Bildecken wird der maximale Index ermittelt. Die rotierte Normale muss senkrecht
		//auf den Ortsvektoren RECHTSOBEN oder LINKSUNTEN stehen
		Vector3D<Float> trtl = Vector3D.substract(tr, tl);
		Vector3D<Float> bltl = Vector3D.substract(bl, tl);
		Vector3D<Float> brtl = Vector3D.substract(br, tl);
		
		float dotNormalTrtl = Vector3D.dotProduct(rotated_normal, trtl);
		float dotNormalBltl = Vector3D.dotProduct(rotated_normal, bltl);
		float dotNormalBrtl = Vector3D.dotProduct(rotated_normal, brtl);
		
		Vector3D<Float> dotProducts = new Vector3D<Float>(dotNormalTrtl, dotNormalBltl, dotNormalBrtl, 1f);
		
		int indexDir = getRecessiveIndex(dotProducts);
		//int slicesIndex;
		float sliceSpacing;
		
		if(indexDir == 0){
			slicesIndex = width;
			width = images.size();
			sliceSpacing = img.pixelSpacing.x;
		}
		else{
			slicesIndex = height;
			height = images.size();
			sliceSpacing = img.pixelSpacing.y;
		}
			
		System.out.println("w "+width+", h "+height+", index "+slicesIndex);
		
		Vector3D<Float> coordinate;
		
		int value = img.getMin();
		Vector3D<Float> oldI = I;
		
		for(int i = 0; i < slicesIndex; i++){
			AImage result = SimpleImageFactory.getAbstractImage(img.imageType, width, height);
			
			translation = i*sliceSpacing;
			
			I = Vector3D.translate(rotated_normal.x*translation,rotated_normal.y*translation, rotated_normal.z*translation, oldI);
			
			System.out.println("OLD "+oldI.toString()+", NEW "+I.toString()+" Trans "+translation);
			
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					
					coordinate = getSpaceCoordinates(I, R_rotated, C_rotated, img.pixelSpacing, x, y);
					
					Vector3D<Float> tempDist = Vector3D.substract(coordinate, firstSlice);
					float layer = (float) (((Math.abs(tempDist.get(maxIndex))/Math.abs(maxDistance.get(maxIndex)))*images.size())+0.5);
					
					AImage layerImg;
					ArrayList<Vector3D<Float>> boundingBox;

					if(layer < images.size()){
						layerImg =  images.get((int)layer);
						boundingBox = boundingBoxes.get((int)(layer));
						/////////////////////////////////////////////////////////////////
						
						Vector3D<Float> TL = boundingBox.get(0);
						Vector3D<Float> TR = boundingBox.get(1);
						Vector3D<Float> BL = boundingBox.get(2);
						Vector3D<Float> BR = boundingBox.get(3);
						
						float[] lfp = getPlanePointDistance(Vector3D.crossProduct(R, C), TL, coordinate);
						
						Vector3D<Float> P = new Vector3D<Float>(lfp[1], lfp[2], lfp[3], 1f);
						Vector3D<Float> PTL = Vector3D.substract(P, TL);
						Vector3D<Float> TRTL = Vector3D.substract(TR, TL);
						Vector3D<Float> BLTL = Vector3D.substract(BL, TL);
						
						float maxLength = Vector3D.length(Vector3D.substract(BR, TL));
						float pointLength = Vector3D.length(PTL);
						
						float percent = pointLength / maxLength;
						
						float angle_P_TR = Vector3D.dotProduct(TRTL, PTL)/(Vector3D.length(TRTL)*(Vector3D.length(PTL)));
						float angle_P_BL = Vector3D.dotProduct(BLTL, PTL)/(Vector3D.length(BLTL)*(Vector3D.length(PTL)));
						
						
						int x_index = (int) ((angle_P_TR * (percent * maxImageDistanceLength))+0.5);
						int y_index = (int) ((angle_P_BL * (percent * maxImageDistanceLength))+0.5);
						
						if((x_index < img.getWidth() && y_index < img.getHeight()) && (x_index >= 0 && y_index >= 0)){
							value = layerImg.getPixel(x_index, y_index);
						}
						else value = 0;
						/////////////////////////////////////////////////////////////////
						
						/*System.out.println("Normale "+rotated_normal.toString());
						
						System.out.println("TRTL "+Vector3D.substract(TR, TL).toString()+", "+(Vector3D.substract(TR, TL).x/img.pixelSpacing.x)+", "+img.width);
						System.out.println("BLTL "+Vector3D.substract(BL, TL).toString()+", "+(Vector3D.substract(BL, TL).y/img.pixelSpacing.y)+", "+img.height);
						System.out.println("BRTL "+Vector3D.substract(BR, TL).toString());
						
						System.out.println("DOT N TRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(TR, TL)));
						System.out.println("DOT N BLTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(BL, TL)));
						System.out.println("DOT N BRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(BR, TL)));
						
						System.out.println("ANGLE N TRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(TR, TL))/(Vector3D.length(rotated_normal)*Vector3D.length(Vector3D.substract(TR, TL))));
						System.out.println("ANGLE N BLTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(BL, TL))/(Vector3D.length(rotated_normal)*Vector3D.length(Vector3D.substract(BL, TL))));
						System.out.println("ANGLE N BRTL "+Vector3D.dotProduct(rotated_normal, Vector3D.substract(BR, TL))/(Vector3D.length(rotated_normal)*Vector3D.length(Vector3D.substract(BR, TL))));*/
					/*}
					else{
						//layerImg =  images.get(images.size()-1);
						//boundingBox = boundingBoxes.get(images.size()-1);
						//planeImagePosition = layerImg.getImagePosition();
						//plane_R_Orientation = layerImg.getRowImageOrientation();
						//plane_C_Orientation = layerImg.getColumnImageOrientation();
					}
					result.setPixel(x, y, value);
				}
			}

			result.imagePosition = I;
			result.rowVector = R_rotated;
			result.columnVector = C_rotated;
			result.windowCenter = img.windowCenter;
			result.windowWidth = img.windowWidth;
			result.min = img.min;
			result.max = img.max;
			result.pixelSpacing = img.pixelSpacing;
			result.rescaleIntercept = img.rescaleIntercept;
			result.rescaleSlope = img.rescaleSlope;
			
			recalculatedImages.add(result);
			System.out.println("worked "+i+"/ "+slicesIndex);
		}
		return recalculatedImages;
	}*/
	
	
	/**
	 * Die Methode nimmt als Parameter Grad entgegen. Die Umrechnung in Bogenmass
	 * findet in der Methode statt.
	 * 
	 * @param x Angabe Grad der Rotation um die X-Achse
	 * @param y Angabe Grad der Rotation um die Y-Achse
	 * @param z Angabe Grad der Rotation um die Z-Achse
	 */
	/*public void setRotationsAngles(float x, float y, float z){
		rX = deg2rad(x);
		rY = deg2rad(y);
		rZ = deg2rad(z);
	}*/
	
	/*public Vector3D<Float> getSpaceCoordinates(Vector3D<Float> I, Vector3D<Float> R, Vector3D<Float> C, Point2D<Float> pixelSpacing, int x, int y){
		Vector3D<Float> coordinate = new Vector3D<Float>(0f, 0f, 0f, 1f);
		
		Vector3D<Float> imageIndex = new Vector3D<Float>((float)x, (float)y, 0f, 1f);
		
		float[][] transformationMatrix = new float[4][4];
		transformationMatrix[0][0] = R.x*pixelSpacing.x; 	transformationMatrix[0][1] = C.x*pixelSpacing.y; 	transformationMatrix[0][2] = 0;		transformationMatrix[0][3] = I.x;
		transformationMatrix[1][0] = R.y*pixelSpacing.x; 	transformationMatrix[1][1] = C.y*pixelSpacing.y; 	transformationMatrix[1][2] = 0;		transformationMatrix[1][3] = I.y;
		transformationMatrix[2][0] = R.z*pixelSpacing.x; 	transformationMatrix[2][1] = C.z*pixelSpacing.y; 	transformationMatrix[2][2] = 0;		transformationMatrix[2][3] = I.z;
		transformationMatrix[3][0] = 0; 					transformationMatrix[3][1] = 0; 					transformationMatrix[3][2] = 0; 	transformationMatrix[3][3] = 1;
		
		for(int i = 0; i < transformationMatrix.length; i++){
			for(int j = 0; j < transformationMatrix[i].length; j++){
				coordinate.set(i, coordinate.get(i)+imageIndex.get(j)*transformationMatrix[i][j]); 
			}
		}
		
		return coordinate;
	}*/
	
	/*public float[] getPlanePointDistance(Vector3D<Float> normal, Vector3D<Float> planePoint, Vector3D<Float> point){
		float[] params = new float[4];

		float r2 = (-(Vector3D.dotProduct(Vector3D.substract(point, planePoint), normal)))/(Vector3D.dotProduct(normal, normal));
		
		Vector3D<Float> lfp = Vector3D.add(point, Vector3D.smult(r2, normal));
		
		Vector3D<Float> olpl = Vector3D.substract(lfp, point);

		params[0] =  Vector3D.length(olpl);
		params[1] = lfp.x;
		params[2] = lfp.y;
		params[3] = lfp.z;
		
		return params;
	}*/
	
	/**
	 * Vorberechnung der Bounding-Boxen 
	 * 
	 * Vorraussetzung, dass Richtungsvektoren �ber den Bildstapel konsistent sind
	 */
	/*public ArrayList<ArrayList<Vector3D<Float>>> precalculateBoundingBoxes(Vector3D<Float> rowVector, Vector3D<Float> columnVector){
		
		ArrayList<ArrayList<Vector3D<Float>>> boudingBoxes = new ArrayList<ArrayList<Vector3D<Float>>>();
		
		for(int i = 0; i < images.size(); i++){
			
			ArrayList<Vector3D<Float>> boundingBox = new ArrayList<Vector3D<Float>>();
			
			AImage img = images.get(i);
			
			Vector3D<Float> P_i = img.getImagePosition();
			
			Vector3D<Float> TL = P_i;
			Vector3D<Float> TR = getSpaceCoordinates(P_i, rowVector, columnVector, img.pixelSpacing, img.getWidth(), 0);
			Vector3D<Float> BL = getSpaceCoordinates(P_i, rowVector, columnVector, img.pixelSpacing, 0, img.getHeight());
			Vector3D<Float> BR = getSpaceCoordinates(P_i, rowVector, columnVector, img.pixelSpacing, img.getWidth(), img.getHeight());
			
			boundingBox.add(0, TL);
			boundingBox.add(1, TR);
			boundingBox.add(2, BL);
			boundingBox.add(3, BR);
			
			boudingBoxes.add(boundingBox);
		}
		
		return boudingBoxes;
	}*/
	
	/**
	 * Methode zur Umrechnung von Gradmass nach Bogenmass
	 * 
	 * @param alpha in Grad
	 * @return radiant
	 */
	private float deg2rad(float alpha){
		return (float) (((2f*Math.PI)/360f)*alpha);
	}
	
	/**
	 * Bestimmt mittels der Werte des Vektors v den groessten <b>Betrag</b> von V
	 * 
	 * @param v
	 * @return
	 */
	public static int getDominantIndex(Vector3D<Float> v){
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
	}
	
	/**
	 * Bestimmt mittels der Werte des Vektors v den kleinsten <b>Betrag</b> von V
	 * 
	 * @param v
	 * @return
	 */
	public static int getRecessiveIndex(Vector3D<Float> v){
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
	
	//public void setTranslation(float t){
	//	translation = t;
	//}
	
	//public float getTranslation(){
	//	return translation;
	//}
}
