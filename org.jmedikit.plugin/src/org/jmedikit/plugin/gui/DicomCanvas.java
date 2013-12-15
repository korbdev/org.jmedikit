package org.jmedikit.plugin.gui;

import java.util.ArrayList;

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
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.image.ImageCube;
import org.jmedikit.lib.image.ROI;
import org.jmedikit.lib.util.Dimension2D;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.lib.util.Vector3D;
import org.jmedikit.plugin.gui.tools.ATool;

import com.sun.org.apache.xpath.internal.axes.AxesWalker;

public class DicomCanvas extends Canvas{
 
	private DicomTreeItem item;
	
	private int index;
	
	private ATool tool;
	
	private ArrayList<AbstractImage> images;
	
	private ImageCube cube;
	
	public AbstractImage sourceImage;
	public Dimension2D<Integer> sourceDimension;
	public String imageOrientationType;
	
	public float windowCenter;
	public float windowWidth;
	
	public int min;
	public int max;
	
	public String[] axes;
	
	public Point2D<Integer> imageCenter;
	public Dimension2D<Integer> imageDimension;
	
	public Dimension2D<Integer> canvasDimension;
	
	private boolean isInitialized;
	private boolean doXLineUpdate;
	private boolean doYLineUpdate;
	
	private Color black;
	private Color white;
	private Color yellow;
	private Color brightBlue;
	private Color lightRed;
	
	private float alpha;
	private float beta;
	private float gamma;
	
	private int startWidth;
	private int startHeight;
	
	private int actualWidth;
	private int actualHeight;
	
	private int xLineIndex;
	private int yLineIndex;
	
	private int xIndex;
	private int yIndex;
	
	public DicomCanvas(Composite parent, int style, DicomTreeItem selection, ArrayList<AbstractImage> images) {
		super(parent, style);

		item = selection;
		this.images = images;
		startWidth = images.get(0).getWidth();
		startHeight = images.get(0).getHeight();
		
		actualWidth = startWidth;
		actualHeight = startHeight;
		
		cube = new ImageCube(images);
		
		black = new Color(this.getDisplay(), new RGB(0, 0, 0));
		white = new Color(this.getDisplay(), new RGB(255, 255, 255));
		yellow = new Color(this.getDisplay(), new RGB(250, 250, 210));
		brightBlue = new Color(this.getDisplay(), new RGB(116, 232, 230));
		lightRed = new Color(this.getDisplay(), new RGB(232, 116, 118));
		
		sourceDimension = new Dimension2D<Integer>(0, 0);
		imageCenter = new Point2D<Integer>(0, 0);
		imageDimension = new Dimension2D<Integer>(0, 0);
		canvasDimension = new Dimension2D<Integer>(0, 0);
		
		index = 0;

		sourceImage = images.get(0);
		sourceDimension.width = sourceImage.getWidth();
		sourceDimension.height = sourceImage.getHeight();
		
		windowCenter = sourceImage.getWindowCenter();
		windowWidth = sourceImage.getWindowWidth();
		
		min = sourceImage.getMin();
		max = sourceImage.getMax();
		
		doXLineUpdate = false;
		doYLineUpdate = false;
		
		xLineIndex = 0;
		yLineIndex = 0;
		
		xIndex = 0;
		yIndex = 0;
		
		addListener(SWT.MouseDown, mouseDownListener);
		addListener(SWT.MouseUp, mouseUpListener);
		addListener(SWT.MouseMove, mouseMoveListener);
		addListener(SWT.Paint, paintListener);
	}

	private Listener paintListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			
			sourceImage = images.get(index);
			//System.out.println(sourceImage.getWidth());
			
			actualWidth = sourceImage.getWidth();
			actualHeight = sourceImage.getHeight();
			
			/*cube.setRotationsAngles(alpha, beta, gamma);
			if(alpha != 0 || beta != 0 || gamma != 0){
				sourceImage = cube.calc3D(index);
			}*/
			if(actualWidth != startWidth || actualHeight != startHeight){
				//resample
				BilinearInterpolation resampleImg = new BilinearInterpolation(sourceImage);
				AbstractImage res = resampleImg.resample(actualWidth, actualHeight, startWidth, startHeight);
				res.setImagePosition(sourceImage.getImagePosition());
				res.setRowImageOrientation(sourceImage.getRowImageOrientation());
				res.setColumnImageOrientation(sourceImage.getColumnImageOrientation());
				sourceImage = res;
			}
			axes = sourceImage.getImageOrientationAxis();
			imageOrientationType = sourceImage.getMprType();
			
			//System.out.println("x, y, z "+actualWidth+", "+actualHeight+", "+index+" normal "+Vector3D.crossProduct(sourceImage.getRowImageOrientation(), sourceImage.getColumnImageOrientation()).toString());
			
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
		
