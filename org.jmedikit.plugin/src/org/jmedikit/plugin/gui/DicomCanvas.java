package org.jmedikit.plugin.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.jmedikit.lib.core.BilinearInterpolation;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.image.ROI;
import org.jmedikit.lib.util.Dimension2D;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.tools.ATool;

public class DicomCanvas extends Canvas{
 
	
	private DicomTreeItem item;
	
	private int index;
	
	private ATool tool;
	
	public AbstractImage sourceImage;
	public Dimension2D<Integer> sourceDimension;
	
	public float windowCenter;
	public float windowWidth;
	
	public Point2D<Integer> imageCenter;
	public Dimension2D<Integer> imageDimension;
	
	public Dimension2D<Integer> canvasDimension;
	
	private boolean isInitialized;
	
	public DicomCanvas(Composite parent, int style, DicomTreeItem selection) {
		super(parent, style);

		item = selection;
		
		sourceDimension = new Dimension2D<Integer>(0, 0);
		imageCenter = new Point2D<Integer>(0, 0);
		imageDimension = new Dimension2D<Integer>(0, 0);
		canvasDimension = new Dimension2D<Integer>(0, 0);
		
		index = 0;
		
		//Bilddaten von DicomObject laden
		DicomObject toDraw;
		if(item.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
			toDraw = (DicomObject)item;
		}
		else {
			toDraw = (DicomObject) item.getChild(index);
		}
		
		sourceImage = toDraw.getImage(0);
		sourceDimension.width = sourceImage.getWidth();
		sourceDimension.height = sourceImage.getHeight();
		
		windowCenter = sourceImage.getWindowCenter();
		windowWidth = sourceImage.getWindowWidth();
		
		addListener(SWT.MouseDown, mouseDownListener);
		addListener(SWT.MouseUp, mouseUpListener);
		addListener(SWT.MouseMove, mouseMoveListener);
		addListener(SWT.Paint, paintListener);
	}

	private Listener paintListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			
			//Bilddaten von DicomObject laden
			DicomObject toDraw;
			if(item.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
				toDraw = (DicomObject)item;
			}
			else {
				toDraw = (DicomObject) item.getChild(index);
			}
			
			sourceImage = toDraw.getImage(0);
			sourceDimension.width = sourceImage.getWidth();
			sourceDimension.height = sourceImage.getHeight();
			
			
			//bestimme Koordinaten von Canvas
			Rectangle clientRect = DicomCanvas.this.getClientArea();
			canvasDimension.width = clientRect.width - clientRect.x;
			canvasDimension.height = clientRect.height - clientRect.y;
			
			Image bufferImage = new  Image(DicomCanvas.this.getDisplay(), canvasDimension.width, canvasDimension.height);
			GC buffer = new GC(bufferImage);
			
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
			//System.out.println("Offset_x "+offset_x);
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
		
		//Roi ermittelt
		BilinearInterpolation bilinearInterpolation = new BilinearInterpolation(sourceImage);
		AbstractImage resampled = bilinearInterpolation.resampleROI(roi, sourceDimension.width, sourceDimension.height, imageDimension.width, imageDimension.height);
		resampled.determineMinMaxValues(resampled.getPixels());
		
		ImageData data = ImageWindowInterpolation.interpolateImage(resampled, windowCenter, windowWidth, 0, 255);
		Image iimg = new Image(this.getDisplay(), data);

		buffer.drawImage(iimg, newBounds.x, newBounds.y);
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
	
	public void setTool(ATool tool){
		this.tool = tool;
	}
	
	public void setIndex(int index){
		this.index = index;
		this.redraw();
	}
}
