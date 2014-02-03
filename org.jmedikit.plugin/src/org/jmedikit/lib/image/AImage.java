package org.jmedikit.lib.image;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jmedikit.lib.util.Point2D;
import org.jmedikit.lib.util.Vector3D;

public abstract class AImage implements Comparable<AImage>{
	
	public static final int TYPE_BYTE_SIGNED = 0;
	public static final int TYPE_BYTE_UNSIGNED = 1;
	public static final int TYPE_SHORT_SIGNED = 2;
	public static final int TYPE_SHORT_UNSIGNED = 3;
	public static final int TYPE_INT_SIGNED = 4;
	public static final int TYPE_INT_UNSIGNED = 5;
	
	public static final String PATIENT_RIGHT = "R";
	public static final String PATIENT_LEFT = "L";
	public static final String PATIENT_ANTERIOR = "A";
	public static final String PATIENT_POSTERIOR = "P";
	public static final String PATIENT_HEAD = "H";
	public static final String PATIENT_FOOT = "F";
	
	public static final String CORONAL = "CORONAL";
	public static final String SAGITTAL = "SAGITTAL";
	public static final String AXIAL = "AXIAL";
	private static final String OBLIQUE = "OBLIQUE";
	
	/**
	 * PixelData is stored as unsigned value
	 */
	public static final int TYPE_UNSIGNED = 0;
	
	/**
	 * PixelData is stored as Two Complement
	 */
	public static final int TYPE_SIGNED = 1;
	
	
	protected String title;
	
	/**
	 * Image width
	 */
	protected int width;
	
	/**
	 * Image height
	 */
	protected int height;
	
	protected int samplesPerPixel;
	
	/**
	 * Aspect Ratio width/height
	 */
	protected float aspectRatio;
	
	/**
	 * Minimum value in the Image
	 */
	protected int min;
	
	/**
	 * Maximum value in the Image
	 */
	protected int max;
	
	/**
	 * is set when extrema are determined via method determinMinMaxValues
	 */
	protected boolean extrema;
	
	/**
	 * device independet values are calculated with the equation
	 * rescaleSlope * storedValue + rescale Intercept
	 */
	protected float rescaleSlope;
	
	/**
	 * description @see {@link AImage#rescaleSlope}
	 */
	protected float rescaleIntercept;
	
	/**
	 * Window width and window center is used to map grey values to a scale of 0 - 255
	 * Medical images usually use grey values between 0 and 4096
	 * 
	 * if window width = 200 and window center = 0
	 * pixels between -100 and 100 are mapped
	 * 
	 * values lower than -100 are mapped as black
	 * values higher than 100 are mapped as white
	 * between the pixels store values from 1 to 254
	 */
	protected float windowWidth;
	
	/**
	 * description @see {@link AImage#windowWidth}
	 */
	protected float windowCenter;
	
	protected Vector3D<Float> rowVector;
	
	protected Vector3D<Float> columnVector;
	
	protected Vector3D<Float> imagePosition;
	
	protected Point2D<Float> pixelSpacing;
	
	/**
	 * Beschreibt den Typ des Bildes
	 */
	protected int imageType;
	
	protected String mprType;
	
	protected ROI roi;
	
	protected ArrayList<Point2D<Float>> selectedPoints;
	
