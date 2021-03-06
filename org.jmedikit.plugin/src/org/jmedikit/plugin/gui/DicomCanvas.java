package org.jmedikit.plugin.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import org.jmedikit.lib.core.BilinearInterpolation;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.ADicomTreeItem;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.core.APlugIn;
import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.image.ImageCube;
import org.jmedikit.lib.image.ROI;
import org.jmedikit.lib.util.Dimension2D;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.gui.tools.ATool;
import org.jmedikit.plugin.io.PlugInClassLoader;
import org.jmedikit.plugin.util.ImageProvider;

/**
 * DicomCanvas ist verantwortlich f�r das Anzeigen der Bilddaten.
 * 
 * @author Rudolf Korb
 *
 */
public class DicomCanvas extends Canvas{
 
	/**
	 * Bedienelement auf dem sich das aktuelle Canvas befindet.
	 * Bedienelement ist eine ge�ffnete DICOM-Serie
	 */
	private ImageViewComposite context;
	
	/**
	 * Referenz auf das ausgew�hlte Element im DicomBrowser
	 */
	private ADicomTreeItem item;
	
	/**
	 * Index des aktuell ausgew�hlten DICOM-Elements.
	 */
	private int index;
	
	/**
	 * maximaler Index, bzw z-Wert bei einer Konvetierung vom aktuellen MPR-Typ nach AXIAL
	 */
	private int maxAxialIndex;
	
	/**
	 * maximaler Index, bzw z-Wert bei einer Konvetierung vom aktuellen MPR-Typ nach CORONAL
	 */
	private int maxCoronalIndex;
	
	/**
	 * maximaler Index, bzw z-Wert bei einer Konvetierung vom aktuellen MPR-Typ nach SAGGITAL
	 */
	private int maxSagittalIndex;
	
	/**
	 * maximaler Index, bzw z-Wert bei aktuellem MPR-Typ
	 */
	private int maxCurrentIndex;
	
	/**
	 * Das aktuell gew�hlte Werkzeug aus der Werkzeugleiste
	 */
	private ATool tool;
	
	/**
	 * DICOM-Bildstapel
	 * Enth�lt initial die Bilder, die im DICOM-Browser gew�hlt wurden
	 * Inhalt kann bei Bedienung ge�ndert werden (zum Beispiel durch PlugIns oder �nderung des MPR-Typs)
	 */
	private ArrayList<AImage> images;
	
	/**
	 * ImageCube behandelt �nderungen des MPR-Typs
	 */
	private ImageCube cube;
	//private MultiplanarReconstruction cube;
	
	/**
	 * Enth�lt das Originalbild des aktuell gew�hlten Index
	 */
	public AImage sourceImage;
	
	/**
	 * Kopie des ersten Bildes im initialen DICOM-Stapel, darf nicht neu vergeben werden.
	 */
	public AImage sampleImage;
	
	/**
	 * Enth�lt die tats�chlich Dimension des aktuell gew�hlten Bildes
	 */
	public Dimension2D<Integer> sourceDimension;
	
	/**
	 * MPR-Typ bei initialisierung des DICOM-Bildstapels, darf nicht neu vergeben werden.
	 */
	public String initialImageOrientationType;
	
	/**
	 * Aktueller MPR-Typ
	 */
	public String imageOrientationType;
	
	/**
	 * enth�lt windowCenter des aktuell aktiven Bildes
	 * @see org.jmedikit.lib.image.AImage#getWindowCenter()
	 */
	public float windowCenter;
	
	/**
	 * enth�lt windowWidth des aktuell aktiven Bildes
	 * @see org.jmedikit.lib.image.AImage#getWindowWidth()
	 */
	public float windowWidth;
	
	/**
	 * Enth�lt den tats�chlichen minimalen Pixelwert
	 */
	public int min;
	
	/**
	 * Enth�lt den tats�chlichen maximalen Pixelwert
	 */
	public int max;
	
	/**
	 * Enth�lt die Beschriftung der Koordinatenachsen entsprechen {@link AImage#getImageOrientationAxis()}
	 */
	public String[] axes;
	
	/**
	 * Mittelpunkt des interpolierten Bildes auf dem Canvas (z.B. nach Skalierung)
	 */
	public Point2D<Integer> imageCenter;
	
	/**
	 * Dimension des interpolierten Bildes auf dem Canvas (z.B. nach Skalierung)
	 */
	public Dimension2D<Integer> imageDimension;
	
	/**
	 * Region Of Interest des aktuell sichtbaren Bildbereichs auf dem Canvas.
	 * ROI ist normalisiert auf Werte zwischen 0 und 1; x = 0; y = 0; widht = 1; height = 1 == Bild ist vollst�ndig sichtbar
	 */
	public ROI roi;
	
