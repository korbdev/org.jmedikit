package org.jmedikit.plugin.gui;



import java.security.CodeSource;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.DicomSeriesItem;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.plugin.gui.events.EventConstants;

import com.sun.media.imageio.stream.RawImageInputStream;


public class ImageViewPart {
	
	private Slider slider;
	
	private Label topLabel;
	
	private Canvas canvas;
	@Inject
	EModelService service;
	
	@Inject
	MApplication app;
	
	@Inject
    IResourcePool resourcePool;
	
	private float wc;
	private float ww;
	
	private ArrayList<AbstractImage> images;
	private ImageData interpolatedImg;
	private Image icon;
	
	@PostConstruct
	public void createGUI(final Composite parent){
		
		images = new ArrayList<AbstractImage>();
		
		parent.setLayout(new GridLayout(1, false));
		
		topLabel = new Label(parent, SWT.NONE);
		topLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		topLabel.setImage(icon);
		
		Class klass = RawImageInputStream.class;

	    CodeSource codeSource = klass.getProtectionDomain().getCodeSource();

	    if ( codeSource != null) {

	        System.out.println(codeSource.getLocation());

	    }else System.out.println("LOADED");
		
		canvas = new Canvas(parent, SWT.NONE);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				//System.out.println("In Paint Event "+canvas.getClass().getName());
				/*Rectangle clientArea = canvas.getClientArea();
				e.gc.drawLine(clientArea.width / 2, clientArea.height / 2, clientArea.width, clientArea.height );
				System.out.println(clientArea);
				GC gc = e.gc;
				gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_RED));
				Rectangle rect = new Rectangle(10, 10, 10, 10);
				gc.drawRectangle(rect);
				//only draw a box
	 
				gc.setBackground(new Color(e.display, 50, 50, 200));
	 
				gc.drawOval(100, 100, 5, 5);
	 
				//this can fill the shape				
				gc.fillOval(100, 200, 10, 10);
	 
	 
				Rectangle rect2 = new Rectangle(10, 100, 10, 10);
				gc.fillRectangle(rect2);
	 
				//draw a point may not not a good idea since it is too small to read
				gc.drawPoint(50, 59);*/
				}
		});
		canvas.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("Test1");
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("Test2");
			}
		});
		
		/*canvas.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {
				if(e.count > 0){
					//ww += 10;
					//interpolatedImg = ImageWindowInterpolation.interpolateImage(img, wc, ww, 0, 255);
					Display.getCurrent().syncExec(new Runnable() {
						
						@Override
						public void run() {
							canvas.setBackgroundImage(new Image(canvas.getDisplay(), interpolatedImg));
							canvas.redraw();
						}
					});
				}
				else{
					//ww -= 10;
					//interpolatedImg = ImageWindowInterpolation.interpolateImage(img, wc, ww, 0, 255);
					Display.getCurrent().syncExec(new Runnable() {
						
						@Override
						public void run() {
							canvas.setBackgroundImage(new Image(canvas.getDisplay(), interpolatedImg));
							canvas.redraw();
						}
					});
				}
			}
		});*/

		slider = new Slider(parent, SWT.NONE);
		slider.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		
		slider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(slider.getSelection());
				
				AbstractImage img = images.get(slider.getSelection());
				ww = img.getWindowWidth();
				wc = img.getWindowCenter();
				System.out.println(ww+", "+wc);
				interpolatedImg = ImageWindowInterpolation.interpolateImage(img, wc, ww, 0, 255);
				
				canvas.setBackgroundImage(new Image(canvas.getDisplay(), interpolatedImg));
				canvas.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.DICOMBROWSER_ITEM_SELECTION) final DicomTreeItem selection){
		//img = selection;
		
		if(selection instanceof DicomSeriesItem){
			//img = obj.getImage(0);
			
			Display.getCurrent().syncExec(new Runnable() {
				@Override
				public void run() {
					ArrayList<DicomTreeItem> children = selection.getChildren();
					slider.setMaximum(children.size());
					for (DicomTreeItem obj : children) {
						DicomObject o = (DicomObject) obj;
						images.add(o.getImage(0));
					}
					
					AbstractImage first = images.get(0);
					ww = first.getWindowWidth();
					wc = first.getWindowCenter();
					System.out.println(ww+", "+wc);
					interpolatedImg = ImageWindowInterpolation.interpolateImage(first, wc, ww, 0, 255);
					
					canvas.setBackgroundImage(new Image(Display.getCurrent(), interpolatedImg));
					canvas.redraw();
				}
			});
			//setFocus();
		}
		
	}
	
	//Labels cant get Focus
	@Focus
	public void setFocus(){
		canvas.setFocus();
	}
}
