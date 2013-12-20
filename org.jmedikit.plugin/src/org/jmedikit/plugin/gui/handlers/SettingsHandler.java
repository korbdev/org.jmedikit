 
package org.jmedikit.plugin.gui.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.jmedikit.plugin.gui.PlugInPreferences;




public class SettingsHandler {
	
	@Inject @Named(IServiceConstants.ACTIVE_SHELL) 
	protected Shell shell;
	
	@Inject
	protected
	IResourcePool imagePool;
	
	@Execute 
	public void execute(){ 
		
		PreferenceManager pm = new PreferenceManager();

		IPreferenceNode plugInNode = new PreferenceNode("Plug-Ins", new PlugInPreferences());
		pm.addToRoot(plugInNode);

		IPreferenceNode colorNode = new PreferenceNode("Colors", new PlugInPreferences());
		pm.addToRoot(colorNode);
		
		PreferenceDialog dialog = new PreferenceDialog(shell, pm);
		
		dialog.setPreferenceStore(new ScopedPreferenceStore(ConfigurationScope.INSTANCE, "org.jmedikit.plugin"));
		dialog.create();
		
		dialog.open();
	} 	
}