	/**
	 * Dimension des Canvaselements nach erstmaligem Zeichnen
	 */
	public Dimension2D<Integer> canvasDimension;
	
	//public Rectangle visibleImageBounds;
	
	/**
	 * Flag ob Canvas zum ersten mal aufgerufen wird
	 */
	private boolean isInitialized;
	
	/**
	 * true wenn X-Richtung ScoutingLines neu gezeichnet werden m�ssen
	 */
	private boolean doXLineUpdate;
	
	/**
	 * true wenn X-Richtung ScoutingLines neu gezeichnet werden m�ssen
	 */
	private boolean doYLineUpdate;
	
	/**
	 * Hintergrundfarbe
	 */
	public Color black;
	
	/**
	 * Fordergrundfarbe
	 */
	public Color white;
	
	/**
	 * Koordinatensystemfarbe
	 */
	public Color yellow;
	
	/**
	 * y Scouting Line Grundfarbe
	 */
	private Color hLineColor;
	
	/**
	 * y Scouting Line Schatten
	 */
	private Color hLineShadow;
	
	/**
	 * y Scouting Line Highlight
	 */
	private Color hLineHighlight;
	
	/**
	 * x Scouting Line Grundfarbe
	 */
	private Color vLineColor;
	
	/**
	 * x Scouting Line Schatten
	 */
	private Color vLineShadow;
	
	/**
	 * x Scouting Line Highlight
	 */
	private Color vLineHighlight;
	
	/**
	 * Farbe f�r Auswahl
	 */
	private Color selectedPointsColor;
	
	//private float alpha;
	//private float beta;
	//private float gamma;
	
	/**
	 * Breite des initialen Bildes. Wird im Konstruktor gesetzt. Darf nicht ge�ndert werden.
	 */
	private int startWidth;
	
	/**
	 * H�he des initialen Bildes. Wird im Konstruktor gesetzt. Darf nicht ge�ndert werden.
	 */
	private int startHeight;
	
	/**
	 * Tats�chliche Breite des aktuell ausgew�hlten Bildes.
	 */
	private int actualWidth;
	
	/**
	 * Tats�chliche H�he des aktuell ausgew�hlten Bildes.
	 */
	private int actualHeight;
	
	/**
	 * RAW x-Index f�r Scouting Lines
	 */
	private int xLineIndex;
	
	/**
	 * RAW y-Index f�r Scouting Lines
	 */
	private int yLineIndex;
	
	/**
	 * Aus RAW index tats�chlich berechneter x-Index f�r Scouting Lines zum Zeichnen der Linie
	 */
	private int xIndex;
	
	/**
	 * Aus RAW index tats�chlich berechneter y-Index f�r Scouting Lines zum Zeichnen der Linie
	 */
	private int yIndex;
	
	/**
	 * Flag ob Auswahl gezeichnet wird
	 */
	private boolean drawSelection;
	
	/**
	 * Flag ob Koordinatensystem etc. gezeichnet wird
	 */
	private boolean drawAnnotations;
	
	/**
	 * Flag ob Scouting Lines gezeichnet werden
	 */
	private boolean drawScoutingLines;
	
	/**
	 * Bilddatei zur Anzeige des MPR-Typs
	 */
	private Image axialImage, coronalImage, sagittalImage;
	
