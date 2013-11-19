package org.jmedikit.plugin.gui;


import java.util.ArrayList;

import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.util.ImageProvider;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.gui.tools.AToolFactory;


public class ImageViewComposite extends Composite{
	
	private String title;
	
	private DicomTreeItem item;
	
	private DicomCanvas canvas;
	
	private Composite parent, canvasContainer, controls;

	private Slider slider;
	
	private ToolItem close, fullscreen, reload;
	
	private IResourcePool imageResource;
	
	private ImageViewPart rootPart;
	
	private Image closeImg, fullscreenImg, fullscreenExitImg, reloadImg;
	
	private Shell fullScreenShell;
	
	private boolean isFullscreen;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ImageViewComposite(Composite parent, int style, String title, DicomTreeItem selection, ArrayList<AbstractImage> images, IResourcePool pool, ImageViewPart rootPart) {
		super(parent, style);
		this.title = title;
		this.parent = parent;
		this.imageResource = pool;
		this.rootPart = rootPart;
		this.item = selection;
		isFullscreen = false;
		fullScreenShell = new Shell(SWT.NO_TRIM | SWT.ON_TOP);
		closeImg = imageResource.getImageUnchecked(ImageProvider.CLOSE_DARK_BUTTON);
		fullscreenImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_FULLSCREEN);
		fullscreenExitImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_FULLSCREEN_EXIT);
		reloadImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_RELOAD);
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		setLayoutData(data);

		canvasContainer = new Composite(this, SWT.NONE);
		canvasContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		//canvasContainer.setLayout(new GridLayout(1, false));
		GridLayout canvasContainerLayout = new GridLayout(1, false);
		canvasContainerLayout.marginHeight = 0;
		canvasContainerLayout.marginWidth = 0;
		canvasContainerLayout.horizontalSpacing = 0;
		canvasContainer.setLayout(canvasContainerLayout);
		
		/*titlebar = new Composite(canvasContainer, SWT.NONE);
		GridData titlebarData = new GridData(SWT.FILL, SWT.FILL, true, false, 0, 0);
		titlebarData.heightHint = 40;
		GridLayout titlebarLayout = new GridLayout(1, false);
		titlebarLayout.marginHeight = 3;
		titlebarLayout.marginWidth = 3;
		titlebar.setLayout(titlebarLayout);
		titlebar.setLayoutData(titlebarData);
		
		label = new Label(titlebar, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 0, 0));
		label.setText(title);*/
		
		canvas = new DicomCanvas(canvasContainer, SWT.NO_BACKGROUND, selection, images);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		
		int controlsSizeWidth = 20;
		int controlsSiteHeight = 20;
		
		controls = new Composite(this, SWT.NONE);
		GridLayout controlsLayout = new GridLayout(1, false);
		GridData controlsLayoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 0, 0);
		controlsLayoutData.widthHint = 20;
		controlsLayout.marginHeight = 0;
		controlsLayout.marginWidth = 0;
		controls.setLayout(controlsLayout);
		controls.setLayoutData(controlsLayoutData);
		
		ToolBar imageViewTools = new ToolBar(controls, SWT.FLAT | SWT.VERTICAL);
		//imageViewTools.setLayout(new GridLayout(1, false));
		//GridData toolbarData = new GridData(SWT.FILL, SWT.FILL, true, false, 0, 0);
		//toolbarData.widthHint = controlsSizeWidth;
		//imageViewTools.setLayoutData(toolbarData);
		
		
		close = new ToolItem(imageViewTools, SWT.NONE);
		close.setImage(closeImg);
		//GridData gd_button_close = new GridData(SWT.CENTER, SWT.FILL, false, false, 0, 0);
		//gd_button_close.widthHint = controlsSizeWidth;
		//gd_button_close.heightHint = controlsSiteHeight;
		//close.setLayoutData(gd_button_close);
		//close.setImage(closeImg);
		
		fullscreen = new ToolItem(imageViewTools, SWT.NONE);
		fullscreen.setImage(fullscreenImg);
		//GridData gd_button_fullscreen = new GridData(SWT.CENTER, SWT.FILL, false, false, 0, 0);
		//gd_button_fullscreen.widthHint = controlsSizeWidth;
		//gd_button_fullscreen.heightHint = controlsSiteHeight;
		//fullscreen.setLayoutData(gd_button_fullscreen);
		//fullscreen.setImage(fullscreenImg);
		
		reload = new ToolItem(imageViewTools, SWT.NONE);
		reload.setImage(reloadImg);
		
		slider = new Slider(controls, SWT.VERTICAL);
		GridData gd_slider = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		gd_slider.widthHint = controlsSizeWidth;
		slider.setLayoutData(gd_slider);
		int sliderMaximum = selection.getChildren().size() > 0 ? selection.getChildren().size()-1 : 0;
		System.out.println(sliderMaximum);
		slider.setMaximum(sliderMaximum+slider.getThumb());

		init();
	}

	private void initButtons(){
		
		close.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				//ImageViewComposite.this.layout();
				if(isFullscreen){
					isFullscreen = false;
					
					Shell current = Display.getCurrent().getActiveShell();

					current.setVisible(false);
					parent.layout(true, true);
				}
				dispose();
				fullScreenShell.dispose();
				parent.layout();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		fullscreen.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(isFullscreen){
					isFullscreen = false;
					
					Shell current = Display.getCurrent().getActiveShell();
					ImageViewComposite.this.setParent(parent);
					ImageViewComposite.this.fullscreen.setImage(fullscreenImg);
					current.setVisible(false);
					
					parent.layout(true, true);
					
					//Bild wieder zentrieren
					canvas.imageCenter.x = (int) ((canvas.getClientArea().width - canvas.getClientArea().x)/2);
					canvas.imageCenter.y = (int) ((canvas.getClientArea().height - canvas.getClientArea().y)/2);
					canvas.redraw();
				}
				else {
					isFullscreen = true;
					
					
					fullScreenShell.setLayout(new GridLayout(1, true));
					fullScreenShell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
					fullScreenShell.setBounds(Display.getDefault().getPrimaryMonitor().getBounds());

					ImageViewComposite.this.setParent(fullScreenShell);
					ImageViewComposite.this.fullscreen.setImage(fullscreenExitImg);
					
					fullScreenShell.setFullScreen(true);
					fullScreenShell.setVisible(true);
					
					fullScreenShell.layout(true, true);
					
					//Bild wieder zentrieren
					canvas.imageCenter.x = (int) ((canvas.getClientArea().width - canvas.getClientArea().x)/2);
					canvas.imageCenter.y = (int) ((canvas.getClientArea().height - canvas.getClientArea().y)/2);
					canvas.redraw();
					
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		reload.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Bild wieder zentrieren
				canvas.imageCenter.x = (int) ((canvas.getClientArea().width - canvas.getClientArea().x)/2);
				canvas.imageCenter.y = (int) ((canvas.getClientArea().height - canvas.getClientArea().y)/2);
				
				//imageDimension auf sourceImage zuruecksetzen
				canvas.imageDimension.width = canvas.sourceImage.getWidth();
				canvas.imageDimension.height = canvas.sourceImage.getHeight();
				
				//windowwerte zuruecksetzen
				canvas.windowCenter = canvas.sourceImage.getWindowCenter();
				canvas.windowWidth = canvas.sourceImage.getWindowWidth();
				canvas.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		/*minus.addListener(SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				
			}
		});
		
		minus.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("Minus lost");
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("Minus gained");
			}
		});
		
		plus.addListener(SWT.MouseUp, new Listener() {
			
			public void handleEvent(Event event) {
				
			}
		});*/
	}
	
	private void initSlider(){
		System.out.println("initSlider");
		System.out.println("Get max "+slider.getMaximum());
		slider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				//painter.setIndex(slider.getSelection());
				canvas.setIndex(slider.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}
	
	
	/*private void initText(){
		percentage.addListener(SWT.KeyUp, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == 13){
					String percent = percentage.getText();
					System.out.println(percent);
					//p.setSize(Integer.parseInt(percent));
				}
			}
		});
	}*/
	
	private void initCanvas(){
		canvas.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {

			}
			
			@Override
			public void focusGained(FocusEvent e) {
				rootPart.setActive(ImageViewComposite.this);
			}
		});
	}
	
	private void init(){
		//initText();
		initButtons();
		initSlider();
		initCanvas();
	}
	
	public void setTool(AToolFactory factory, String toolname){
		canvas.setTool(factory.createTool(toolname, canvas));
	}
	
	public DicomCanvas getCanvas(){
		return canvas;
	}
	
	public void setActiveCanvas(){
		rootPart.setActive(this);
	}
	
	public String getTitle(){
		return title;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}