	protected boolean initializedOrientation;
	/**
	 * Basic Image Constructor
	 * 
	 * @param width
	 * @param height
	 */
	public AImage(int width, int height){
		title = "";
		this.width = width;
		this.height = height;
		aspectRatio = (float)width / (float)height;
		extrema = false;
		roi = new ROI();
		selectedPoints = new ArrayList<Point2D<Float>>();
		rowVector = new Vector3D<Float>(0f, 0f, 0f, 1f);
		columnVector = new Vector3D<Float>(0f, 0f, 0f, 1f);
		imagePosition = new Vector3D<Float>(0f, 0f, 0f, 1f);
		pixelSpacing = new Point2D<Float>(1f, 1f);
		samplesPerPixel = 1;
		mprType = "DEFAULT";
		initializedOrientation = false;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getImageType(){
		return imageType;
	}
	
	public int getSamplesPerPixel() {
		return samplesPerPixel;
	}

	/**
	 * 
	 * @return returns the image width 
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * 
	 * @return returns the image height
	 */
	public int getHeight(){
		return height;
	}
	
	public float getAspectRatio(){
		return aspectRatio;
	}
	
	public String getMprType() {
		return mprType;
	}

	public void setMprType(String mprType) {
		if(mprType.equals(AXIAL)){
			this.mprType = AXIAL;
		}
		else if(mprType.equals(CORONAL)){
			this.mprType = CORONAL;
		}
		else if(mprType.equals(SAGITTAL)){
			this.mprType = SAGITTAL;
		}
		else this.mprType = OBLIQUE;
	}

	public void setImageOrientation(float[] vectors){
		if(vectors.length == 6){
			rowVector.setVector(vectors[0], vectors[1], vectors[2]);
			columnVector.setVector(vectors[3], vectors[4], vectors[5]);
			initializedOrientation = true;
		}
		else throw new IllegalArgumentException("Vectors must have 6 values: got "+vectors.length);
	}
	
	public void setImagePosition(float[] vector){
		if(vector.length == 3){
			imagePosition.setVector(vector[0], vector[1], vector[2]);
			initializedOrientation = true;
		}
		else throw new IllegalArgumentException("Vector must have 3 values: got "+vector.length);
	}
	
	public void setImagePosition(Vector3D<Float> v){
		initializedOrientation = true;
		imagePosition = v;
	}
	
	public void setPixelSpacing(float[] point){
		if(point.length == 2){
			pixelSpacing.setPoint(point[0], point[1]);
		}
		else throw new IllegalArgumentException("Point must have 2 values: got "+point.length);
	}
	
	public Vector3D<Float> getRowImageOrientation(){
		return rowVector;
	}
	
	public void setRowImageOrientation(Vector3D<Float> v){
		initializedOrientation = true;
		rowVector = v;
	}
	
	public Vector3D<Float> getColumnImageOrientation(){
		return columnVector;
	}
	
	public void setColumnImageOrientation(Vector3D<Float> v){
		initializedOrientation = true;
		columnVector = v;
	}
	
	public Vector3D<Float> getImagePosition(){
		return imagePosition;
	}
	
	public Point2D<Float> getPixelSpacing(){
		return pixelSpacing;
	}
	
	public abstract int getMinValue();
	
	public abstract int getMaxValue();
	
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMinMaxValues(int min, int max){
		this.min = min;
		this.max = max;
		extrema = true;
	}
	
	public float getRescaleSlope() {
		return rescaleSlope;
	}

	public void setRescaleSlope(float rescaleSlope) {
		this.rescaleSlope = rescaleSlope;
	}

	public float getRescaleIntercept() {
		return rescaleIntercept;
	}

	public void setRescaleIntercept(float rescaleIntercept) {
		this.rescaleIntercept = rescaleIntercept;
	}

	public float getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(float windowWidth) {
		this.windowWidth = windowWidth;
	}

	public float getWindowCenter() {
		return windowCenter;
	}

	public void setWindowCenter(float windowCenter) {
		this.windowCenter = windowCenter;
	}
	
	public ROI getROI(){
		return roi;
	}
	
	public void setROI(ROI roi){
		this.roi = roi;
	}
	
	public void addPoint(Point2D<Float> p){
		selectedPoints.add(p);
	}
	
	public Point2D<Float> getPoint(int index){
		return selectedPoints.get(index);
	}
	
	public Point2D<Float> getPoint(int x, int y){
		float xN = (float)x/(float)width;
		float yN = (float)y/(float)height;
		
		for(Point2D<Float> p : selectedPoints){
			if(xN == p.x && yN == p.y){
				return p;
			}
		}
		return null;
	}
	
	public Point2D<Float> getPoint(float xN, float yN){
		for(Point2D<Float> p : selectedPoints){
			if(xN == p.x && yN == p.y){
				return p;
			}
		}
		return null;
	}
	
	public void removePoint(int index){
		selectedPoints.remove(index);
	}
	
	public void deletePoints(){
		selectedPoints.clear();
	}
	
	public ArrayList<Point2D<Float>> getPoints(){
		return selectedPoints;
	}
	
	public void setPoints(ArrayList<Point2D<Float>> points){
		selectedPoints = points;
	}
	/*public void assignExtrema(boolean extrema){
		this.extrema = extrema;
	}*/
	
	/**
	 * returns true if minimum and maximum are set
	 * 
	 * @return 
	 */
	public boolean extremaAssigned(){
		return extrema;
	}
	
	public boolean getInitializedOrientation(){
		return initializedOrientation;
	}
	
	/**
	 * 
	 * @param x x Location
	 * @param y	y Location
	 * @return returns a single Pixel value. Gibt int zurueck, cast von niedrige auf h�her unproblematisch
	 */
	public abstract int getPixel(int x, int y);
	
	
	/**
	 * 
	 */
	public abstract void setPixel(int x, int y, int value);
	
	/**
	 * 
	 * @return returns an array of pixel values. Array type depends on image type
	 */
	public abstract Object getPixels();
	
	/**
	 * Finds and return maximum of given byte array
	 * 
	 * @param pixels pixel array of bytes
	 * @return maximum
	 */
	protected static byte findBytetMaximum(byte[] pixels){
		byte maxValue = Byte.MIN_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			byte value = pixels[i];
			if(value > maxValue){
				maxValue = value;
			}
		}
		return maxValue;
	}
	
