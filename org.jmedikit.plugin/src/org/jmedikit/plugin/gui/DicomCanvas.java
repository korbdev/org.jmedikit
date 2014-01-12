package org.jmedikit.plugin.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.eclipse.e4.tools.services.IResourcePool;
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


public class DicomCanvas extends Canvas{
 
	private ImageViewComposite context;
	
	private ADicomTreeItem item;
	
	private int index;
	
	private int maxAxialIndex;
	private int maxCoronalIndex;
	private int maxSagittalIndex;
	
	private int maxCurrentIndex;
	
	private ATool tool;
	
	private ArrayList<AImage> images;
	
	private ImageCube cube;
	//private MultiplanarReconstruction cube;
	
	public AImage sourceImage;
	public AImage sampleImage;
	public Dimension2D<Integer> sourceDimension;
	public String initialImageOrientationType;
	public String imageOrientationType;
	
	public float windowCenter;
	public float windowWidth;
	
	public int min;
	public int max;
	
	public String[] axes;
	
	public Point2D<Integer> imageCenter;
	public Dimension2D<Integer> imageDimension;
	public ROI roi;
	public Dimension2D<Integer> canvasDimension;
	public Rectangle visibleImageBounds;
	
	private boolean isInitialized;
	private boolean doXLineUpdate;
	private boolean doYLineUpdate;
	
	public Color black;
	public Color white;
	public Color yellow;
	
	private Color hLineColor;
	private Color hLineShadow;
	private Color hLineHighlight;
	
	private Color vLineColor;
	private Color vLineShadow;
	private Color vLineHighlight;
	
	private Color selectedPointsColor;
	
	//private float alpha;
	//private float beta;
	//private float gamma;
	
	private int startWidth;
	private int startHeight;
	
	private int actualWidth;
	private int actualHeight;
	
	private int xLineIndex;
	private int yLineIndex;
	
	private int xIndex;
	private int yIndex;
	
	private boolean drawSelection;
	private boolean drawAnnotations;
	private boolean drawScoutingLines;
	
	private Image axialImage, coronalImage, sagittalImage;
	
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

	private Listener paintListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			
			sourceImage = images.get(index);
			
			actualWidth = sourceImage.getWidth();
			actualHeight = sourceImage.getHeight();
			
			if(actualWidth != startWidth || actualHeight != startHeight){
				//resample
				BilinearInterpolation resampleImg = new BilinearInterpolation(sourceImage);
				AImage res = resampleImg.resample(actualWidth, actualHeight, startWidth, startHeight);
				res.setImagePosition(sourceImage.getImagePosition());
				res.setTitle(sourceImage.getTitle());
				res.setPoints(sourceImage.getPoints());
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
		//System.out.println("BufferDrawing");
		//System.out.println(sourceImage.getHeight());
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
			roi.x = (float)(Math.abs(imageBounds.x)) / (float)imageBounds.width;
		}
		else{
			newBounds.x = imageBounds.x;
			roi.x = 0f;
		}
		
		if(imageBounds.y < 0){
			newBounds.y = 0;
			newBounds.height = imageBounds.height+imageBounds.y;
			roi.y = (float)Math.abs(imageBounds.y) / (float)imageBounds.height;
		}
		else{
			newBounds.y = imageBounds.y;
			roi.y = 0f;
		}
		
		if(imageBounds.x+imageDimension.width > canvasDimension.width){
			//bild ragt ueber den rechten rand hinaus
			int offset_x = imageBounds.x+imageDimension.width-canvasDimension.width;
			newBounds.width = imageBounds.width-offset_x;
			roi.width = (float)newBounds.width / (float)imageBounds.width;
		}
		else {
			//newBounds.width = imageBounds.width;
			roi.width = 1.0f;
		}
		
		if(imageBounds.y+imageDimension.height > canvasDimension.height){
			//bild ragt ueber den rechten rand hinaus
			int offset_y = imageBounds.y+imageDimension.height-canvasDimension.height;
			newBounds.height = imageBounds.height-offset_y;
			roi.height = (float)newBounds.height / (float)imageBounds.height;
			//System.out.println("Offset_y "+offset_y);
		}
		else {
			//newBounds.height = imageBounds.height;
			roi.height = 1.0f;
		}
		
		visibleImageBounds = newBounds;
		
		//Roi ermittelt
		BilinearInterpolation bilinearInterpolation = new BilinearInterpolation(sourceImage);
		AImage resampled = bilinearInterpolation.resampleROI(roi, sourceDimension.width, sourceDimension.height, imageDimension.width, imageDimension.height);
		resampled.setMinMaxValues(min, max);

		
		ImageData data = ImageWindowInterpolation.interpolateImage(resampled, windowCenter, windowWidth, 0, 255);
		Image iimg = new Image(this.getDisplay(), data);

