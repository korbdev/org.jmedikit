package org.jmedikit.plugin.gui;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class PlugInDialog extends TitleAreaDialog{

	public static final int OK_STATUS = TitleAreaDialog.OK;
	public static final int CANCEL_STATUS = TitleAreaDialog.CANCEL;
	
	String title;
	
	String message;
	
	public ArrayList<IPlugInDialogItem> items;
	
	public PlugInDialog(String title, String message) {
		super(ImageViewPart.getPartShell());
		this.title = title;
		this.message = message;
		items = new ArrayList<IPlugInDialogItem>();
	}

	public Object getItemValue(String name){
		for(IPlugInDialogItem item : items){
			if(item.getName().equals(name)){
				return item.getValue();
			}
		}
		return null;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(title);
		setMessage(message);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		for(IPlugInDialogItem item : items){
			item.getSWTObject(container);
		}
		return area;
	}
	
	public void addStringItem(String name, String defaultValue){
		items.add(new PlugInStringValue(name, defaultValue));
	}
	
	public void addSlider(String name, int defaultValue, int min, int max, int increment, int digits){
		items.add(new PlugInSlider(name, defaultValue, min, max, increment, digits));
	}
	
	public void addRadioGroup(String name, String[] labels, int selectedIndex){
		items.add(new PlugInRadioGroup(name, labels, selectedIndex));
	}
}
