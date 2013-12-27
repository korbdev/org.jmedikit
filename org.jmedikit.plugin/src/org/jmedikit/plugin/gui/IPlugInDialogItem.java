package org.jmedikit.plugin.gui;

import org.eclipse.swt.widgets.Composite;

public interface IPlugInDialogItem{
	public String getName();
	public Object getValue();
	public void setValue(Object o);
	public void getSWTObject(Composite parent);
}
