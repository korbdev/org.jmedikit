package org.jmedikit.plugin.gui;


import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.util.ImageProvider;
import org.jmedikit.plugin.tools.AToolFactory;


public class ImageViewComposite extends Composite{
	
	private DicomCanvas canvas;
	
	private Label label;
	
	private Composite parent, titlebar, canvasContainer, controls;
	
	private Slider slider;
	
	private Button plus, minus, close;
	
	private Text percentage;
	
	private IResourcePool imageResource;
	
	private ImageViewPart rootPart;
	
	private Image closeDark, minusDark, plusDark;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ImageViewComposite(Composite parent, int style, String title, DicomTreeItem selection, IResourcePool pool, ImageViewPart rootPart) {
		super(parent, style);
		this.parent = parent;
		this.imageResource = pool;
		this.rootPart = rootPart;
		
		closeDark = imageResource.getImageUnchecked(ImageProvider.CLOSE_DARK_BUTTON);
		
		minusDark = imageResource.getImageUnchecked(ImageProvider.MINUS_DARK_BUTTON);
		
		plusDark = imageResource.getImageUnchecked(ImageProvider.PLUS_DARK_BUTTON);
		
		GridLayout gridLayout = new GridLayout(1, false);
		//gridLayout.horizontalSpacing = 15;
		setLayout(gridLayout);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		setLayoutData(data);
		
		titlebar = new Composite(this, SWT.NONE);
		titlebar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		titlebar.setLayout(new GridLayout(2, false));
		
		label = new Label(titlebar, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		label.setText(title);

		close = new Button(titlebar, SWT.NONE);
		GridData gd_button_close = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_button_close.widthHint = 25;
		gd_button_close.heightHint = 25;
		close.setLayoutData(gd_button_close);
		close.setImage(closeDark);
		
		canvasContainer = new Composite(this, SWT.BORDER);
		canvasContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		canvasContainer.setLayout(new GridLayout(1, false));
		
		canvas = new DicomCanvas(canvasContainer, SWT.NO_BACKGROUND, selection);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		controls = new Composite(this, SWT.NONE);
		controls.setLayout(new GridLayout(1, false));
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		slider = new Slider(controls, SWT.BORDER);
		GridData gd_slider = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_slider.heightHint = 20;
		slider.setLayoutData(gd_slider);
		int sliderMaximum = selection.getChildren().size() > 0 ? selection.getChildren().size()-1 : 0;
		System.out.println(sliderMaximum);
		slider.setMaximum(sliderMaximum+slider.getThumb());
		
		/*minus = new Button(controls, SWT.NONE);
		GridData gd_button_1 = new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1);
		gd_button_1.widthHint = 25;
		gd_button_1.heightHint = 25;
		minus.setLayoutData( gd_button_1);
		minus.setImage(minusDark);
		
		percentage = new Text(controls, SWT.BORDER | SWT.CENTER);
		percentage.setText("100");
		GridData gd_text = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_text.heightHint = 15;
		gd_text.widthHint = 50;
		percentage.setLayoutData(gd_text);

		plus = new Button(controls, SWT.CENTER);
		GridData gd_button = new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1);
		gd_button.widthHint = 25;
		gd_button.heightHint = 25;
		plus.setLayoutData(gd_button);
		plus.setImage(plusDark);*/

		init();
	}

	private void initButtons(){
		close.addListener(SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				System.out.println("Close clicked");
				dispose();
				parent.layout();
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
		return label.getText();
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
