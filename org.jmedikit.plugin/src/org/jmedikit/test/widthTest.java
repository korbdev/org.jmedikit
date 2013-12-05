package org.jmedikit.test;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class widthTest extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public widthTest(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		Spinner spinner = new Spinner(this, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1));
		
		Spinner spinner_1 = new Spinner(this, SWT.BORDER);
		spinner_1.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1));
		
		Spinner spinner_2 = new Spinner(this, SWT.BORDER);
		spinner_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
