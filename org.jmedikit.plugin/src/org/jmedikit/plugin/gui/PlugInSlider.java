package org.jmedikit.plugin.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

/**
 * Der Slider repräsentiert ein Element zur Auswahl von numerischen Werten in einem Intervall[min, max].
 * 
 * 
 * @author rkorb
 *
 */
public class PlugInSlider implements IPlugInDialogItem{

	private String name;
	
	private int value;
	
	private int min;
	
	private int max;
	
	private int start;
	
	private int end;
	
	private int range;
	
	private int increment;
	
	private int digits;
	
	/**
	 * 
	 * Erzeugt das Formularelement zur Intervallauswahl. Die Nachkommastellen bestimmen minimale und maximale Werte.
	 * Die Extremwerte werden mit der Formel min/(10^digits) und max/(10^digits) berechnet
	 * <p>Beispiele</p>
	 * <ul>
	 * <li>min = 10, max = 30, digits = 0 ergibt im Dialog das Intervall [10, 30] Beispielwert 14</li>
	 * <li>min = 10, max = 30, digits = 1 ergibt im Dialog das Intervall [1, 3], Beispielwert 1.4</li>
	 * <li>min = 10, max = 30, digits = 2 ergibt im Dialog das Intervall [0.1, 0.3], Beispielwert 0.14</li>
	 * <li>min = 100, max = 300, digits = 2 ergibt im Dialog das Intervall [1, 3], Beispielwert 1.14</li>
	 * </ul>
	 * 
	 * @param name Name des Elements
	 * @param startValue Startwert
	 * @param min Minimaler Wert
	 * @param max Maximaler Wert
	 * @param increment Inkrement
	 * @param digits Nachkommastellen
	 */
	public PlugInSlider(String name, int startValue, int min, int max, int increment, int digits){
		range = max - min;
		
		this.name = name;
		this.min = 0;
		this.max = range;
		start = min;
		end = max;
		value = startValue;
		this.increment = increment;
		this.digits = digits;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public Object getValue() {
		float div = (float) Math.pow(10, digits);
		return value/div;
	}

	@Override
	public void setValue(Object o) {
		float fac = (float) Math.pow(10, digits);
		value = (int)((float)o*fac+0.5);
	}

	@Override
	public void getSWTObject(Composite parent) {
		Composite c = new Composite(parent, SWT.BORDER);
		GridLayout grid = new GridLayout(1, true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		c.setLayout(grid);
		c.setLayoutData(data);
		
		Label label = new Label(c, SWT.NONE);
		label.setText(name);
		
		Composite sliderComposite = new Composite(c, SWT.NONE);
		sliderComposite.setLayout(new GridLayout(2, false));
		sliderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		
		final Label sliderValue = new Label(sliderComposite, SWT.CENTER);
		GridData sliderValueData = new GridData();
		sliderValueData.widthHint = 100;
		sliderValueData.verticalAlignment = SWT.CENTER;
		sliderValue.setLayoutData(sliderValueData);
		sliderValue.setText((float)getValue()+"");
		
		final Slider slider = new Slider(sliderComposite, SWT.HORIZONTAL);
		GridData sliderData = new GridData(SWT.FILL, SWT.CENTER, true, true, 0, 0);
		slider.setLayoutData(sliderData);
		slider.setMinimum(min);
		slider.setMaximum(max+slider.getThumb());
		slider.setIncrement(increment);
		slider.setSelection(value-start);
		
		slider.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				//float temp = (float)slider.getSelection();
				value = start + slider.getSelection();
				sliderValue.setText((float)getValue()+"");
			}
		});
	}

}