	/**
	 * Finds and return minimum of given byte array
	 * 
	 * @param pixels pixel array of bytes
	 * @return minimum
	 */
	protected static byte findByteMinimum(byte[] pixels){
		byte minValue = Byte.MAX_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			byte value = pixels[i];
			if(value < minValue){
				minValue = value;
			}
		}
		return minValue;
	}
	
	/**
	 * Finds and return maximum of given short array
	 * 
	 * @param pixels pixel array of shorts
	 * @return maximum
	 */
	protected static short findShortMaximum(short[] pixels){
		short maxValue = Short.MIN_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			short value = pixels[i];
			if(value > maxValue){
				maxValue = value;
			}
		}
		return maxValue;
	}
	
	/**
	 * Finds and return minimum of given short array
	 * 
	 * @param pixels pixel array of shorts
	 * @return minimum
	 */
	protected static short findShortMinimum(short[] pixels){
		short minValue = Short.MAX_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			short value = pixels[i];
			if(value < minValue){
				minValue = value;
			}
		}
		return minValue;
	}
	
	/**
	 * Finds and return maximum of given integer array
	 * 
	 * @param pixels pixel array of ints
	 * @return maximum
	 */
	protected static int findIntMaximum(int[] pixels){
		int maxValue = Integer.MIN_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			int value = pixels[i];
			if(value > maxValue){
				maxValue = value;
			}
		}
		return maxValue;
	}
	
	/**
	 * Finds and return minimum of given integer array
	 * 
	 * @param pixels pixel array of ints
	 * @return minimum
	 */
	protected static int findIntMinimum(int[] pixels){
		int minValue = Integer.MAX_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			int value = pixels[i];
			if(value < minValue){
				minValue = value;
			}
		}
		return minValue;
	}
	
	/**
	 * Finds and sets the minimum and maximum value of a given pixel array
	 * 
	 * @param pixels array of pixel values. Must be of type byte[], short[] or int[]
	 */
	public void determineMinMaxValues(Object pixels){
		if(pixels instanceof byte[]){
			min = findByteMinimum((byte[]) pixels);
			max = findBytetMaximum((byte[]) pixels);
			extrema = true;
		}
		else if(pixels instanceof short[]){
			min = findShortMinimum((short[]) pixels);
			max = findShortMaximum((short[]) pixels);
			extrema = true;
		}
		else if(pixels instanceof int[]){
			min = findIntMinimum((int[]) pixels);
			max = findIntMaximum((int[]) pixels);
			extrema = true;
		}
		else throw new IllegalArgumentException("PixelType of pixels not supported");
		//System.out.println(pixels.getClass().getName());
		//System.out.println("Minimum: "+min);
		//System.out.println("Maximum: "+max);
		
	}
	
	/**
	 * Converts an unsigned short stored in an integer value to signed short
	 * 
	 * @param toConvert value which needs to be converted
	 * @return
	 */
	public static short unsignedShortToSignedShort(int toConvert){
		return (short)toConvert;
	}
	
	/**
	 * Converts signed short to unsigned short value
	 * 
	 * @param toConvert
	 * @return
	 */
	public static int signedShortToUnsignedShort(short toConvert){
		return toConvert & 0xFFFF;
	}
	
	public static void printPixelValues(String path, AImage img){
		PrintWriter pWriter = null;
		try {
			pWriter = new PrintWriter(new FileWriter(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int y = 0; y < img.height; y++) {
			for (int x = 0; x < img.width; x++) {
				pWriter.print("("+img.getPixel(x, y)+")");
			}
			pWriter.println("");
		}
		pWriter.flush();
	}
	
	private static String[] getDominantAxis(float x, float y, float z){
		
		String[] xDirection = new String[2];
		String[] yDirection = new String[2];
		String[] zDirection = new String[2];
		
		if(x < 0){
			xDirection[0] = PATIENT_RIGHT;
			xDirection[1] = PATIENT_LEFT;
		}
		else{
			xDirection[0] = PATIENT_LEFT;
			xDirection[1] = PATIENT_RIGHT;
		}
		
		if(y < 0){
			yDirection[0] = PATIENT_ANTERIOR;
			yDirection[1] = PATIENT_POSTERIOR;
		}
		else{
			yDirection[0] = PATIENT_POSTERIOR;
			yDirection[1] = PATIENT_ANTERIOR;
		}
		
		if(z < 0){
			zDirection[0] = PATIENT_FOOT;
			zDirection[1] = PATIENT_HEAD;
		}
		else{
			zDirection[0] = PATIENT_HEAD;
			zDirection[1] = PATIENT_FOOT;
		}
		
		float absx = Math.abs(x);
		float absy = Math.abs(y);
		float absz = Math.abs(z);
		
		//System.out.println("XYZ "+ x+ ", "+y+", "+z);
		
		if(absx > absy && absx > absz){
			return xDirection;
		}
		else if(absy > absx && absy > absz){
			return yDirection;
		}
		else if(absz > absx && absz > absy){
			return zDirection;
		}
		else return new String[]{"-","-"};
	}
	
	/**
	 * @see <ul><li><a target="_blank" href="http://www.itk.org/pipermail/insight-users/2005-March/012246.html">http://www.itk.org/pipermail/insight-users/2005-March/012246.html</a></li></ul>
	 * @return
	 */
	public String[] getImageOrientationAxis(){
		
		String[] axes = new String[4];
		
		String[] row = getDominantAxis(rowVector.x, rowVector.y, rowVector.z);
		String[] col = getDominantAxis(columnVector.x, columnVector.y, columnVector.z);
		
		// Top -> Left -> Bottom -> Right
		axes[0] = col[1]; //Top
		axes[1] = row[0]; //Left
		axes[2] = col[0]; //Bottom
		axes[3] = row[1]; //Right
		
		if (!row.equals("-") && !col.equals("-")) {
			if      ((row[0].equals("R") || row[0].equals("L")) && (col[0].equals("A") || col[0].equals("P"))) mprType=AXIAL;
			else if ((col[0].equals("R") || col[0].equals("L")) && (row[0].equals("A") || row[0].equals("P"))) mprType=AXIAL;
		
			else if ((row[0].equals("R") || row[0].equals("L")) && (col[0].equals("H") || col[0].equals("F"))) mprType=CORONAL;
			else if ((col[0].equals("R") || col[0].equals("L")) && (row[0].equals("H") || row[0].equals("F"))) mprType=CORONAL;
		
			else if ((row[0].equals("A") || row[0].equals("P")) && (col[0].equals("H") || col[0].equals("F"))) mprType=SAGITTAL;
			else if ((col[0].equals("A") || col[0].equals("P")) && (row[0].equals("H") || row[0].equals("F"))) mprType=SAGITTAL;
		}
		else {
			mprType=OBLIQUE;
		}
		
		for(int i = 0; i < 4; i++){
			//System.out.print(axes[i]+", ");
		}
		
		//System.out.println(mprType);
		return axes;
	}
	
	
	public static String getImageOrientation(Vector3D<Float> rowVector, Vector3D<Float> columnVector){
		
		String[] row = getDominantAxis(rowVector.x, rowVector.y, rowVector.z);
		String[] col = getDominantAxis(columnVector.x, columnVector.y, columnVector.z);
		
		if (!row.equals("-") && !col.equals("-")) {
			if      ((row[0].equals("R") || row[0].equals("L")) && (col[0].equals("A") || col[0].equals("P"))) return AXIAL;
			else if ((col[0].equals("R") || col[0].equals("L")) && (row[0].equals("A") || row[0].equals("P"))) return AXIAL;
		
			else if ((row[0].equals("R") || row[0].equals("L")) && (col[0].equals("H") || col[0].equals("F"))) return CORONAL;
			else if ((col[0].equals("R") || col[0].equals("L")) && (row[0].equals("H") || row[0].equals("F"))) return CORONAL;
		
			else if ((row[0].equals("A") || row[0].equals("P")) && (col[0].equals("H") || col[0].equals("F"))) return SAGITTAL;
			else if ((col[0].equals("A") || col[0].equals("P")) && (row[0].equals("H") || row[0].equals("F"))) return SAGITTAL;
		}
		else {
			return OBLIQUE;
		}
		return null;
	}
	
	public void copySignificantAttributes(AImage img){
		//imagePosition = img.getImagePosition();
		//rowVector = img.getRowImageOrientation();
		//columnVector = img.getColumnImageOrientation();
		pixelSpacing = img.getPixelSpacing();
		windowCenter = img.windowCenter;
		windowWidth = img.windowWidth;
		min = img.min;
		max = img.max;
		pixelSpacing = img.pixelSpacing;
		rescaleIntercept = img.rescaleIntercept;
		rescaleSlope = img.rescaleSlope;
	}
	
	public int compareTo(AImage img){
		
		Vector3D<Float> imgIP = img.getImagePosition();
		
		Vector3D<Float> normal = Vector3D.crossProduct(columnVector, rowVector);
		
		int index = ImageCube.getDominantIndex(normal);
		
		float indexValue = normal.get(index);
		
		float a = imagePosition.get(index);
		float b = imgIP.get(index);
		
		if(indexValue < 0){
			if(a == b){
				return 0;
			}
			else if(a < b){
				return 1;
			}
			else return -1;
		}
		else{
			if(a == b){
				return 0;
			}
			else if(a < b){
				return -1;
			}
			else return 1;
		}
	}
}
