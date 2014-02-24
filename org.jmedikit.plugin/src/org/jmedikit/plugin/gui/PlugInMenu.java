 
package org.jmedikit.plugin.gui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.util.HashMap;
import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import org.jmedikit.plugin.io.PlugInClassLoader;

/**
 * Diese Klasse stellt das Menü der Plug-ins zusammen
 * 
 * @author rkorb
 *
 */
public class PlugInMenu {
	
	/**
	 * Das Plug-in-Menü wird aus den geladenen Plug-ins zusammengebaut
	 * 
	 * @param items
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) throws NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException {
		
		PlugInClassLoader loader = PlugInClassLoader.getInstance();
		
		HashMap<URL, String> plugins = (HashMap<URL, String>) loader.getPlugIns();
		for(String s : plugins.values()){
			System.out.println("loaded "+s);
			MDirectMenuItem item = MMenuFactory.INSTANCE.createDirectMenuItem();
			item.setLabel(s);
			item.setContributorURI("platform://plugin/org.jmedikit.plugin");
			item.setContributionURI("bundleclass://org.jmedikit.plugin/org.jmedikit.plugin.gui.handlers.PlugInMenuHandler");
			item.setElementId(s);
			items.add(item);
		}
	}
		
}