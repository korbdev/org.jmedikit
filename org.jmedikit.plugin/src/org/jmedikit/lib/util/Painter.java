package org.jmedikit.lib.util;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.jmedikit.lib.core.BilinearInterpolation;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.image.ROI;
import org.jmedikit.plugin.gui.events.EventConstants;

public class Painter{
	
	private DicomTreeItem item;
	
	private Canvas canvas;
	
	private Rectangle clientArea;
	
	private int index;
	
	//protected PaintEvent event;
	
	private boolean isInitialised = false;
	
	private boolean mouseDown;
	
	private int start_x;
	private int start_y;
	
	private int end_x;
	private int end_y;
	
	private int actual_x;
	private int actual_y;
	
	private int translation_x;
	private int translation_y;
	
	private int center_x;
	private int center_y;
	
	private int width;
	private int height;
	
	float size;
	
	private int image_center_x;
	private int image_center_y;

	private float roi_x;
	private float roi_y;
	private float roi_width;
	private float roi_height;
	
	private float wc;
	private float ww;

	private float wc_addition;

	private float ww_addition;
	
	public Painter(final String name, final DicomTreeItem selection, Canvas c) {
		item = selection;
		index = 0;
		canvas = c;
		size = 1;
		
		start_x = 0;
		start_y = 0;
		
		end_x = 0;
		end_x = 0;
		
		actual_x = 0;
		actual_y = 0;
		
		translation_x = 0;
		translation_y = 0;
		
		image_center_x = 0;
		image_center_y = 0;
		
		mouseDown = false;
		
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				//feste Variablen f�r Drawfunktion
				clientArea = canvas.getClientArea();
				
				width = clientArea.width-clientArea.x;
				height = clientArea.height-clientArea.y;
				
				center_x = width/2;
				center_y = height/2;
				
				if(!isInitialised){
					image_center_x = center_x;
					image_center_y = center_y;
					isInitialised = true;
				}
				
				draw(e);
			}
		});
		
		canvas.addListener(SWT.MouseDown, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				mouseDown = true;
				start_x = event.x;
				start_y = event.y;
				
				translation_x = 0;
				translation_y = 0;
				
				actual_x = start_x;
				actual_y = start_y;

				canvas.redraw();
			}
		});
		
		canvas.addListener(SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				mouseDown = false;
				end_x = event.x;
				end_y = event.y;
				
				//translation_x = end_x - start_x;
				//translation_y = end_y - start_y;
				
				image_center_x = image_center_x+translation_x;
				image_center_y = image_center_y+translation_y;
				
				canvas.redraw();
				System.out.println("Translation "+translation_x+" x "+translation_y);
			}
		});
		
		canvas.addListener(SWT.MouseMove, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if(mouseDown){
					actual_x = event.x;
					actual_y = event.y;
					
					translation_x = actual_x - start_x;
					translation_y = actual_y - start_y;
					
					//image_center_x = image_center_x+translation_x;
					//image_center_y = image_center_y+translation_y;
					
					canvas.redraw();
				}
			}
		});
		
		canvas.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {
				if(e.count < 0){
					ww_addition += -100;
				}
				else{
					ww_addition += 100;
				}
				canvas.redraw();
			}
		});
		
	}

	private void draw(PaintEvent e){
		
		Display display = Display.getCurrent();
		Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		
		Image bufferImage = new Image(canvas.getDisplay(), width, height);
		GC buffer = new GC(bufferImage);
		
		buffer.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
		buffer.fillRectangle(clientArea);
		
		DicomObject toDraw = null;
		if(item.getLevel() == DicomTreeItem.TREE_OBJECT_LEVEL){
			toDraw = (DicomObject)item;
		}
		else {
			toDraw = (DicomObject) item.getChild(index);
		}
		
		AbstractImage img = toDraw.getImage(0);
		
		wc = img.getWindowCenter() + wc_addition;
		ww = img.getWindowWidth() + ww_addition;
		
		int image_width = img.getWidth();
		int image_height = img.getHeight();
		
		int scaled_image_width = (int)(size*img.getWidth()+0.5);
		int scaled_image_height = (int)(size*img.getHeight()+0.5);
		
		int scaled_image_cx = scaled_image_width/2;
		int scaled_image_cy = scaled_image_width/2;
		
		int x = image_center_x - scaled_image_cx;
		int y = image_center_y - scaled_image_cy;
		
		System.out.println("XY: "+(x)+" x "+(y));
		
		//Bild soll nicht uber den oberen und linken Rand hinausgezogen werden
		if(x < -scaled_image_width){
			x = -scaled_image_width+10;
		}
		
		if(y < -scaled_image_height){
			y = -scaled_image_height+10;
		}
		
		//Bild soll nicht uber den unteren und rechten Rand hinausgezogen werden
		if(x > width){
			x = width-10;
		}
		if(y > height){
			y = height-10;
		}
		
		if(x < 0){
			roi_x = (float)Math.abs(x)/(float)scaled_image_width;
		}
		else roi_x = 0f;
		
		if(y < 0){
			roi_y = (float)Math.abs(y)/(float)scaled_image_height;
		}
		else roi_y = 0f;
		
		int x_offset = x + scaled_image_width - width;
		int y_offset = y + scaled_image_height - height;
		
		if(x_offset > 0){
			roi_width = (float)(scaled_image_width-x_offset)/(float)scaled_image_width;
		}
		else roi_width = 1f;
		
		if(y_offset > 0){
			roi_height = (float)(scaled_image_height-y_offset)/(float)scaled_image_height;
		}
		else roi_height = 1f;
		
		ROI roi = new ROI(roi_x, roi_y, roi_width, roi_height);
		img.setROI(roi);
		
		System.out.println(roi.toString());
		
		System.out.println("WH: "+(scaled_image_width-x_offset)+" x "+(scaled_image_height-y_offset));
		
		System.out.println(roi.toString());
		BilinearInterpolation scale = new BilinearInterpolation(img);
		AbstractImage resampled = scale.resampleROI(roi, image_width, image_height, scaled_image_width, scaled_image_height);
		
		System.out.println("WC+WW "+wc+", "+wc);
		ImageData data = ImageWindowInterpolation.interpolateImage(resampled, wc, ww, 0, 255);
		
		Image iimg = new Image(canvas.getDisplay(), data);
		
		System.out.println("index roix "+roi.x*scaled_image_width);
		buffer.drawImage(iimg, x+(int)(roi.x*scaled_image_width), y+(int)(roi.y*scaled_image_height));
		
		
		buffer.setForeground(blue);
		buffer.setBackground(blue);

		if(mouseDown){
			int rectWidth = (int) (size*img.getWidth());
			int rectHeight = (int) (size*img.getHeight());

			buffer.drawRectangle(x+translation_x, y+translation_y, rectWidth, rectHeight);
			buffer.setAlpha(50);
			buffer.fillRectangle(x+translation_x, y+translation_y, rectWidth, rectHeight);
		}
		
		e.gc.drawImage(bufferImage, 0, 0);

		bufferImage.dispose();
		buffer.dispose();
	}
	
	public void setIndex(int index){
		this.index = index;
		canvas.redraw();
	}
	
	public void setSize(float size){
		this.size = size/100;
		canvas.redraw();
	}
}
