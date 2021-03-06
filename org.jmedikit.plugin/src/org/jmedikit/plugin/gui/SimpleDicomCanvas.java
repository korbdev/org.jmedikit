package org.jmedikit.plugin.gui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.core.Visualizer;
import org.jmedikit.lib.image.AImage;

/**
 * Das SimpleDicomCanvas ist eine leichte Variante des {@link DicomCanvas} und dient zum visuellen Debugging. F�r das Debuggin innerhalb der Plug-ins muss die Klasse {@link Visualizer}
 * verwendet werden.
 * 
 * @author rkorb
 *
 */
public class SimpleDicomCanvas {

	private Canvas canvas;
	
	private Slider slider;
	
	private Shell shell;
	
	private int sliderIndex;
	//private ArrayList<AImage> images;
	
	/**
	 * Erzeugt ein neuen Fenster mit einem Titel
	 *  
	 * @param title Titel des Fensters
	 */
	public SimpleDicomCanvas(String title) {
		shell = new Shell(ImageViewPart.getPartShell());
		shell.setLayout(new GridLayout(1, true));
		shell.setText(title);
		
		sliderIndex = 0;
		
		initComposite(shell);
	}
	
	private void initComposite(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		canvas = new Canvas(container, SWT.NO_BACKGROUND);
		GridData canvasData = new GridData(SWT.FILL, SWT.FILL, true, true);
		canvas.setLayoutData(canvasData);
		
		slider = new Slider(container, SWT.VERTICAL);
		GridData sliderData = new GridData(SWT.FILL, SWT.FILL, false, false);
		sliderData.widthHint = 20;
		slider.setLayoutData(sliderData);
		
		slider.addListener(SWT.Selection, sliderSelection);
	}

	public void show(AImage img){
		ArrayList<AImage> images = new ArrayList<AImage>();
		images.add(img);
		show(images);
	}
	
	public void show(final ArrayList<AImage> images){
		GridData canvasData = (GridData) canvas.getLayoutData();
		canvasData.widthHint = images.get(0).getWidth();
		canvasData.heightHint = images.get(0).getHeight();
		
		int sliderMax = images.size() > 0 ? images.size()-1 : 0;
		slider.setMaximum(sliderMax+slider.getThumb());
		shell.pack();
		
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				Rectangle canvasRect = canvas.getClientArea();
				System.out.println(canvasRect.toString());
				Image bufferImage = new  Image(canvas.getDisplay(), canvasRect.width, canvasRect.height);
				GC buffer = new GC(bufferImage);
				
				buffer = draw(buffer, images.get(sliderIndex));
				e.gc.drawImage(bufferImage, 0, 0);
				
				bufferImage.dispose();
				buffer.dispose();
			}
		});
		
		
		shell.open();
	}
	
	private GC draw(GC buffer, AImage img){
		System.out.println("WW WC "+img.getWindowWidth()+" x "+img.getWindowCenter());
		ImageData data = ImageWindowInterpolation.interpolateImage(img, img.getWindowCenter(), img.getWindowWidth(), 0, 255);
		Image iimg = new Image(canvas.getDisplay(), data);
		buffer.drawImage(iimg, 0, 0);
		iimg.dispose();
		return buffer;
	}
	
	private Listener sliderSelection = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			System.out.println(slider.getSelection());
			sliderIndex = slider.getSelection();
			canvas.redraw();
		}
	};
}
