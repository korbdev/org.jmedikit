package org.jmedikit.lib.image;

import java.util.ArrayList;

import org.jmedikit.lib.util.Point2D;
import org.jmedikit.lib.util.SimpleImageFactory;
import org.jmedikit.lib.util.Vector3D;

public class ImageCube {
	
	private ArrayList<AbstractImage> images;
	
	/**
	 * Rotation um die X-Achse angabe im Bogenmass
	 */
	private float rX;
	
	/**
	 * Rotation um die Y-Achse angabe im Bogenmass
	 */
	private float rY;
	
	/**
	 * Rotation um die Z-Achse angabe im Bogenmass
	 */
	private float rZ;
	
	public ImageCube(ArrayList<AbstractImage> images){
		this.images = images;
	}
	
	public AbstractImage calc3D(int index){
		
		AbstractImage img = images.get(index);
		AbstractImage result = SimpleImageFactory.getAbstractImage(img.imageType, img.getWidth(), img.getHeight());
		
		Vector3D<Float> I = img.getImagePosition();
		Vector3D<Float> R = img.getRowImageOrientation();
		Vector3D<Float> C = img.getColumnImageOrientation();

		Vector3D<Float> firstSlice = images.get(0).getImagePosition();
		Vector3D<Float> nthSlice = images.get(images.size()-1).getImagePosition();
		
		Vector3D<Float> rotationCenter = getSpaceCoordinates(I, R, C, img.pixelSpacing, img.getWidth()/2, img.getHeight()/2);
		
		Vector3D<Float> maxDistance = Vector3D.substract(nthSlice, firstSlice);
		
		float maxImageDistanceLength = Vector3D.length(new Vector3D<Float>((float) img.getWidth(), (float) img.getHeight(), 0f, 1f));
		
		int maxIndex = getDominantGroth(maxDistance);
		
		
		//System.out.println("1st"+firstSlice.toString()+ "nth "+ nthSlice.toString()+" maxDist "+maxDistance.toString());
		
		//Die beiden Richtungsvektoren werden der Reihenfolge x -> y -> z gedreht
		Vector3D<Float> R_rotated = Vector3D.rotateZ(rZ, Vector3D.rotateY(rY, Vector3D.rotateX(rX, R)));
		Vector3D<Float> C_rotated = Vector3D.rotateZ(rZ, Vector3D.rotateY(rY, Vector3D.rotateX(rX, C)));
		
		//Es folgt eine Normalisierung auf die L�nge 1
		System.out.println("I "+I.toString());
		//I = I_rotated;

		Vector3D<Float> I_trans = Vector3D.translate(rotationCenter.x, rotationCenter.y, rotationCenter.z, I);
		System.out.println("ITRANS "+I_trans.toString());
		Vector3D<Float> I_rotated = Vector3D.rotateZ(rZ, Vector3D.rotateY(rY, Vector3D.rotateX(rX, I_trans)));
		System.out.println("IROT "+I_rotated.toString());
		//I = Vector3D.translate(-rotationCenter.x, -rotationCenter.y, -rotationCenter.z, I_rotated);
		
		I = I_rotated;
		System.out.println("I f"+I.toString());
		
		
		
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

		//Vorberechnung
		ArrayList<ArrayList<Vector3D<Float>>> boundingBoxes = precalculateBoundingBoxes(R, C);
		
		Vector3D<Float> coordinate;
		
		//System.out.println(rotated_normal.toString());
		int trans = 80;
		
		int value = img.getMin();
		//System.out.println(rotated_normal.x*trans+", "+rotated_normal.y*trans+", "+rotated_normal.z*trans);
		I = Vector3D.translate(rotated_normal.x*trans,rotated_normal.y*trans, rotated_normal.z*trans, I);
		//System.out.println(I.toString());
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				
				coordinate = getSpaceCoordinates(I, R_rotated, C_rotated, img.pixelSpacing, x, y);
				
				Vector3D<Float> tempDist = Vector3D.substract(coordinate, firstSlice);
				float layer = (float) (((Math.abs(tempDist.get(maxIndex))/Math.abs(maxDistance.get(maxIndex)))*images.size())+0.5);
				
				AbstractImage layerImg;
				ArrayList<Vector3D<Float>> boundingBox;
				//Vector3D<Float> planeImagePosition;
				//Vector3D<Float> plane_R_Orientation;
				//Vector3D<Float> plane_C_Orientation;
				if(layer < images.size()){
					layerImg =  images.get((int)layer);
					boundingBox = boundingBoxes.get((int)(layer));
					
					//planeImagePosition = layerImg.getImagePosition();
					//plane_R_Orientation = layerImg.getRowImageOrientation();
					//plane_C_Orientation = layerImg.getColumnImageOrientation();
					/////////////////////////////////////////////////////////////////
					
					Vector3D<Float> TL = boundingBox.get(0);
					Vector3D<Float> TR = boundingBox.get(1);
					Vector3D<Float> BL = boundingBox.get(2);
					Vector3D<Float> BR = boundingBox.get(3);
					
					//System.out.println(tempDist.toString()+"Coord "+ Math.abs(tempDist.get(maxIndex))+", Max "+Math.abs(maxDistance.get(maxIndex))+", % "+(Math.abs(tempDist.get(maxIndex))/Math.abs(maxDistance.get(maxIndex))));
					//float[] lfp = getPlanePointDistance(Vector3D.crossProduct(plane_R_Orientation, plane_C_Orientation), planeImagePosition, coordinate);
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

					if(x_index >= img.getWidth()){
						x_index = img.getWidth()-1;
					}
					if(y_index >= img.getHeight()){
						y_index = img.getHeight()-1;
					}
					
					if(x_index < 0){
						x_index = 0;
					}
					if(y_index < 0){
						y_index = 0;;
					}
					
					
					value = layerImg.getPixel(x_index, y_index);
					/////////////////////////////////////////////////////////////////
					
				}
				else{
					layerImg =  images.get(images.size()-1);
					boundingBox = boundingBoxes.get(images.size()-1);
					//planeImagePosition = layerImg.getImagePosition();
					//plane_R_Orientation = layerImg.getRowImageOrientation();
					//plane_C_Orientation = layerImg.getColumnImageOrientation();
				}
				
				/*Vector3D<Float> TL = boundingBox.get(0);
				Vector3D<Float> TR = boundingBox.get(1);
				Vector3D<Float> BL = boundingBox.get(2);
				Vector3D<Float> BR = boundingBox.get(3);
				
				//System.out.println(tempDist.toString()+"Coord "+ Math.abs(tempDist.get(maxIndex))+", Max "+Math.abs(maxDistance.get(maxIndex))+", % "+(Math.abs(tempDist.get(maxIndex))/Math.abs(maxDistance.get(maxIndex))));
				//float[] lfp = getPlanePointDistance(Vector3D.crossProduct(plane_R_Orientation, plane_C_Orientation), planeImagePosition, coordinate);
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

				if(x_index >= img.getWidth()){
					x_index = img.getWidth()-1;
				}
				if(y_index >= img.getHeight()){
					y_index = img.getHeight()-1;
				}
				
				if(x_index < 0){
					x_index = 0;
				}
				if(y_index < 0){
					y_index = 0;;
				}*/
				
				result.setPixel(x, y, value);
				
				//System.out.println(", "+x_index+",  "+y_index);
				//result.setPixel(x, y, layerImg.getPixel(x_index, y_index));
				//System.out.println(lfp[0]);
				//System.out.println("slice index "+p);
			}
		}
		return result;
	}
	
	
	/**
	 * Die Methode nimmt als Parameter Grad entgegen. Die Umrechnung in Bogenmass
	 * findet in der Methode statt.
	 * 
	 * @param x Angabe Grad der Rotation um die X-Achse
	 * @param y Angabe Grad der Rotation um die Y-Achse
	 * @param z Angabe Grad der Rotation um die Z-Achse
	 */
	public void setRotationsAngles(float x, float y, float z){
		rX = deg2rad(x);
		rY = deg2rad(y);
		rZ = deg2rad(z);
	}
	
	public Vector3D<Float> getSpaceCoordinates(Vector3D<Float> I, Vector3D<Float> R, Vector3D<Float> C, Point2D<Float> pixelSpacing, int x, int y){
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
	}
	
	public float[] getPlanePointDistance(Vector3D<Float> normal, Vector3D<Float> planePoint, Vector3D<Float> point){
		float[] params = new float[4];

		float r2 = (-(Vector3D.dotProduct(Vector3D.substract(point, planePoint), normal)))/(Vector3D.dotProduct(normal, normal));
		
		Vector3D<Float> lfp = Vector3D.add(point, Vector3D.smult(r2, normal));
		
		Vector3D<Float> olpl = Vector3D.substract(lfp, point);

		params[0] =  Vector3D.length(olpl);
		params[1] = lfp.x;
		params[2] = lfp.y;
		params[3] = lfp.z;
		
		return params;
	}
	
	/**
	 * Vorberechnung der Bounding-Boxen 
	 * 
	 * Vorraussetzung, dass Richtungsvektoren �ber den Bildstapel konsistent sind
	 */
	public ArrayList<ArrayList<Vector3D<Float>>> precalculateBoundingBoxes(Vector3D<Float> rowVector, Vector3D<Float> columnVector){
		
		ArrayList<ArrayList<Vector3D<Float>>> boudingBoxes = new ArrayList<ArrayList<Vector3D<Float>>>();
		
		for(int i = 0; i < images.size(); i++){
			
			ArrayList<Vector3D<Float>> boundingBox = new ArrayList<Vector3D<Float>>();
			
			AbstractImage img = images.get(i);
			
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
	}
	
	/**
	 * Methode zur Umrechnung von Gradmass nach Bogenmass
	 * 
	 * @param alpha in Grad
	 * @return radiant
	 */
	private float deg2rad(float alpha){
		return (float) (((2f*Math.PI)/360f)*alpha);
	}
	
	
	private int getDominantGroth(Vector3D<Float> v){
		float x = Math.abs(v.x);
		float y = Math.abs(v.y);
		float z = Math.abs(v.z);
		
		int max = 0;
		
		if(x > y && x > z){
			max = 0;
		}
		else if(y > x && y > z){
			max = 1;
		}
		else if(z > x && z > y){
			max = 2;
		}
		System.out.println(max);
		return max;
	}
	
}
