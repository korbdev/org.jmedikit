package org.jmedikit.lib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.jmedikit.lib.core.BilinearInterpolation;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.image.ROI;

public abstract class AbstractTool_OLD {
	
	protected boolean isInitialised = false;
	
	protected DicomTreeItem item;
	
	protected int index;
	
	protected AbstractImage sourceImage;
	
	protected AbstractImage resampledImage;
	
	protected float scale;
	
	protected Point2D<Integer> scaledImageDimension;
	
	protected Point2D<Integer> scaledCenter;
	
	protected Point2D<Integer> imageDimemsion;
	
	protected Rectangle imageCoordinates;
	
	protected Point2D<Integer> imageCenter;
	
	/**
	 * Beinhaltet relative Koordinaten des sichtbaren Bildbereichs
	 */
	protected final ROI roi = new ROI();
	
	protected Point2D<Float> windowDimension;
	
	protected Canvas canvas;
	
	protected Rectangle clientArea;
	
	protected Point2D<Integer> canvasDimension;
	
	protected Point2D<Integer> canvasCenter;
	
	protected Map<Integer, Listener> listeners;
	
	protected PaintListener painter = new PaintListener() {
		
		@Override
		public void paintControl(PaintEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Paint");
			
			DicomObject toDraw = null;
			if(item.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
				toDraw = (DicomObject)item;
			}
			else {
				toDraw = (DicomObject) item.getChild(index);
			}
			
			sourceImage = toDraw.getImage(0);
			
			windowDimension = new Point2D<Float>(sourceImage.getWindowCenter(), sourceImage.getWindowWidth());
			
			//+0.5 fuer Kaufmaennische Rundung, erspart Aufruf von ceil() oder floor()
			int scaledImageWidth = (int) (scale * sourceImage.getWidth()+0.5);
			int scaledImageHeight = (int) (scale * sourceImage.getHeight()+0.5);
			scaledImageDimension = new Point2D<Integer>(scaledImageWidth, scaledImageHeight);
			
			clientArea = canvas.getClientArea();
			canvasDimension = new Point2D<Integer>(clientArea.width-clientArea.x, clientArea.height-clientArea.y);
			
			if(!isInitialised){
				//Bilddaten werden zentriert
				canvasCenter = new Point2D<Integer>(canvasDimension.x/2, canvasDimension.y/2);
				imageCenter = new Point2D<Integer>(canvasCenter.x, canvasCenter.y);
				scaledCenter = new Point2D<Integer>(scaledImageDimension.x/2, scaledImageDimension.y/2);
				isInitialised = true;
			}
			//Sichbarer bereich wird berechnet
			int xTemp = imageCenter.x - scaledCenter.x;
			int yTemp = imageCenter.y - scaledCenter.y;
			
			//Bild soll nicht uber den oberen und linken Rand hinausgezogen werden
			if(xTemp < -scaledImageWidth){
				xTemp = -scaledImageHeight+10;
			}
			
			if(yTemp < -scaledImageWidth){
				yTemp = -scaledImageHeight+10;
			}
			
			//Bild soll nicht uber den unteren und rechten Rand hinausgezogen werden
			if(xTemp > canvasDimension.x){
				xTemp = canvasDimension.x-10;
			}
			if(yTemp > canvasDimension.y){
				yTemp = canvasDimension.y-10;
			}
			
			if(xTemp < 0){
				roi.x = (float)Math.abs(xTemp)/(float)scaledImageDimension.x;
			}
			else roi.x = 0f;
			
			if(yTemp < 0){
				roi.y = (float)Math.abs(yTemp)/(float)scaledImageDimension.y;
			}
			else roi.y = 0f;
			
			int x_offset = xTemp + scaledImageDimension.x - canvasDimension.x;
			int y_offset = yTemp + scaledImageDimension.y - canvasDimension.y;
			
			if(x_offset > 0){
				roi.width = (float)(scaledImageDimension.x-x_offset)/(float)scaledImageDimension.x;
			}
			else roi.width = 1f;
			
			if(y_offset > 0){
				roi.height = (float)(scaledImageDimension.y-y_offset)/(float)scaledImageDimension.y;
			}
			else roi.height = 1f;
			
			System.out.println(roi.toString());
			//sourceImage.setROI(roi);
			int x = (int) (roi.x * scaledImageDimension.x + 0.5);
			int y = (int) (roi.y * scaledImageDimension.y + 0.5);
			int width = (int) (roi.width * scaledImageDimension.x + 0.5);
			int height = (int) (roi.height * scaledImageDimension.y + 0.5);
			imageCoordinates = new Rectangle(x, y, width, height);
			imageDimemsion = new Point2D<Integer>(width - x, height - y);
			
			//DoubleBuffer anlegen um Flackern der Zeichenflaeche zu verhindern
			Image bufferImage = new Image(canvas.getDisplay(), canvasDimension.x, canvasDimension.y);
			GC buffer = new GC(bufferImage);
			BilinearInterpolation scale = new BilinearInterpolation(getSourceImage());
			resampledImage = scale.resampleROI(roi, sourceImage.getWidth(), sourceImage.getHeight(), scaledImageWidth, scaledImageHeight);
			//Implementierer uebernimmt das Zeichnen
			buffer = paint(buffer);
			
			e.gc.drawImage(bufferImage, 0, 0);
			bufferImage.dispose();
		}
	};
	