		buffer.setBackground(black);
		buffer.setForeground(white);
		buffer.fillRectangle(0, 0, canvasDimension.width, canvasDimension.height);
		
		if(drawSelection){
			//System.out.println(sourceImage.getTitle());
			GC selection = new GC(iimg);
			selection.setLineWidth(2);
			selection.setForeground(selectedPointsColor);
			for(Point2D<Float> p : sourceImage.getPoints()){
				System.out.println(roi.toString()+" "+p.toString());
				if(roi.x < p.x && roi.y < p.y){
					int pX = (int)((p.x-roi.x) * imageDimension.width);
					int pY = (int)((p.y-roi.y) * imageDimension.height);
					System.out.println("DRAW "+pX+" x "+pY);
					selection.drawLine(pX-5, pY-5, pX+5, pY+5);
					selection.drawLine(pX+5, pY-5, pX-5, pY+5);
				}
			}
			selection.dispose();
		}
		
		if(drawScoutingLines){
			GC scoutingLines = new GC(iimg);
			scoutingLines.setLineWidth(2);
			if(yLineIndex != 0){
				if(startHeight != actualHeight && doYLineUpdate){
					if(startHeight > actualHeight){
						yLineIndex *= (int)((float)startHeight/(float)actualHeight+0.5f);
					}
					else yLineIndex *= (int)((float)actualHeight/(float)startHeight+0.5f);
					doYLineUpdate = false;
				}
				
				float yLineNormalized = (float)yLineIndex/(float)startHeight;
				//int yIndex = 0;
				if(roi.y < yLineNormalized){
					float temp = yLineNormalized-roi.y;
					//System.out.println("TEMP "+temp);
					yIndex = (int) (temp * imageDimension.height);
					
					//GC scoutingLines = new GC(iimg);
					
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
				
				//System.out.println("YINDEX "+yIndex+" STARTDIM "+startWidth+" x "+startHeight+", ACTDIM "+actualWidth+" x "+actualHeight);
			}
			
			if(xLineIndex != 0){
				if(startWidth != actualWidth && doXLineUpdate){
					if(startWidth > actualWidth){
						xLineIndex *= (int)((float)startWidth/(float)actualWidth+0.5f);
					}
					else xLineIndex *= (int)((float)actualWidth/(float)startWidth+0.5f);
					doXLineUpdate = false;
				}
				
				float xLineNormalized = (float)xLineIndex/(float)startWidth;
				//int xIndex = 0;
				if(roi.x < xLineNormalized){
					float temp = xLineNormalized-roi.x;
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
	
	public AImage loadImage(int i){
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
	}
	
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
	
	
	public ADicomTreeItem getItem() {
		return item;
	}

	/*public void setItem(DicomTreeItem item) {
		this.item = item;
	}*/

	public ArrayList<AImage> getImages() {
		return images;
	}

	public AImage getImage(){
		return images.get(index);
	}
	
	public AImage getSampleImage() {
		return sampleImage;
	}

	public void setTool(ATool tool){
		this.tool = tool;
	}
	
	public void setIndex(int index){
		if(index >= 0 && index < images.size()){
			this.index = index;
			this.redraw();
		}
	}
	
	public ImageViewComposite getContext(){
		return context;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setImages(ArrayList<AImage> images){
		this.images = images;
		this.redraw();
	}
	
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

	public void runPlugIn(String mainClassName) {
		PlugInClassLoader loader = PlugInClassLoader.getInstance();
		APlugIn plugin = (APlugIn) loader.instantiate(mainClassName);
		
		String initializeError = "Error in options()\n";
		
		try {
			plugin.initialize();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			initializeError  += sw.toString();
			context.postErrorEvent(initializeError+"\n");
		}
		
		Integer options = plugin.getOptions();

		if((options & APlugIn.OPTION_PROCESS_ALL) == APlugIn.OPTION_PROCESS_ALL){
			for(int i = 0; i < getImageStackSize(); i++){
				String processError= "Error in process() at image index " + i + " \n";
				try {
					AImage result = plugin.run(images.get(i));
					images.remove(i);
					images.add(i, result);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					processError  += sw.toString();
					context.postErrorEvent(processError+"\n");
				}
			}
		}
		else{
			AImage result = plugin.run(images.get(index));
			images.remove(index);
			images.add(index, result);
		}
		plugin.restoreSystemOut();
		redraw();
	}
}
