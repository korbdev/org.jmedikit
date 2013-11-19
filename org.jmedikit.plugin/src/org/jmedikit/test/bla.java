package org.jmedikit.test;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class bla extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public bla(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setBounds(0, 0, 64, 64);
		
		Canvas canvas = new Canvas(composite, SWT.NONE);
		canvas.setBounds(100, 113, 64, 64);
		canvas.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setBounds(0, 0, 64, 64);
		
		ToolBar toolBar = new ToolBar(composite_1, SWT.FLAT | SWT.RIGHT | SWT.VERTICAL);
		toolBar.setLocation(0, 0);
		toolBar.setSize(170, 49);
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setText("New Item");
		
		ToolItem tltmTest = new ToolItem(toolBar, SWT.NONE);
		tltmTest.setText("test");
		
		Slider slider = new Slider(composite_1, SWT.VERTICAL);
		slider.setBounds(0, 136, 170, 144);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