	public AbstractTool_OLD(AbstractTool_OLD copyTool){
		item = copyTool.item;
		index = copyTool.index;
		sourceImage = copyTool.sourceImage;
		scale = copyTool.scale;
		scaledImageDimension = copyTool.scaledImageDimension;
		scaledCenter = copyTool.scaledCenter;
		imageDimemsion = copyTool.imageDimemsion;
		imageCoordinates = copyTool.imageCoordinates;
		imageCenter = copyTool.imageCenter;
		roi.x = copyTool.roi.x;
		roi.y = copyTool.roi.y;
		roi.width = copyTool.roi.width;
		roi.height = copyTool.roi.height;
		windowDimension = copyTool.windowDimension;
		canvas = copyTool.canvas;
		clientArea = copyTool.clientArea;
		canvasDimension = copyTool.canvasDimension;
		canvasCenter = copyTool.canvasCenter;
		canvas.removePaintListener(copyTool.painter);
		canvas.addPaintListener(painter);
		canvas.setFocus();
		listeners = new HashMap<Integer, Listener>();
		listeners.putAll(copyTool.listeners);
		for (Integer key : listeners.keySet()) {
		    System.out.println("Key = " + key);
		    canvas.removeListener(key, listeners.get(key));
		}
	}
	
	public AbstractTool_OLD(Composite parent, DicomTreeItem selection) {
		canvas = new Canvas(parent, SWT.NO_BACKGROUND);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		item = selection;
		scale = 1f;
		listeners = new HashMap<Integer, Listener>();
		System.out.println("Init Tool");
		canvas.addPaintListener(painter);
		canvas.setFocus();
		/*canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				
				System.out.println("Paint");
				
				DicomObject toDraw = null;
				if(item.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
					toDraw = (DicomObject)item;
				}
				else {
					toDraw = (DicomObject) item.getChild(index);
				}
				
				sourceImage = toDraw.getImage(0);
				
				windowDimension = new Point2D<Float>(sourceImage.getWindowCenter(), sourceImage.getWindowWidth());
				
				//+0.5 fuer Kaufmaennische Rundung, erspart Aufruf von ceil() oder floor()
				int scaledImageWidth = (int) (scale * sourceImage.getWidth()+0.5);
				int scaledImageHeight = (int) (scale * sourceImage.getHeight()+0.5);
				scaledImageDimension = new Point2D<Integer>(scaledImageWidth, scaledImageHeight);
				
				clientArea = canvas.getClientArea();
				canvasDimension = new Point2D<Integer>(clientArea.width-clientArea.x, clientArea.height-clientArea.y);
				
				//Bilddaten werden zentriert
				canvasCenter = new Point2D<Integer>(canvasDimension.x/2, canvasDimension.y/2);
				imageCenter = new Point2D<Integer>(canvasCenter.x, canvasCenter.y);
				scaledCenter = new Point2D<Integer>(scaledImageDimension.x/2, scaledImageDimension.y/2);
				
				//Sichbarer bereich wird berechnet
				int xTemp = imageCenter.x - scaledCenter.x;
				int yTemp = imageCenter.y - scaledCenter.y;
				
				if(xTemp < 0){
					roi.x = (float)Math.abs(xTemp)/(float)scaledImageDimension.x;
				}
				else roi.x = 0f;
				
				if(yTemp < 0){
					roi.y = (float)Math.abs(yTemp)/(float)scaledImageDimension.y;
				}
				else roi.y = 0f;
				
				int x_offset = xTemp + scaledImageDimension.x - canvasDimension.x;
				int y_offset = yTemp + scaledImageDimension.y - canvasDimension.y;
				
				if(x_offset > 0){
					roi.width = (float)(scaledImageDimension.x-x_offset)/(float)scaledImageDimension.x;
				}
				else roi.width = 1f;
				
				if(y_offset > 0){
					roi.height = (float)(scaledImageDimension.y-y_offset)/(float)scaledImageDimension.y;
				}
				else roi.height = 1f;
				
				System.out.println(roi.toString());
				//sourceImage.setROI(roi);
				int x = (int) (roi.x * scaledImageDimension.x + 0.5);
				int y = (int) (roi.y * scaledImageDimension.y + 0.5);
				int width = (int) (roi.width * scaledImageDimension.x + 0.5);
				int height = (int) (roi.height * scaledImageDimension.y + 0.5);
				imageCoordinates = new Rectangle(x, y, width, height);
				imageDimemsion = new Point2D<Integer>(width - x, height - y);
				
				//DoubleBuffer anlegen um Flackern der Zeichenflaeche zu verhindern
				Image bufferImage = new Image(canvas.getDisplay(), canvasDimension.x, canvasDimension.y);
				GC buffer = new GC(bufferImage);
				
				//Implementierer uebernimmt das Zeichnen
				buffer = paint(buffer);
				
				e.gc.drawImage(bufferImage, 0, 0);
				bufferImage.dispose();
			}
		});*/
	}

