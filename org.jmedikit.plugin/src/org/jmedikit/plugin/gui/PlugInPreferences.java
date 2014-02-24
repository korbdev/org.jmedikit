package org.jmedikit.plugin.gui;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jmedikit.plugin.util.PreferencesConstants;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Diese Klasse repräsentiert den Einstellungsdialog für Plug-in-Optionen. Hier wird zum Beispiel der Pfad zum Plug-in
 * Verzeichnis gesetzt
 * 
 * @author rkorb
 *
 */
public class PlugInPreferences extends FieldEditorPreferencePage{
	
	/**
	 * Formularfeld um Dateien zu öffnen
	 */
	private DirectoryFieldEditor dir;
	
	/**
	 * Einstellungsmanager zum Auslesen der Einstellungen
	 */
	private Preferences prefs;
	
	/**
	 * Enthält den Pfad zum Plug-in-Verzeichnis, das vom Anwender gesetzt wurde
	 */
	private String pluginDirectory;
	
	/**
	 * Erzeugt die Plug-in-Einstellungen und legt den Gültigkeitsbereich fest. Einstellungen bleiben auch nach dem Schließen der Anwendung erhalten.
	 */
	public PlugInPreferences() {
		super("Plug-In Optionen", GRID);
		setMessage("Plug-In Verzeichnis");
		setDescription("Bitte waehlen Sie hier das Verzeichnis, in dem Plug-In Klassen gespeichert werden.");
		
		prefs = ConfigurationScope.INSTANCE.getNode("org.jmedikit.plugin");
		pluginDirectory = prefs.get(PreferencesConstants.PLUGIN_DIRECTORY, "");
	}



	/**
	 * Generiert den Dialog und setzt die Formularelemente zusammen
	 */
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

	/**
	 * Wird ausgeführt, wenn der Anwender auf Apply clickt.
	 */
	@Override
	protected void performApply(){
		super.performApply();
	}
	
	/**
	 * Nach einem Klick auf OK werden die neuen Einstellungen übernommen. Ein klick auf OK löst gleichzeitig {@link PlugInPreferences#performApply()} aus.
	 */
	@Override
	public boolean performOk(){
		boolean returnVal = super.performOk();
		System.out.println("OK");
		
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("org.jmedikit.plugin");
		preferences.put(PreferencesConstants.PLUGIN_DIRECTORY, dir.getStringValue());
		
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		return returnVal;
	}
}