	/**
	 * Bei der Instantiierung wird das Canvas, die Icons und die Listener initialisiert. Das erste Bild wird aus dem Stapel geladen und angezeigt. 
	 * Zus�tzlich werden die grundlegenden Informationen zu den Bilddaten ermittelt
	 * 
	 * @param parent Elternelement
	 * @param style SWT-Style
	 * @param selection der selektierte Knoten im Baum
	 * @param images der 3D-Datensatz
	 * @param pool Bildverwaltung von jMediKit
	 * @param context zugeh�riger ImageViewComposite
	 */
	public DicomCanvas(Composite parent, int style, ADicomTreeItem selection, ArrayList<AImage> images, IResourcePool pool, ImageViewComposite context) {
		super(parent, style);
		
		this.context = context;
		
		item = selection;
		this.images = images;
		cube = new ImageCube(images);

		initializeColors();
		
		axialImage = pool.getImageUnchecked(ImageProvider.AXIAL_W_ICON);
		coronalImage = pool.getImageUnchecked(ImageProvider.CORONAL_W_ICON);
		sagittalImage = pool.getImageUnchecked(ImageProvider.SAGITTAL_W_ICON);
		
		sourceDimension = new Dimension2D<Integer>(0, 0);
		imageCenter = new Point2D<Integer>(0, 0);
		imageDimension = new Dimension2D<Integer>(0, 0);
		canvasDimension = new Dimension2D<Integer>(0, 0);
		
		sourceImage = images.get(0);
		sampleImage = sourceImage;
		sourceDimension.width = sourceImage.getWidth();
		sourceDimension.height = sourceImage.getHeight();
		
		startWidth = sourceDimension.width;
		startHeight = sourceDimension.height;
		
		actualWidth = startWidth;
		actualHeight = startHeight;
		
		windowCenter = sourceImage.getWindowCenter();
		windowWidth = sourceImage.getWindowWidth();
		
		min = sourceImage.getMin();
		max = sourceImage.getMax();
		
		doXLineUpdate = false;
		doYLineUpdate = false;
		
		index = 0;
		setMaxCurrentIndex(item.size());
		
		xLineIndex = 0;
		yLineIndex = 0;
		
		xIndex = 0;
		yIndex = 0;
		
		setDrawSelection(true);
		setDrawAnnotations(true);
		setDrawScoutingLines(true);
		
		axes = sourceImage.getImageOrientationAxis();
		imageOrientationType = sourceImage.getMprType();
		initialImageOrientationType = imageOrientationType;
		
		//mit imageOrientationType wurde der initiale MPR-Typ bestimmt
		//Index kann nun festgelegt werden
		determineMaxIndizes();
		
		addListener(SWT.MouseWheel, mouseWheelListener);
		addListener(SWT.MouseDown, mouseDownListener);
		addListener(SWT.MouseUp, mouseUpListener);
		addListener(SWT.MouseMove, mouseMoveListener);
		addListener(SWT.Paint, paintListener);
		addListener(SWT.MouseEnter, mouseEnterListener);
		addListener(SWT.MouseExit, mouseExitListener);
	}

	/**
	 * Der PaintListener regelt den Zeichenprozess auf dem Canvas
	 * Die Vorgehensweise erfolgt in den Schritten
	 * <ol>
	 * <li>Bild laden und auf einheitliche Gr��e interpolieren</li>
	 * <li>sichtbaren Bildbereich bestimmen</li>
	 * <li>Interpolation des sichtbaren Bereichs</li>
	 * <li>Fensterungswerte berechnen</li>
	 * <li>Bild auf die Zeichenfl�che malen</li>
	 * <li>Zeichnen der zus�tzlichen Informationen f�r den Anwender</li>
	 * </ol>
	 */
	private Listener paintListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			
			//laden des Quellbildes
			sourceImage = images.get(index);
			
			actualWidth = sourceImage.getWidth();
			actualHeight = sourceImage.getHeight();
			
			// bringt die Bilddaten auf eine einheitliche Gr��e
			// Sei Bildstapel (x, y, z) => (512, 512, 22) 
			// dann haben Bilddaten in Coronaler/Sagittaler Ansicht eine Gr��e von (512, 22, 512)
			// dadurch muss das Bild 512 x 22 auf urspr�ngliche Gr��e von 512 x 512 skaliert werden
			if(actualWidth != startWidth || actualHeight != startHeight){
				//resample
				BilinearInterpolation resampleImg = new BilinearInterpolation(sourceImage);
				AImage res = resampleImg.resample(actualWidth, actualHeight, startWidth, startHeight);
				res.setImagePosition(sourceImage.getImagePosition());
				res.setTitle(sourceImage.getTitle());
				res.setPoints(sourceImage.getPoints());
				res.setROIs(sourceImage.getROIs());
				res.setRowImageOrientation(sourceImage.getRowImageOrientation());
				res.setColumnImageOrientation(sourceImage.getColumnImageOrientation());
				res.copySignificantAttributes(sourceImage);
				sourceImage = res;
			}
			
			axes = sourceImage.getImageOrientationAxis();
			imageOrientationType = sourceImage.getMprType();
						
			sourceDimension.width = sourceImage.getWidth();
			sourceDimension.height = sourceImage.getHeight();
			
			//bestimme Koordinaten von Canvas
			Rectangle clientRect = DicomCanvas.this.getClientArea();
			canvasDimension.width = clientRect.width - clientRect.x;
			canvasDimension.height = clientRect.height - clientRect.y;
			
			Image bufferImage = new  Image(DicomCanvas.this.getDisplay(), canvasDimension.width, canvasDimension.height);
			GC buffer = new GC(bufferImage);
			
			Font font = new Font(Display.getCurrent(), "Verdana", 11, SWT.NONE); 
			buffer.setFont(font);
			//initialisierung
			//DicomBild wird beim ersten Aufruf zentriert
			if(!isInitialized){
				isInitialized = true;
				imageCenter.x = canvasDimension.width/2;
				imageCenter.y = canvasDimension.height/2;
				
				imageDimension.width = sourceDimension.width;
				imageDimension.height = sourceDimension.height;
			}
			else{
				buffer = tool.preCalculation(buffer);
			}
			