	public void registerListener(int eventType, Listener listener){
		canvas.addListener(eventType, listener);
		listeners.put(eventType, listener);
	}

	public DicomTreeItem getItem() {
		return item;
	}



	public void setItem(DicomTreeItem item) {
		this.item = item;
	}



	public int getIndex() {
		return index;
	}



	public void setIndex(int index) {
		this.index = index;
	}



	public AbstractImage getSourceImage() {
		return sourceImage;
	}



	public void setSourceImage(AbstractImage sourceImage) {
		this.sourceImage = sourceImage;
	}



	public float getScale() {
		return scale;
	}



	public void setScale(float scale) {
		this.scale = scale;
	}



	public Point2D<Integer> getScaledImageDimension() {
		return scaledImageDimension;
	}



	public void setScaledImageDimension(Point2D<Integer> scaledImageDimension) {
		this.scaledImageDimension = scaledImageDimension;
	}



	public Point2D<Integer> getScaledCenter() {
		return scaledCenter;
	}



	public void setScaledCenter(Point2D<Integer> scaledCenter) {
		this.scaledCenter = scaledCenter;
	}



	public Point2D<Integer> getImageDimemsion() {
		return imageDimemsion;
	}


	public void setImageDimemsion(Point2D<Integer> imageDimemsion) {
		this.imageDimemsion = imageDimemsion;
	}


	public Rectangle getImageCoordinates() {
		return imageCoordinates;
	}



	public void setImageCoordinates(Rectangle imageCoordinates) {
		this.imageCoordinates = imageCoordinates;
	}



	public Point2D<Integer> getImageCenter() {
		return imageCenter;
	}



	public void setImageCenter(Point2D<Integer> imageCenter) {
		this.imageCenter = imageCenter;
	}



	public ROI getRoi() {
		return roi;
	}


	public Point2D<Float> getWindowDimension() {
		return windowDimension;
	}



	public void setWindowDimension(Point2D<Float> windowDimension) {
		this.windowDimension = windowDimension;
	}



	public Canvas getCanvas() {
		return canvas;
	}



	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}



	public Rectangle getClientArea() {
		return clientArea;
	}



	public void setClientArea(Rectangle clientArea) {
		this.clientArea = clientArea;
	}



	public Point2D<Integer> getCanvasDimension() {
		return canvasDimension;
	}



	public void setCanvasDimension(Point2D<Integer> canvasDimension) {
		this.canvasDimension = canvasDimension;
	}



	public Point2D<Integer> getCanvasCenter() {
		return canvasCenter;
	}



	public void setCanvasCenter(Point2D<Integer> canvasCenter) {
		this.canvasCenter = canvasCenter;
	}



	public abstract GC paint(GC buffer);
}
