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
	 * PixelData wird als Unsigned gespeichert. 
	 */
	public static final int TYPE_UNSIGNED = 0;
	
	/**
	 * PixelData ist im Zweierkomplement gespeichert
	 */
	public static final int TYPE_SIGNED = 1;
	
	/**
	 * Titel des Bildes
	 */
	protected String title;
	
	/**
	 * Bild-Breite - x
	 */
	protected int width;
	
	/**
	 * Bild-Höhe - y
	 */
	protected int height;
	
	/**
	 * <p>Wert = 1 für Grauwertbilder</p>
	 * Wert > 1 für Farbbilder
	 */
	protected int samplesPerPixel;
	
	/**
	 * Aspect Ratio width/height
	 */
	protected float aspectRatio;
	
	/**
	 * Minimaler tatsächlicher Pixelwert. Enthält das Bild Werte von 34 - 187, entspricht min dem Wert 34. 
	 */
	protected int min;
	
	/**
	 * Maximaler tatsächlicher Pixelwert. Enthält das Bild Werte von 34 - 187, entspricht min dem Wert 187.
	 */
	protected int max;
	
	/**
	 * True wenn minimalen und maximalen Pixelwerte eines Bildes via {@link AImage#determineMinMaxValues(Object)} berechnet wurden
	 */
	protected boolean extrema;
	
	/**
	 * Gerätunabhängige Pixelwerte werden mit der Formel
	 * <p><b>rescaleSlope * storedValue + rescale Intercept</b></p>
	 * berechnet
	 */
	protected float rescaleSlope;
	
	/**
	 * Beschreibung @see {@link AImage#rescaleSlope}
	 */
	protected float rescaleIntercept;
	
	/**
	 * <p>WindowWidth und WindowCenter wird verwendet, um Grauwerte auf einen Bereich von
	 * 0 - 255 zu mappen. Medizinische Bilder können bis zu 2^16 verschiedene Grauwerte
	 * enthalten.</p>
	 * 
	 * <p>Wenn WindowWidth = 200 und WindowCenter = 0,
	 * werden alle Grauwerte zwischen -100 und 100 gemappt.</p>
	 * Werte kleiner -100 werden weiß dargestellt
	 * Werte größer 100 werden schwarz dargestellt
	 * Der Bereich zwischen -100 und 100 wird auf werte zwischen 1 und 254 gemappt
	 * 
	 */
	protected float windowWidth;
	
	/**
	 * Beschreibung @see {@link AImage#windowWidth}
	 */
	protected float windowCenter;
	
	/**
	 * Reihenvektor aus ImageOrientation
	 */
	protected Vector3D<Float> rowVector;
	
	/**
	 * Spaltenvektor aus ImageOrientation
	 */
	protected Vector3D<Float> columnVector;
	
	/**
	 * Vektor aus ImagePosition
	 */
	protected Vector3D<Float> imagePosition;
	
	/**
	 * Vektor mit dem Abstand der Pixel in mm in x- und y-Richtung
	 */
	protected Point2D<Float> pixelSpacing;
	
	/**
	 * Beschreibt den Typ des Bildes
	 * <ul>
	 * <li>Byte => 0</li>
	 * <li>Unsigned Byte => 1</li>
	 * <li>Short => 2</li>
	 * <li>Unsigned Short => 3</li>
	 * <li>Int => 4</li>
	 * <li>Unsigned Int => 5</li>
	 * </ul>
	 */
	protected int imageType;
	
	/**
	 * Ansichtentyp enthält einen Wert von
	 * <ul>
	 * <li>AXIAL - für die xy Ebene</li>
	 * <li>CORONAL - für die xz Ebene</li>
	 * <li>SAGITTAL - für die yz Ebene</li>
	 * <li>OBLIQUE - schiefe Ebene</li>
	 * <li>DEFAULT - keine Informationen vorhanden</li>
	 * </ul>
	 */
	protected String mprType;
	
	/**
	 * Vom Nutzer gewählte Region Of Interests mit dem ROIWerkzeug
	 */
	protected ArrayList<ROI> rois;
	
	/**
	 * Vom Nutzer gewählte Punkte mit dem Punktauswahlwerkzeug
	 */
	protected ArrayList<Point2D<Float>> selectedPoints;
	
	/**
	 * Dieses Datenelement dient zur Prüfung, ob Reihen- und Spaltenvektor des Bildes gesetzt sind. Wird zur Prüfung auf signifikante Werte
     * vor allem bei Bilder ohne Quelldatei verwendet. Werden neue Bilder erzeugt, müssen Orientierungswerte wie ImageOrientation und ImagePosition
     * explizit vom Anwender gesetzt werden.
	 */
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
		rois = new ArrayList<ROI>();
		//roi = new ROI(0f, 0f, 0f, 0f);
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
	

	public int getImageType(){
		return imageType;
	}
	
	public int getSamplesPerPixel() {
		return samplesPerPixel;
	}

	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public float getAspectRatio(){
		return aspectRatio;
	}
	
	public String getMprType() {
		return mprType;
	}

	/**
	 * Setzt die Ebenendarstellung eines Bildes. Der Parameter muss einen der folgenden Werte haben, sonst wird als Typ <b>OBLIQUE</b> gesetzt.
	 * 
	 * <ul>mprType
	 * <li><b>AXIAL</b></li>
	 * <li><b>CORONAL</b></li>
	 * <li><b>SAGITTAL</b></li>
	 * </ul>
	 * 
	 * @param mprType zu setzender Ebenentyp
	 */
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

	/**
	 * Setzt die Datenelemente {@link AImage#rowVector} und {@link AImage#rowVector} aus einem Float-Array der Länge 6. Die ersten Elemente 0 - 2 repräsentieren
	 * den Reihenvektor, die Elemente 3 - 5 stellen den Spaltenvektor bereit
	 * 
	 * @param vectors muss die Länge 6 haben
	 */
	public void setImageOrientation(float[] vectors){
		if(vectors.length == 6){
			rowVector.setVector(vectors[0], vectors[1], vectors[2]);
			columnVector.setVector(vectors[3], vectors[4], vectors[5]);
			initializedOrientation = true;
		}
		else throw new IllegalArgumentException("Vectors must have 6 values: got "+vectors.length);
	}
	
	/**
	 * Setzt die Position des Bildes im Raum mit einem Float-Array der Länge 3
	 * 
	 * @param vector muss die Länge 3 haben
	 */
	public void setImagePosition(float[] vector){
		if(vector.length == 3){
			imagePosition.setVector(vector[0], vector[1], vector[2]);
			initializedOrientation = true;
		}
		else throw new IllegalArgumentException("Vector must have 3 values: got "+vector.length);
	}
	
	/**
	 * Setzt die Position des Bildes im Raum mit einem Vector3D<Float>
	 * 
	 * @param vector muss die Länge 3 haben
	 */
	public void setImagePosition(Vector3D<Float> v){
		initializedOrientation = true;
		imagePosition = v;
	}
	
	/**
	 * Setzt den Pixelabstand mit einem Float-Array der Länge 2
	 * @param point muss die Länge 2 haben
	 */
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
	
	/**
	 * Gibt den minimal möglichen Pixelwert zurück. Entspricht nicht dem tatsächlichen minimalem Pixelwert eines Bildes.
	 * Werte sind zum Beispiel für
	 * <ul>
	 * <li>UnsignedByteImage => Min = 0, Max = 2^8-1 = 255</li>
	 * <li>ShortImage => Min = Short.MIN_VALUE, Max = Short.MAX_VALUE</li>
	 * </ul>
	 * @return minimal möglicher Pixelwert
	 */
	public abstract int getMinValue();
	
	/**
	 * Gibt den maximal möglichen Pixelwert zurück. Entspricht nicht dem tatsächlichen maximalem Pixelwert eines Bildes.
	 * Werte sind zum Beispiel für
	 * <ul>
	 * <li>UnsignedByteImage => Min = 0, Max = 2^8-1 = 255</li>
	 * <li>ShortImage => Min = Short.MIN_VALUE, Max = Short.MAX_VALUE</li>
	 * </ul>
	 * @return maximal möglicher Pixelwert
	 */
	public abstract int getMaxValue();
	
	/**
	 * @see {@link AImage#min}
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @see {@link AImage#min}
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * @see {@link AImage#max}
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @see {@link AImage#max}
	 */
	public void setMax(int max) {
		this.max = max;
	}
	
	/**
	 * @see {@link AImage#min} und {@link AImage#max}
	 */
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
	
	public ROI getROI(int index){
		return rois.get(index);
	}
	
	public void addROI(ROI roi){
		rois.add(roi);
	}
	
	public void removeROI(int index){
		rois.remove(index);
	}
	
	public void deleteROIs(){
		rois.clear();
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
	 * Prüft ob {@link AImage#min} und {@link AImage#max} eines Bildes ermittelt wurden
	 * 
	 * @return true wenn die Werte gesetzt sind
	 */
	public boolean extremaAssigned(){
		return extrema;
	}
	
	/**
	 *Prüft, ob ImageOrientation mit Reihen- und Spaltenvektor oder ImagePosition gesetzt sind
	 * @return true wenn einer oder alle Werte gesetzt sind
	 */
	public boolean getInitializedOrientation(){
		return initializedOrientation;
	}
	
	/**
	 * Gibt den Pixelwert der Koordinaten(x,y) eines Bildes als Integer zurück. Ein cast von niedrigem Typ auf höheren Typ ist unproblematisch.
	 * 
	 * @param x x Location
	 * @param y	y Location
	 * @return den Pixelwert der Koordinaten (x, y)
	 */
	public abstract int getPixel(int x, int y);
	
	
	/**
	 * Setzt einen einzelnen Pixelwert an den entsprechenden Koordinaten
	 */
	public abstract void setPixel(int x, int y, int value);
	
	/**
	 * Gibt ein Array der Pixelwerte des Bildes zurück. Der Arraytyp ist abhängig vom konkreten Bildtyp.
	 * 
	 * @return numerisches Array mit den Pixelwerten
	 */
	public abstract Object getPixels();
	
	/**
	 * Findet den größten Wert eines Byte-Arrays und gibt diesen zurück
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
	 * Findet den kleinsten Wert eines Byte-Arrays und gibt diesen zurück
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
	 * Findet den größten Wert eines Short-Arrays und gibt diesen zurück
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
	 * Findet den kleinsten Wert eines Short-Arrays und gibt diesen zurück
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
	 * Findet den größten Wert eines Integer-Arrays und gibt diesen zurück
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
	 * Findet den kleinsten Wert eines Integer-Arrays und gibt diesen zurück
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
	 * Findet Minimum und Maximum eines Numerischen Arrays und setzt die Werte im AImage Objekt
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
	}
	
	/**
	 * Konvertiert einen UnsignedShort Wert gespeichert in einem Integer zu einem Short-Wert
	 * 
	 * @param toConvert zu konvertierender Wert
	 * @return konvertierten Wert
	 */
	public static short unsignedShortToSignedShort(int toConvert){
		return (short)toConvert;
	}
	
	/**
	 * Konvertiert einen Short-Wert zu einem UnsignedShort-Wert
	 * 
	 * @param toConvertzu konvertierender Wert
	 * @return konvertierten Wert
	 */
	public static int signedShortToUnsignedShort(short toConvert){
		return toConvert & 0xFFFF;
	}
	
	/*public static void printPixelValues(String path, AImage img){
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
	}*/
	
	/**
	 * Berechnet die dominante Koordinatenachse eines Reihen- oder Spaltenvektors aus ImageOrientation und gibt die
	 * Achsenbeschriftung der dominanten Achse zurück. Der Rückgabewert enthält
	 * 
	 * <ul>
	 * <li>([0] => A, [1] => P) | ([0] => A, [1] => P)</li>
	 * <li>([0] => L, [1] => R) | ([0] => R, [1] => L)</li>
	 * <li>([0] => F, [1] => H) | ([0] => H, [1] => F)</li>
	 * </ul>
	 * 
	 * 
	 * @param x 
	 * @param y
	 * @param z
	 * @return Beschriftung der Koordinatenachsen als String-Array der Länge 2
	 */
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
	 * Bestimmt die Ebenendarstellung({@link AImage#mprType}) des Bildes und berechnet die Koordinatenachsen des Bildes und gibt diese als String-Array der Länge 4 zurück. Die Beschriftung hat die Form:
	 * 
	 * <ul> String-Array[4]
	 * 	<li>[0] => top</li>
	 * <li>[1] => right</li>
	 * <li>[2] => bottom</li>
	 * <li>[3] => left</li>
	 * </ul>
	 * 
	 * @see <a target="_blank" href="http://www.itk.org/pipermail/insight-users/2005-March/012246.html">http://www.itk.org/pipermail/insight-users/2005-March/012246.html</a>
	 * @return
	 */
	public String[] getImageOrientationAxis(){
		
		String[] axes = new String[4];
		
		// Berechnet aus dem Reihenvektor und Spaltenvektor die dominante Achse, die in das Koordinatensystem gezeichnet wird
		String[] row = getDominantAxis(rowVector.x, rowVector.y, rowVector.z);
		String[] col = getDominantAxis(columnVector.x, columnVector.y, columnVector.z);
		
		// Top -> Right -> Bottom -> Left
		axes[0] = col[1]; //Top negative Seite
		axes[1] = row[0]; //Right positive Seite
		axes[2] = col[0]; //Bottom positive Seite
		axes[3] = row[1]; //Left negative Seite
		
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

		return axes;
	}
	
	
	/**
	 * Bestimmt anhand des Reihen- und Spaltenvektors aus dem DICOM-Tag ImageOrientation die Ebenendarstellung({@link AImage#mprType}) eines Biles.
	 * 
	 * @param rowVector Reihenvektor
	 * @param columnVector Spaltenvektor
	 * @return Ebenentyp als String
	 */
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
	
	/**
	 * Kopiert signifikante Werte aus dem Bild img. Zu diesen Werten gehören
	 * <ul>
	 * <li>Der Ebenentyp</li>
	 * <li>Window Center</li>
	 * <li>Window Width</li>
	 * <li>tatsächlicher minimaler Pixelwert</li>
	 * <li>tatsächlicher maximaler Pixelwert</li>
	 * <li>Pixel Spacing</li>
	 * <li>Rescale Intercept</li>
	 * <li>Rescale Slope</li>
	 * </ul>
	 * 
	 * @param img Bild dessen Werte kopiert werden
	 */
	public void copySignificantAttributes(AImage img){
		mprType = img.getMprType();
		//pixelSpacing = img.getPixelSpacing();
		windowCenter = img.windowCenter;
		windowWidth = img.windowWidth;
		min = img.min;
		max = img.max;
		pixelSpacing = img.pixelSpacing;
		rescaleIntercept = img.rescaleIntercept;
		rescaleSlope = img.rescaleSlope;
	}
	
	/**
	 * Vergleicht das Bild mit dem Bild img entlang der Normalen, um eine Sortierung 
	 * der Bilder im Raum zu ermöglichen.
	 */
	public int compareTo(AImage img){
		
		Vector3D<Float> imgIP = img.getImagePosition();
		
		Vector3D<Float> normal = Vector3D.crossProduct(rowVector, columnVector);
		
		int index = ImageCube.getDominantIndex(normal);
		
		float indexValue = normal.get(index);
		
		float a = imagePosition.get(index);
		float b = imgIP.get(index);
		System.out.println(indexValue + " "+normal.toString()+" "+columnVector.toString()+" "+rowVector.toString());
		if(indexValue < 0){
			if(a == b){
				return 0;
			}
			else if(a < b){
				return -1;
			}
			else return 1;
		}
		else{
			if(a == b){
				return 0;
			}
			else if(a < b){
				return 1;
			}
			else return -1;
		}
	}

	/**
	 * Gibt die vom Nutzer markierten Regions Of Interest zurück
	 * 
	 * @return ROIs
	 */
	public ArrayList<ROI> getROIs() {
		return rois;
	}
	
	/**
	 * Vergibt die Regions Of Interest
	 * 
	 * @param rois
	 */
	public void setROIs(ArrayList<ROI> rois){
		this.rois = rois;
	}
}
