package org.jmedikit.test;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.wb.swt.SWTResourceManager;

public class ctest extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ctest(Composite parent, int style) {
		super(parent, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		Canvas canvas = new Canvas(this, SWT.NONE);
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		canvas.setBounds(0, 0, 64, 64);
		
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite.setBounds(0, 0, 64, 64);
		
		Slider slider = new Slider(composite, SWT.VERTICAL);
		GridData gd_slider = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_slider.widthHint = 20;
		slider.setLayoutData(gd_slider);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