		ROI roi = new ROI(0f, 0f, 1f, 1f);
		
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
		
		//System.out.println(roi.toString());
		
		//Roi ermittelt
		BilinearInterpolation bilinearInterpolation = new BilinearInterpolation(sourceImage);
		AbstractImage resampled = bilinearInterpolation.resampleROI(roi, sourceDimension.width, sourceDimension.height, imageDimension.width, imageDimension.height);
		resampled.setMinMaxValues(min, max);
		//resampled.determineMinMaxValues(resampled.getPixels());
		
		ImageData data = ImageWindowInterpolation.interpolateImage(resampled, windowCenter, windowWidth, 0, 255);
		Image iimg = new Image(this.getDisplay(), data);
		
		GC scoutingLines = new GC(iimg);
		scoutingLines.setLineWidth(4);
		//System.out.println("YLINEINDEX ! "+yLineIndex);
		if(yLineIndex != 0){
			if(startHeight != actualHeight && doYLineUpdate){
				if(startHeight > actualHeight){
					yLineIndex *= (int)((float)startHeight/(float)actualHeight+0.5f);
				}
				else yLineIndex *= (int)((float)actualHeight/(float)startHeight+0.5f);
				doYLineUpdate = false;
			}
			
			float yLineNormaliezed = (float)yLineIndex/(float)startWidth;
			//int yIndex = 0;
			if(roi.y < yLineNormaliezed){
				float temp = yLineNormaliezed-roi.y;
				//System.out.println("TEMP "+temp);
				yIndex = (int) (temp * imageDimension.height);
				
				//GC scoutingLines = new GC(iimg);
				
				scoutingLines.setForeground(lightRed);
				scoutingLines.drawLine(0, yIndex, iimg.getBounds().width, yIndex);
				
				//scoutingLines.dispose();
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
			
			float xLineNormaliezed = (float)xLineIndex/(float)startWidth;
			//int xIndex = 0;
			if(roi.x < xLineNormaliezed){
				float temp = xLineNormaliezed-roi.x;
				//System.out.println("TEMP "+temp);
				xIndex = (int) (temp * imageDimension.width);			
				
				scoutingLines.setForeground(brightBlue);
				scoutingLines.drawLine(xIndex, 0, xIndex, iimg.getBounds().height);
				
				
			}
			
			//System.out.println("XINDEX "+xIndex+" STARTDIM "+startWidth+" x "+startHeight+", ACTDIM "+actualWidth+" x "+actualHeight);
		}
		
		scoutingLines.dispose();
		
		//System.out.println("IIMG "+iimg.getBounds().toString());
		buffer.setBackground(black);
		buffer.setForeground(white);
		
		//GC lineGc = new GC(iimg);
		//lineGc.drawLine(0, sourceDimension.height/2, sourceDimension.width, sourceDimension.height/2);
		
		buffer.fillRectangle(0, 0, canvasDimension.width, canvasDimension.height);
		
		buffer.drawImage(iimg, newBounds.x, newBounds.y);

		String sliceNumber = (index+1)+"/ "+images.size();
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
		
		iimg.dispose();
		return buffer;
	}
	
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
	
	/*private AbstractImage loadImage(){
		//Test, ob Bilddaten bereits geladen sind
		AbstractImage img = images.get(index);
		if(img == null){
			DicomObject toDraw;
			if(item.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
				toDraw = (DicomObject)item;
			}
			else {
				toDraw = (DicomObject) item.getChild(index);
			}
			img = toDraw.getImage(0);
			images.add(index, img);
			return img;
		}
		else return img;
	}*/
	
	
	public void setTool(ATool tool){
		this.tool = tool;
	}
	
	public void setIndex(int index){
		this.index = index;
		this.redraw();
	}
	
	public void setImages(ArrayList<AbstractImage> images){
		this.images = images;
		this.redraw();
	}
	
	public void recalculateImages(String newOrientation){
		isInitialized = false;
		index = 0;
		if(newOrientation.equals(AbstractImage.AXIAL)){
			images = cube.calculateAxialView();
		}
		else if(newOrientation.equals(AbstractImage.CORONAL)){
			images = cube.calculateCoronalView();
		}
		else if(newOrientation.equals(AbstractImage.SAGITTAL)){
			images = cube.calculateSagittalView();
		}
		this.redraw();
	}
	
	public int getImageStackSize(){
		return images.size();
	}
	
	public void setAngles(float alpha, float beta, float gamma){
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		
		this.redraw();
	}
	
	public Point2D<Float> getNormalizedImageCenter(){
		float x = (float)imageCenter.x / (float)canvasDimension.width;
		float y = (float)imageCenter.y / (float)canvasDimension.height;
		return new Point2D<Float>(x, y);
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
}