			//Sichbare Elemente berechnen
			buffer = draw(buffer);
			
			buffer = tool.postCalculation(buffer);
			event.gc.drawImage(bufferImage, 0, 0);
			bufferImage.dispose();
			buffer.dispose();
			font.dispose();
		}
	};
	
	protected GC draw(GC buffer) {
		
		int x = imageCenter.x-imageDimension.width/2;
		int y = imageCenter.y-imageDimension.height/2;
		int width = imageDimension.width;
		int height = imageDimension.height;
		
		Rectangle imageBounds = new Rectangle(x, y, width, height);
		Rectangle newBounds = new Rectangle(x, y, width, height);
		
		roi = new ROI(0f, 0f, 1f, 1f);
		
		if(imageBounds.x < 0){
			//top_left_x ragt ueber den rand hinaus
			newBounds.x = 0;
			newBounds.width = imageBounds.width+imageBounds.x;
			roi.x1 = (float)(Math.abs(imageBounds.x)) / (float)imageBounds.width;
		}
		else{
			newBounds.x = imageBounds.x;
			roi.x1 = 0f;
		}
		
		if(imageBounds.y < 0){
			newBounds.y = 0;
			newBounds.height = imageBounds.height+imageBounds.y;
			roi.y1 = (float)Math.abs(imageBounds.y) / (float)imageBounds.height;
		}
		else{
			newBounds.y = imageBounds.y;
			roi.y1 = 0f;
		}
		
		if(imageBounds.x+imageDimension.width > canvasDimension.width){
			//bild ragt ueber den rechten rand hinaus
			int offset_x = imageBounds.x+imageDimension.width-canvasDimension.width;
			newBounds.width = imageBounds.width-offset_x;
			roi.x2 = (float)newBounds.width / (float)imageBounds.width;
		}
		else {
			//newBounds.width = imageBounds.width;
			roi.x2 = 1.0f;
		}
		
		if(imageBounds.y+imageDimension.height > canvasDimension.height){
			//bild ragt ueber den rechten rand hinaus
			int offset_y = imageBounds.y+imageDimension.height-canvasDimension.height;
			newBounds.height = imageBounds.height-offset_y;
			roi.y2 = (float)newBounds.height / (float)imageBounds.height;
		}
		else {
			//newBounds.height = imageBounds.height;
			roi.y2 = 1.0f;
		}

		
		//Roi ermittelt
		//darauf kann das Bild interpoliert werden
		BilinearInterpolation bilinearInterpolation = new BilinearInterpolation(sourceImage);
		AImage resampled = bilinearInterpolation.resampleROI(roi, sourceDimension.width, sourceDimension.height, imageDimension.width, imageDimension.height);
		resampled.setMinMaxValues(min, max);

		//Nach der Interpolation folgt die Berechnung der Grauwerte
		ImageData data = ImageWindowInterpolation.interpolateImage(resampled, windowCenter, windowWidth, 0, 255);
		Image iimg = new Image(this.getDisplay(), data);

		buffer.setBackground(black);
		buffer.setForeground(white);
		buffer.fillRectangle(0, 0, canvasDimension.width, canvasDimension.height);
		
		//Zeichnet die vom Anwender selektierten Punkte und Regions Of Interest
		if(drawSelection){
			GC selection = new GC(iimg);
			selection.setLineWidth(2);
			selection.setForeground(selectedPointsColor);
			selection.setAlpha(200);
			for(Point2D<Float> p : sourceImage.getPoints()){
				if(roi.x1 < p.x && roi.y1 < p.y){
					int pX = (int)((p.x-roi.x1) * imageDimension.width);
					int pY = (int)((p.y-roi.y1) * imageDimension.height);
					selection.drawLine(pX-5, pY-5, pX+5, pY+5);
					selection.drawLine(pX+5, pY-5, pX-5, pY+5);
				}
			}
			
			for(ROI imgRoi : sourceImage.getROIs()){
				selection.setLineWidth(1);
				selection.setForeground(vLineColor);

				int pXS = (int)((imgRoi.x1-roi.x1) * imageDimension.width);
				int pYS = (int)((imgRoi.y1-roi.y1) * imageDimension.height);
				
				int pXE = (int)((imgRoi.x2-roi.x1) * imageDimension.width);
				int pYE = (int)((imgRoi.y2-roi.y1) * imageDimension.height);
				
				selection.setAlpha(255);
				selection.drawRectangle(pXS, pYS, pXE-pXS, pYE-pYS);
				selection.setAlpha(80);
				selection.fillRectangle(pXS, pYS, pXE-pXS, pYE-pYS);
			}
			selection.dispose();
		}
		
		//�bernimmt das Zeichnen der Orientierungslinien
		if(drawScoutingLines){
			GC scoutingLines = new GC(iimg);
			scoutingLines.setAlpha(75);
			scoutingLines.setLineWidth(2);
			if(yLineIndex != 0){
				if(startHeight != actualHeight && doYLineUpdate){
					//AspectRatio berechnen, damit ScoutingLines entsprechend dem Faktor
					//am richtigen Index gezeichnet werden
					if(startHeight > actualHeight){
						yLineIndex *= (int)((float)startHeight/(float)actualHeight+0.5f);
					}
					else yLineIndex *= (int)((float)actualHeight/(float)startHeight+0.5f);
					doYLineUpdate = false;
				}
				
				float yLineNormalized = (float)yLineIndex/(float)startHeight;
				//int yIndex = 0;
				if(roi.y1 < yLineNormalized){
					float temp = yLineNormalized-roi.y1;
					yIndex = (int) (temp * imageDimension.height);

					
					scoutingLines.setForeground(hLineColor);
					scoutingLines.drawLine(0, yIndex, iimg.getBounds().width, yIndex);
					
					scoutingLines.setLineWidth(1);
					scoutingLines.setForeground(hLineHighlight);
					scoutingLines.drawLine(0, yIndex+1, iimg.getBounds().width, yIndex+1);
					scoutingLines.setForeground(hLineShadow);
					scoutingLines.drawLine(0, yIndex+2, iimg.getBounds().width, yIndex+2);
					scoutingLines.drawLine(0, yIndex-1, iimg.getBounds().width, yIndex-1);
					
					//scoutingLines.dispose();
					int canvasLineYIndex = newBounds.y+yIndex;
					buffer.setForeground(hLineColor);
					buffer.drawLine(0, canvasLineYIndex, newBounds.x, canvasLineYIndex);
					buffer.drawLine(newBounds.width, canvasLineYIndex, canvasDimension.width, canvasLineYIndex);
					buffer.setLineWidth(2);
				}
			}
			
			if(xLineIndex != 0){
				if(startWidth != actualWidth && doXLineUpdate){
					//AspectRatio berechnen, damit ScoutingLines entsprechend dem Faktor
					//am richtigen Index gezeichnet werden
					if(startWidth > actualWidth){
						xLineIndex *= (int)((float)startWidth/(float)actualWidth+0.5f);
					}
					else xLineIndex *= (int)((float)actualWidth/(float)startWidth+0.5f);
					doXLineUpdate = false;
				}
				
				float xLineNormalized = (float)xLineIndex/(float)startWidth;
				//int xIndex = 0;
				if(roi.x1 < xLineNormalized){
					float temp = xLineNormalized-roi.x1;
					//System.out.println("TEMP "+temp);
					xIndex = (int) (temp * imageDimension.width);			
					
					scoutingLines.setForeground(vLineColor);
					scoutingLines.drawLine(xIndex, 0, xIndex, iimg.getBounds().height);
					
					scoutingLines.setLineWidth(1);
					scoutingLines.setForeground(vLineHighlight);
					scoutingLines.drawLine(xIndex+1, 0, xIndex+1, iimg.getBounds().height);
					scoutingLines.setForeground(vLineShadow);
					scoutingLines.drawLine(xIndex+2, 0, xIndex+2, iimg.getBounds().height);
					scoutingLines.drawLine(xIndex-1, 0, xIndex-1, iimg.getBounds().height);
					
					int canvasLineXIndex = newBounds.x+xIndex;
					buffer.setForeground(vLineColor);
					buffer.drawLine(canvasLineXIndex, 0, canvasLineXIndex, newBounds.y);
					buffer.drawLine(canvasLineXIndex, newBounds.height, canvasLineXIndex, canvasDimension.height);
					buffer.setLineWidth(2);
				}
				
				//System.out.println("XINDEX "+xIndex+" STARTDIM "+startWidth+" x "+startHeight+", ACTDIM "+actualWidth+" x "+actualHeight);
			}
			
			scoutingLines.dispose();
		}
		
		buffer.drawImage(iimg, newBounds.x, newBounds.y);

		buffer.setBackground(black);
		buffer.setForeground(white);
		
		//Zeichnen der weiteren Informationen wie Koordinatensystem und Bildgr��e
		if(drawAnnotations){
			String sliceNumber = (index+1)+"/ "+maxCurrentIndex;
			Point textDim = buffer.textExtent(sliceNumber);
			
			int xPos = canvasDimension.width-textDim.x-20;
			int yPos = 20;
			
			buffer.drawText(sliceNumber, xPos, yPos);
			
			String scale = imageDimension.width + " x " + imageDimension.height +"\n( " + (int)(((float)imageDimension.width/(float)sourceImage.getWidth())*100) + "% )";
			//textDim = buffer.textExtent(scale);
			buffer.drawText(scale, 20, 20);
			
			String wcww = "WindowCenter:\t"+windowCenter+"\nWindowWidth:\t"+windowWidth;
			textDim = buffer.textExtent(wcww);
			
			xPos = 20;
			yPos = canvasDimension.height-textDim.y-20;
			
			buffer.drawText(wcww, xPos, yPos);
			
			//Zeichnen des Koordinatensystems
			
			buffer.setForeground(yellow);
			
			String top = axes[0];
			Point textDimTop = buffer.textExtent(top);
			xPos = canvasDimension.width/2-textDimTop.x/2;
			yPos = 10;
			buffer.drawText(top, xPos, yPos);
			
			String right = axes[1];
			Point textDimRight = buffer.textExtent(right);
			xPos = canvasDimension.width-(textDimRight.x+10);
			yPos = canvasDimension.height/2-textDimRight.y/2;
			buffer.drawText(right, xPos, yPos);
			
			String bottom = axes[2];
			Point textDimBottom = buffer.textExtent(bottom);
			xPos = canvasDimension.width/2-textDimBottom.x/2;
			yPos = canvasDimension.height-(textDimBottom.y+10);
			buffer.drawText(bottom, xPos, yPos);
			
			String left = axes[3];
			Point textDimLeft = buffer.textExtent(left);
			xPos = 10;
			yPos = canvasDimension.height/2-textDimLeft.y/2;
			buffer.drawText(left, xPos, yPos);
			
			setMprIcon(buffer, sourceImage.getMprType());
		}

		iimg.dispose();
		return buffer;
	}
	

	private Listener mouseWheelListener = new Listener(){
		
		@Override
		public void handleEvent(Event event) {
			tool.handleMouseWheel(event);
			DicomCanvas.this.redraw();
		};
	};
	
	private Listener mouseDownListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			DicomCanvas.this.setFocus();
			tool.handleMouseDown(event);
			DicomCanvas.this.redraw();
		}
	};
	
	private Listener mouseUpListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			tool.handleMouseUp(event);
			DicomCanvas.this.redraw();
		}
	};
	
	private Listener mouseMoveListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			tool.handleMouseMove(event);
			DicomCanvas.this.redraw();
		}
	};
	
	private Listener mouseEnterListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			tool.handleMouseEnter(event);
			DicomCanvas.this.redraw();
		}
		
	};
	
	private Listener mouseExitListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			tool.handleMouseExit(event);
			DicomCanvas.this.redraw();
		}
		
	};
	
	/*public AImage loadImage(int i){
		//Test, ob Bilddaten bereits geladen sind
		AImage img;
		try {
			img = images.get(i);
		} catch (Exception e) {
			DicomObject toDraw;
			if(item.getLevel() == ADicomTreeItem.TREE_OBJECT_LEVEL){
				toDraw = (DicomObject)item;
			}
			else {
				toDraw = (DicomObject) item.getChild(i);
			}
			img = toDraw.getImage(0);
			//images.add(i, img);
			return img;
		}
		return img;
	}*/
	
	/*public AbstractImage loadImage(int i){
		//Test, ob Bilddaten bereits geladen sind
		AbstractImage img = images.get(i);
		if(img == null){
			DicomObject toDraw;
			if(item.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
				toDraw = (DicomObject)item;
			}
			else {
				toDraw = (DicomObject) item.getChild(i);
			}
			img = toDraw.getImage(0);
			//images.add(i, img);
			return img;
		}
		else return img;
	}*/
	
	/**
	 * Gibt den gew�hlten Baumknoten zur�ck
	 */
	public ADicomTreeItem getItem() {
		return item;
	}

	/*public void setItem(DicomTreeItem item) {
		this.item = item;
	}*/

	/**
	 * Gibt den Bildstapel als Liste zur�ck
	 * 
	 * @return Bildstapel
	 */
	public ArrayList<AImage> getImages() {
		return images;
	}

	/**
	 * Gibt das aktuelle Bild aus dem Stapel zur�ck. Unver�ndertes Original
	 * @return
	 */
	public AImage getImage(){
		return images.get(index);
	}
	
	/**
	 * Gibt das Referenzbild zur�ck
	 * 
	 * @return
	 */
	public AImage getSampleImage() {
		return sampleImage;
	}

	/**
	 * Setzt das Werkzeug des Canvas
	 * 
	 * @param tool
	 */
	public void setTool(ATool tool){
		this.tool = tool;
	}
	
	/**
	 * Setzt den z-Wert im Bildstapel. Ist der index ausserhalb des g�ltigen Bereichs wird nichts gemacht.
	 * 
	 * @param index
	 */
	public void setIndex(int index){
		if(index >= 0 && index < images.size()){
			this.index = index;
			this.redraw();
		}
	}
	
	/**
	 * Gibt das Wurzelelement der Benutzeroberfl�che zur Bildanzeige zur�ck.
	 * @return
	 */
	public ImageViewComposite getContext(){
		return context;
	}
	
	/**
	 * Gibt die aktuelle Position der z-Richtung im Bildstapel zur�ck
	 * 
	 * @return
	 */
	public int getIndex(){
		return index;
	}
	
	/**
	 * Vergibt einen neuen Bilddatensatz und zeichnet die Zeichenfl�che neu
	 * 
	 * @param images
	 */
	public void setImages(ArrayList<AImage> images){
		this.images = images;
		this.redraw();
	}
	
	/**
	 * Diese Methode l�st die Ebenenrekonstruktion aus und wird aufgerufen, wenn der Nutzer den
	 * Button der jeweiligen Rekonstruktion dr�ckt
	 * 
	 * @param newOrientation String der zu rekonstruierenden Darstellung
	 */
	public void recalculateImages(String newOrientation){
		isInitialized = false;
		index = 0;

		xLineIndex = 0;
		yLineIndex = 0;

		if(newOrientation.equals(AImage.AXIAL)){
			images = cube.calculateAxialView();
		}
		else if(newOrientation.equals(AImage.CORONAL)){
			images = cube.calculateCoronalView();
		}
		else if(newOrientation.equals(AImage.SAGITTAL)){
			images = cube.calculateSagittalView();
		}
		this.redraw();
	}
	
	/*public void recalculateImages(String newOrientation){
		isInitialized = false;
		//System.out.println("NEW "+newOrientation);
		initialImageOrientationType = newOrientation;
		//index = 0;
		if(newOrientation.equals(AbstractImage.AXIAL)){
			sourceImage = cube.calculateAxialView(index);
		}
		else if(newOrientation.equals(AbstractImage.CORONAL)){
			sourceImage = cube.calculateCoronalView(index);
			System.out.println("SI "+sourceDimension.toString());
		}
		else if(newOrientation.equals(AbstractImage.SAGITTAL)){
			sourceImage = cube.calculateSagittalView(index);
		}
		this.redraw();
	}*/
	
	public int getImageStackSize(){
		return item.size();
	}
	
	/*public void setAngles(float alpha, float beta, float gamma){
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		
		this.redraw();
	}*/
	
	/**
	 * Gibt die Koordinaten des Bildmittelpunkts als normalisierte Koordinaten zur�ck
	 * @return
	 */
	public Point2D<Float> getNormalizedImageCenter(){
		float x = (float)imageCenter.x / (float)canvasDimension.width;
		float y = (float)imageCenter.y / (float)canvasDimension.height;
		return new Point2D<Float>(x, y);
	}

	private GC setMprIcon(GC buffer, String mprType){
		if(mprType.equals(AImage.AXIAL)){
			buffer.drawImage(axialImage, canvasDimension.width-axialImage.getBounds().width-20, canvasDimension.height-axialImage.getBounds().height-20);
		}
		else if(mprType.equals(AImage.CORONAL)){
			buffer.drawImage(coronalImage, canvasDimension.width-coronalImage.getBounds().width-20, canvasDimension.height-coronalImage.getBounds().height-20);
		}
		else if(mprType.equals(AImage.SAGITTAL)){
			buffer.drawImage(sagittalImage, canvasDimension.width-sagittalImage.getBounds().width-20, canvasDimension.height-sagittalImage.getBounds().height-20);
		}
		return buffer;
	}
	
	private void initializeColors(){
		black = new Color(this.getDisplay(), new RGB(0, 0, 0));
		white = new Color(this.getDisplay(), new RGB(255, 255, 255));
		yellow = new Color(this.getDisplay(), new RGB(250, 250, 210));
		hLineColor = new Color(this.getDisplay(), new RGB(0, 255, 255));
		hLineShadow = new Color(this.getDisplay(), new RGB(0, 150, 150));
		hLineHighlight = new Color(this.getDisplay(), new RGB(180, 255, 255));
		vLineColor = new Color(this.getDisplay(), new RGB(255, 255, 0));
		vLineShadow = new Color(this.getDisplay(), new RGB(150, 150, 0));
		vLineHighlight = new Color(this.getDisplay(), new RGB(255, 255, 180));
		selectedPointsColor = new Color(this.getDisplay(), new RGB(187, 74, 75));
	}
	
	public int getMaxAxialIndex() {
		return maxAxialIndex;
	}

	public void setMaxAxialIndex(int maxAxialIndex) {
		this.maxAxialIndex = maxAxialIndex;
	}

	public int getMaxCoronalIndex() {
		return maxCoronalIndex;
	}

	public void setMaxCoronalIndex(int maxCoronalIndex) {
		this.maxCoronalIndex = maxCoronalIndex;
	}

	public int getMaxSagittalIndex() {
		return maxSagittalIndex;
	}

	public void setMaxSagittalIndex(int maxSagittalIndex) {
		this.maxSagittalIndex = maxSagittalIndex;
	}

	public int getMaxCurrentIndex() {
		return maxCurrentIndex;
	}

	public void setMaxCurrentIndex(int maxCurrentIndex) {
		this.maxCurrentIndex = maxCurrentIndex;
	}

	/**
	 * Bestimmt den maximal m�glichen Index f�r eine Konvertierung der Ebenen
	 * Beispiel f�r einen DICOM-Stapel der Gr��e 400 x 300 x 250 (x, y, z)
	 * Wenn der aktuelle Typ AXIAL ist, ist der Index des DICOM-Bildstapels nach CORONAL 300
	 * Wenn der aktuelle Typ AXIAL ist, ist der Index des DICOM-Bildstapels nach SAGITTAL 400
	 * 
	 */
	private void determineMaxIndizes(){
		if(imageOrientationType.equals(AImage.AXIAL)){
			maxAxialIndex = item.size();
			maxCoronalIndex = startHeight;
			maxSagittalIndex = startWidth;
		}
		if(imageOrientationType.equals(AImage.CORONAL)){
			maxAxialIndex = startHeight;
			maxCoronalIndex = item.size();
			maxSagittalIndex = startWidth;
		}
		if(imageOrientationType.equals(AImage.SAGITTAL)){
			maxAxialIndex = startHeight;
			maxCoronalIndex = startWidth;
			maxSagittalIndex = item.size();
		}
	}
	
	
	public int getxLineIndex() {
		return xLineIndex;
	}

	public void setxLineIndex(int xLineIndex) {
		this.xLineIndex = xLineIndex;
	}

	public int getyLineIndex() {
		return yLineIndex;
	}

	public void setyLineIndex(int yLineIndex) {
		this.yLineIndex = yLineIndex;
	}

	public boolean getDoXLineUpdate() {
		return doXLineUpdate;
	}

	public void setDoXLineUpdate(boolean doLineUpdate) {
		this.doXLineUpdate = doLineUpdate;
	}
	
	public boolean getDoYLineUpdate() {
		return doYLineUpdate;
	}

	public void setDoYLineUpdate(boolean doLineUpdate) {
		this.doYLineUpdate = doLineUpdate;
	}
	
	public int getActualImageWidth(){
		return actualWidth;
	}
	
	public int getActualImageHeight(){
		return actualHeight;
	}

	public boolean isDrawAnnotations() {
		return drawAnnotations;
	}

	public void setDrawAnnotations(boolean drawAnnotations) {
		this.drawAnnotations = drawAnnotations;
	}

	public boolean isDrawSelection() {
		return drawSelection;
	}

	public void setDrawSelection(boolean drawSelection) {
		this.drawSelection = drawSelection;
	}

	public boolean isDrawScoutingLines() {
		return drawScoutingLines;
	}

	public void setDrawScoutingLines(boolean drawScoutingLines) {
		this.drawScoutingLines = drawScoutingLines;
	}

	/**
	 * Diese Methode wird aufgerufen, wenn im Hauptmen� eine Erweiterung ausgew�hlt wird.
	 * 
	 * @param mainClassName Name der Klasse, die von APlugIn ableitet
	 */
	public void runPlugIn(String mainClassName) {
		PlugInClassLoader loader = PlugInClassLoader.getInstance();
		final APlugIn plugin = (APlugIn) loader.instantiate(mainClassName);
		System.out.println("plugin "+plugin.getPlugInType());
		//Neuer Thread, damit UI nicht blockiert wird
		Display.getCurrent().syncExec(new Runnable() {
			
			@Override
			public void run(){
				try {
					new ProgressMonitorDialog(ImageViewPart.getPartShell()).run(false, false, new PlugInRunner(plugin.getClass().getName(), DicomCanvas.this, plugin, images));
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});	

		redraw();
	}
}
