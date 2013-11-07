package org.jmedikit.lib.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
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

public class DicomPaintingTool{

	private DicomTreeItem item;
	
	protected int index;
	
	private Canvas canvas;
	
	protected AbstractImage sourceImage;
	protected Dimension2D<Integer> sourceDimension;
	
	protected Point2D<Integer> imageCenter;
	protected Dimension2D<Integer> imageDimension;
	
	protected Dimension2D<Integer> canvasDimension;
	
	protected Point2D<Integer> start;
	protected Point2D<Integer> actual;
	protected Point2D<Integer> end;
	
	protected Color red;
	
	private ATransformationTool tool;
	
	Point2D<Integer> translation = new Point2D<>(0, 0);
	
	private Listener mouseDownListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			mouseDown = true;
			start.x = event.x;
			start.y = event.y;
			tool.actionMouseDown(event);
			//atMouseDown(event);
			canvas.redraw();
		}
	};
	
	private Listener mouseUpListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			mouseDown = false;
			mouseUp = true;
			end.x = event.x;
			end.y = event.y;
			tool.actionMouseUp(event);
			//atMouseUp(event);
			canvas.redraw();
		}
	};
	
	private Listener mouseMoveListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			actual.x = event.x;
			actual.y = event.y;
			tool.actionMouseMove(event);
			//atMouseMove(event);
			canvas.redraw();
		}
	};
	
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
			Rectangle clientRect = canvas.getClientArea();
			canvasDimension.width = clientRect.width - clientRect.x;
			canvasDimension.height = clientRect.height - clientRect.y;
			
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
				
				//Transformationen
				/*if(mouseDown){
					System.out.println("MouseDown "+start.toString() +", "+end.toString());
					translation.x = end.x - start.x;
					translation.y = end.y - start.y;
					
					if(translation.x < 0){
						imageCenter.x = imageCenter.x-1;
					}
					else imageCenter.x = imageCenter.x+1;
					
					if(translation.y < 0){
						imageCenter.y = imageCenter.y-1;
					}
					else imageCenter.y = imageCenter.y+1;
				}
				if(mouseUp){
					System.out.println("Translation "+translation.x);
					//imageCenter.x = imageCenter.x + translation.x;
					//imageCenter.y = imageCenter.y + translation.y;
					mouseUp = false;
					//imageCenter.x = imageCenter.x + translation.x;
					//imageCenter.y = imageCenter.y + translation.y;
				}
				
				System.out.println("Alt "+imageCenter.toString());
				
				*/
				
				//imageDimension.width = sourceDimension.width;
				//imageDimension.height = sourceDimension.height;
			}
			
			Image bufferImage = new  Image(canvas.getDisplay(), canvasDimension.width, canvasDimension.height);
			GC buffer = new GC(bufferImage);
			
			//Sichbare Elemente berechnen
			buffer = draw(buffer);
			buffer = tool.postCalculation(buffer);
			event.gc.drawImage(bufferImage, 0, 0);
			bufferImage.dispose();
			buffer.dispose();
		}
	};
	
	protected boolean mouseDown;
	
	protected boolean mouseUp;
	
	private boolean isInitialized;
	
	public DicomPaintingTool(Composite parent, DicomTreeItem selection) {
		
		item = selection;
		index = 0;
		
		start = new Point2D<Integer>(0, 0);
		actual = new Point2D<Integer>(0, 0);
		end = new Point2D<Integer>(0, 0);
		
		imageCenter = new Point2D<Integer>(0, 0);
		imageDimension = new Dimension2D<Integer>(0, 0);
		
		canvas = new Canvas(parent, SWT.NO_BACKGROUND);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		canvasDimension = new Dimension2D<Integer>(0, 0);
		sourceDimension = new Dimension2D<Integer>(0, 0);
		
		canvas.addListener(SWT.MouseDown, mouseDownListener);
		canvas.addListener(SWT.MouseUp, mouseUpListener);
		canvas.addListener(SWT.MouseMove, mouseMoveListener);
		canvas.addListener(SWT.Paint, paintListener);
		
		//tool = new MoveTool(this);
		
		red = new Color(canvas.getDisplay(), new RGB(255, 0, 0));
	}

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
		
		//System.out.println(tl.toString()+" "+tr.toString()+" "+bl.toString()+" "+br.toString());
		//System.out.println(imageBounds);
		
		if(imageBounds.x < 0){
			//top_left_x ragt ueber den rand hinaus
			//System.out.println("neue x-koordinate: "+Math.abs(imageBounds.x));
			newBounds.x = 0;
			newBounds.width = imageBounds.width+imageBounds.x;
			roi.x = (float)(Math.abs(imageBounds.x)) / (float)imageBounds.width;
		}
		else{
			newBounds.x = imageBounds.x;
			roi.x = 0f;
		}
		
		if(imageBounds.y < 0){
			//System.out.println("neue y-koordinate: "+Math.abs(imageBounds.y));
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
		
		ImageData data = ImageWindowInterpolation.interpolateImage(resampled, sourceImage.getWindowCenter(), sourceImage.getWindowWidth(), 0, 255);
		Image iimg = new Image(canvas.getDisplay(), data);
		
		//System.out.println(newBounds);
		//System.out.println(roi.toString());
		//buffer.setBackground(red);
		//buffer.fillRectangle(newBounds);
		buffer.drawImage(iimg, newBounds.x, newBounds.y);
		return buffer;
	}
	
	
	public void setTool(ATransformationTool tool){
		this.tool = tool;
	}
	
	public void setIndex(int index){
		this.index = index;
		canvas.redraw();
	}
	/*private void atMouseDown(Event e){
		mouseDown = true;
		translation.x = 0;
		translation.y = 0;
	}
	
	private void atMouseUp(Event e){
		mouseDown = false;
		//imageCenter.x = imageCenter.x + translation.x;
		//imageCenter.y = imageCenter.y + translation.y;
	}
	
	private void atMouseMove(Event e){
		if(mouseDown){
			
			Point2D<Integer> translationOld = new Point2D<>(translation.x, translation.y);
			
			translation.x = actual.x - start.x;
			translation.y = actual.y - start.y;
			System.out.println(translation.toString());
			imageCenter = new Point2D<Integer>(imageCenter.x+(translation.x - translationOld.x), imageCenter.y+(translation.y - translationOld.y));
		}
		
		
	}
	
	private GC atEnd(GC image){
		if(mouseDown){
			int rectWidth = (int) (imageDimension.width);
			int rectHeight = (int) (imageDimension.height);

			image.drawRectangle(imageCenter.x-rectWidth/2, imageCenter.y - rectHeight/2, rectWidth, rectHeight);
			image.setAlpha(50);
			image.fillRectangle(imageCenter.x-rectWidth/2, imageCenter.y - rectHeight/2, rectWidth, rectHeight);
		}
		return image;
	}*/
}
