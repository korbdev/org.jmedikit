package org.jmedikit.plugin.gui;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class PlugInPreferences extends FieldEditorPreferencePage{
	
	public static final String PLUGIN_DIRECTORY = "PLUGIN_DIRECTORY";
	
	DirectoryFieldEditor dir;
	Preferences prefs;
	
	String pluginDirectory;
	
	public PlugInPreferences() {
		super("Plug-In Optionen", GRID);
		setMessage("Plug-In Verzeichnis");
		setDescription("Bitte waehlen Sie hier das Verzeichnis, in dem Plug-In Klassen gespeichert werden.");
		
		prefs = ConfigurationScope.INSTANCE.getNode("org.jmedikit.plugin");
		pluginDirectory = prefs.get(PLUGIN_DIRECTORY, "");
	}



	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,  true, 1, 1));
		dir = new DirectoryFieldEditor("PATH", "Plug-In Verzeichnis", container);
		dir.setStringValue(pluginDirectory);
		addField(dir);
		return parent;
	}



	@Override
	protected void createFieldEditors() {
		
	}

	@Override
	protected void performApply(){
		super.performApply();
		System.out.println("hallo apply");
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("org.jmedikit.plugin");
		preferences.put(PLUGIN_DIRECTORY, dir.getStringValue());
		
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean performOk(){
		boolean returnVal = super.performOk();
		System.out.println("OK");
		//performApply();
		return returnVal;
	}
}
