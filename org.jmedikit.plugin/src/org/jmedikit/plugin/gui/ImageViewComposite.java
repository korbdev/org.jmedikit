package org.jmedikit.plugin.gui;


import java.util.ArrayList;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jmedikit.lib.core.ADicomTreeItem;
import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.gui.tools.AToolFactory;
import org.jmedikit.plugin.util.IObserver;
import org.jmedikit.plugin.util.ISubject;
import org.jmedikit.plugin.util.ImageProvider;


public class ImageViewComposite extends Composite implements ISubject, IObserver{
	
	private String title;
	
	private ADicomTreeItem item;
	
	//private ArrayList<AImage> images;
	
	private DicomCanvas canvas;
	
	private Composite parent, canvasContainer, controls;

	private Slider slider;
	
	private ToolItem close, fullscreen, reload;
	
	private ToolItem imageSelection, annotations, scoutingLines;
	
	private IResourcePool imageResource;
	
	private ImageViewPart rootPart;
	
	private Image closeImg, currentImg, fullscreenImg, fullscreenExitImg, reloadImg, imageSelectionImg, annotationsImg, scoutingLinesImg;
	
	private Shell fullScreenShell;
	
	private boolean isFullscreen;
	
	private ArrayList<IObserver> observers;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ImageViewComposite(Composite parent, int style, String title, ADicomTreeItem selection, ArrayList<AImage> images, IResourcePool pool, ImageViewPart rootPart) {
		super(parent, style);
		
		observers = new ArrayList<IObserver>();
		
		this.title = title;
		this.parent = parent;
		this.imageResource = pool;
		this.rootPart = rootPart;
		this.item = selection;
		//this.images = images;
		
		isFullscreen = false;
		fullScreenShell = new Shell(SWT.NO_TRIM | SWT.ON_TOP);
		closeImg = imageResource.getImageUnchecked(ImageProvider.CLOSE_DARK_BUTTON);
		currentImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_CURRENT);
		fullscreenImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_FULLSCREEN);
		fullscreenExitImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_FULLSCREEN_EXIT);
		reloadImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_RELOAD);
		imageSelectionImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_SELECTION);
		annotationsImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_ANNOTATION);
		scoutingLinesImg = imageResource.getImageUnchecked(ImageProvider.IMAGEVIEW_SCOUTINGLINES);
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		setLayoutData(data);

		canvasContainer = new Composite(this, SWT.NONE);
		canvasContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));

		GridLayout canvasContainerLayout = new GridLayout(1, false);
		canvasContainerLayout.marginHeight = 0;
		canvasContainerLayout.marginWidth = 0;
		canvasContainerLayout.horizontalSpacing = 0;
		canvasContainer.setLayout(canvasContainerLayout);
		
		canvas = new DicomCanvas(canvasContainer, SWT.NO_BACKGROUND, selection, images, pool, this); 
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		
		int controlsSizeWidth = 20;
		//int controlsSiteHeight = 20;
		
		controls = new Composite(this, SWT.NONE);
		GridLayout controlsLayout = new GridLayout(1, false);
		GridData controlsLayoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 0, 0);
		controlsLayoutData.widthHint = 20;
		controlsLayout.marginHeight = 0;
		controlsLayout.marginWidth = 0;
		controls.setLayout(controlsLayout);
		controls.setLayoutData(controlsLayoutData);
		
		ToolBar imageViewTools = new ToolBar(controls, SWT.FLAT | SWT.VERTICAL);
		
		close = new ToolItem(imageViewTools, SWT.NONE);
		close.setImage(currentImg);

		fullscreen = new ToolItem(imageViewTools, SWT.NONE);
		fullscreen.setImage(fullscreenImg);

		reload = new ToolItem(imageViewTools, SWT.NONE);
		reload.setImage(reloadImg);
		
		slider = new Slider(controls, SWT.VERTICAL);
		GridData gd_slider = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		gd_slider.widthHint = controlsSizeWidth;
		slider.setLayoutData(gd_slider);
		//int sliderMaximum = selection.getChildren().size() > 0 ? selection.getChildren().size()-1 : 0;
		int sliderMaximum = images.size() > 0 ? images.size()-1 : 0;
		//System.out.println(sliderMaximum);
		slider.setMaximum(sliderMaximum+slider.getThumb());

		ToolBar annotationTools = new ToolBar(controls, SWT.FLAT | SWT.VERTICAL);
		imageSelection = new ToolItem(annotationTools, SWT.CHECK);
		imageSelection.setImage(imageSelectionImg);
		annotations = new ToolItem(annotationTools, SWT.CHECK);
		annotations.setImage(annotationsImg);
		scoutingLines = new ToolItem(annotationTools, SWT.CHECK);
		scoutingLines.setImage(scoutingLinesImg);
		
		//Preferences prefs = ConfigurationScope.INSTANCE.getNode("org.jmedikit.plugin");
		//System.out.println(prefs.get(PreferencesConstants.PLUGIN_DIRECTORY, "notset"));
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
				widgetSelected(e);
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
				widgetSelected(e);
			}
		});
		
		imageSelection.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean toggle = canvas.isDrawSelection();
				if(toggle){
					canvas.setDrawSelection(false);
				}
				else canvas.setDrawSelection(true);
				canvas.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		annotations.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean toggle = canvas.isDrawAnnotations();
				if(toggle){
					canvas.setDrawAnnotations(false);
				}
				else canvas.setDrawAnnotations(true);
				canvas.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		scoutingLines.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean toggle = canvas.isDrawScoutingLines();
				if(toggle){
					canvas.setDrawScoutingLines(false);
				}
				else canvas.setDrawScoutingLines(true);
				canvas.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}
	
	public void setSingleSliderSelection(int index){
		slider.setSelection(index);
		parent.layout(true);
		slider.update();
	}
	
	public void setSliderSelection(int index){
		System.out.println(index + "Slider Event");
		slider.setSelection(index);
		slider.notifyListeners(SWT.Selection, new Event());
		slider.redraw();
	}
	
	public int getSliderSelection(){
		return slider.getSelection();
	}
	
	private void initSlider(){
		//System.out.println("initSlider");
		//System.out.println("Get max "+slider.getMaximum());
		slider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setFocus();
				System.out.println("Observer index "+slider.getSelection());
				canvas.setIndex(slider.getSelection());
				//notifyObservers(canvas.getActualImageWidth(), canvas.getActualImageHeight(), slider.getSelection());
				notifyObservers(slider.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("hallo");
			}
		});
		
		slider.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("Focus lost");
				close.setImage(closeImg);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("Focus gained");
				rootPart.setActive(ImageViewComposite.this);
				close.setImage(currentImg);
			}
		});
	}
	
	private void initCanvas(){
		canvas.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				close.setImage(closeImg);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				rootPart.setActive(ImageViewComposite.this);
				close.setImage(currentImg);
			}
		});
	}
	
	private void init(){
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
	
	public void setSliderMaximum(int max){
		slider.setMaximum(max+slider.getThumb());
		slider.setSelection(0);
		canvas.redraw();
	}
	
	public String getTitle(){
		return title;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void registerObserver(IObserver o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(IObserver o) {
		if(observers.contains(o)){
			observers.remove(o);
		}
	}

	@Override
	public void notifyObservers(int z) {
		for(IObserver o : observers){
			if(o instanceof ImageViewComposite){
				String actualIOT = this.getCanvas().imageOrientationType;
				String observerIOT = ((ImageViewComposite) o).getCanvas().imageOrientationType;
				if(actualIOT.equals(observerIOT)){
					o.update(z);
				}
				else{
					o.updateScoutingLine(z, actualIOT);
				}
			}
		}
	}
	
	@Override
	public void notifyObservers(float x, float y, float z) {
		for(IObserver o : observers){
			if(o instanceof ImageViewComposite){
				int x_normal = (int) (x * this.canvas.getActualImageWidth()+0.5);
				int y_normal = (int) (y * this.canvas.getActualImageHeight()+0.5);

				String actualIOT = this.getCanvas().imageOrientationType;
				
				this.getCanvas().setDoXLineUpdate(true);
				this.getCanvas().setDoYLineUpdate(true);
				
				this.getCanvas().setxLineIndex(x_normal);
				this.getCanvas().setyLineIndex(y_normal);
				
				o.updateScoutingLine(x, y, z, actualIOT);
			}
		}
	}
	
	@Override
	public void dispose(){
		super.dispose();
		System.out.println("Remove Observers");
		rootPart.deleteChild(this);
	}
	
	@Override
	public void update(int index){
		canvas.setIndex(index);
		slider.setSelection(index);
	}

	//@Override
	public void updateScoutingLine(float xn, float yn, float zn, String mprType){
		String IOT = this.getCanvas().imageOrientationType;
		
		int width = canvas.getActualImageWidth();
		int height = canvas.getActualImageHeight();
		
		if( (mprType.equals(AImage.AXIAL) && IOT.equals(AImage.AXIAL)) ||
			(mprType.equals(AImage.CORONAL) && IOT.equals(AImage.CORONAL)) ||
			(mprType.equals(AImage.SAGITTAL) && IOT.equals(AImage.SAGITTAL))
			){
			int x = (int) (xn * width + 0.5);
			int y = (int) (yn * height+0.5);
			int z = (int) (zn * canvas.getImages().size() + 0.5);
			
			canvas.setDoYLineUpdate(true);
			canvas.setDoXLineUpdate(true);
			canvas.setIndex(z);
			slider.setSelection(z);
			canvas.setxLineIndex(x); //neu
			canvas.setyLineIndex(y);
		}
		if(mprType.equals(AImage.AXIAL) && IOT.equals(AImage.CORONAL)){
			
			int x = (int) (xn * width + 0.5);
			int y = (int) (zn * height+0.5);
			int z = (int) (yn * canvas.getImages().size() + 0.5);
			
			canvas.setDoYLineUpdate(true);
			canvas.setDoXLineUpdate(true);
			
			canvas.setIndex(z);
			slider.setSelection(z);
			
			canvas.setxLineIndex(x);
			canvas.setyLineIndex(y);
		}
		if(mprType.equals(AImage.AXIAL) && IOT.equals(AImage.SAGITTAL)){
			int x = (int) (yn * width + 0.5);
			int y = (int) (zn * height + 0.5);
			int z = (int) (xn * canvas.getImages().size() + 0.5);

			canvas.setDoYLineUpdate(true);
			canvas.setDoXLineUpdate(true);
			
			canvas.setIndex(z);
			slider.setSelection(z);

			canvas.setxLineIndex(x);
			
			canvas.setyLineIndex(y);
		}
		if(mprType.equals(AImage.CORONAL) && IOT.equals(AImage.AXIAL)){
			int x = (int) (xn * width + 0.5);
			int y = (int) (zn * height + 0.5);
			int z = (int) (yn * canvas.getImages().size() + 0.5);

			canvas.setDoYLineUpdate(true);
			canvas.setDoXLineUpdate(true);
			
			canvas.setIndex(z);
			slider.setSelection(z);
			
			canvas.setxLineIndex(x);
			
			canvas.setyLineIndex(y);
		}
		if(mprType.equals(AImage.CORONAL) && IOT.equals(AImage.SAGITTAL)){
			int x = (int) (zn * width + 0.5);
			int y = (int) (yn * height + 0.5);
			int z = (int) (xn * canvas.getImages().size() + 0.5);

			canvas.setDoXLineUpdate(true);
			canvas.setDoYLineUpdate(true);

			slider.setSelection(z);
			canvas.setIndex(z);
			
			canvas.setyLineIndex(y);
			
			canvas.setxLineIndex(x);
		}
		if(mprType.equals(AImage.SAGITTAL) && IOT.equals(AImage.AXIAL)){
			int x = (int) (zn * width +0.5);
			int y = (int) (xn * height + 0.5);
			int z = (int) (yn * canvas.getImages().size() + 0.5);

			canvas.setDoXLineUpdate(true);
			canvas.setDoYLineUpdate(true);

			slider.setSelection(z);
			canvas.setIndex(z);
			
			canvas.setyLineIndex(y);
			
			canvas.setxLineIndex(x);
		}
		if(mprType.equals(AImage.SAGITTAL) && IOT.equals(AImage.CORONAL)){
			int x = (int) (zn * width + 0.5);
			int y = (int) (yn * height + 0.5);
			int z = (int) (xn * canvas.getImages().size() + 0.5);

			canvas.setDoXLineUpdate(true);
			canvas.setDoYLineUpdate(true);

			slider.setSelection(z);
			canvas.setIndex(z);

			canvas.setyLineIndex(y);
			
			canvas.setxLineIndex(x);
		}
		canvas.redraw();
	}
	
	@Override
	public void updateScoutingLine(int z, String mprType){
		String IOT = this.getCanvas().imageOrientationType;

		if(mprType.equals(AImage.AXIAL) && IOT.equals(AImage.CORONAL)){
			canvas.setDoYLineUpdate(true);
			canvas.setyLineIndex(z);
		}
		if(mprType.equals(AImage.AXIAL) && IOT.equals(AImage.SAGITTAL)){
			canvas.setDoYLineUpdate(true);
			canvas.setyLineIndex(z);
		}
		if(mprType.equals(AImage.CORONAL) && IOT.equals(AImage.AXIAL)){
			canvas.setDoYLineUpdate(true);
			canvas.setyLineIndex(z);
		}
		if(mprType.equals(AImage.CORONAL) && IOT.equals(AImage.SAGITTAL)){
			canvas.setDoXLineUpdate(true);
			canvas.setxLineIndex(z);
		}
		if(mprType.equals(AImage.SAGITTAL) && IOT.equals(AImage.AXIAL)){
			canvas.setDoXLineUpdate(true);
			canvas.setxLineIndex(z);
		}
		if(mprType.equals(AImage.SAGITTAL) && IOT.equals(AImage.CORONAL)){
			canvas.setDoXLineUpdate(true);
			canvas.setxLineIndex(z);
		}
		canvas.redraw();
	}
	
	public DicomObject getCurrentDicomObject(){
		DicomObject current = null;
		if(item.getLevel() == ADicomTreeItem.TREE_OBJECT_LEVEL){
			current = (DicomObject)item;
		}
		else {
			current = (DicomObject) item.getChild(canvas.getIndex());
		}
		return current;
	}
	
	public void removeSelection(){
		ArrayList<AImage> images = canvas.getImages();
		for(AImage ai : images){
			ai.deletePoints();
		}
		canvas.redraw();
	}
	
	public void removeSingleSelection(){
		int index = slider.getSelection();
		AImage ai = canvas.getImages().get(index);
		ai.deletePoints();
		canvas.redraw();
	}
	
	public void postErrorEvent(String error){
		IEventBroker broker = rootPart.getEventBroker();
		broker.post(EventConstants.PLUG_IN_ERROR, error);
	}
	/*public void recalulateImages() {
		// TODO Auto-generated method stub
		
	}*/
}
