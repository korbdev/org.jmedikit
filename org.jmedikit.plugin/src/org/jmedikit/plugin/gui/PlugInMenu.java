 
package org.jmedikit.plugin.gui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MExpression;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.jmedikit.plugin.io.PlugInClassLoader;

public class PlugInMenu {
	
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
		
		/*Object o = loader.instantiate("main.abc.__JmkPlgn");
		Method m = o.getClass().getMethod("run");
		try {
			m.invoke(o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println("HALLo DYN");
		
		MDirectMenuItem item = MMenuFactory.INSTANCE.createDirectMenuItem();

		File f = new File("C:\\Users\\rkorb\\Desktop\\plugins\\Testplugin\\bin\\");
		System.out.println(f.getAbsolutePath()+", "+f.getPath()+", "+System.getProperty("user.dir"));

	    URL url = null;
		try {
			url = f.toURI().toURL();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}       // file:/c:/myclasses/
	    System.out.println(url.getPath());
	    URL[] urls = new URL[]{url};
	    
		URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
	    Class<?> c = null;
	    Object paramsObj[] = {};
		try {
			c = loader.loadClass("JmkPlgn");
			try {
				Object o = c.newInstance();
				System.out.println(o.getClass().getName());
				Method m = o.getClass().getMethod("print");
				m.invoke(o);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loader.close();
		//popup.setLabel("TEST");
		//popup.
		//items.add(popup);
		item.setLabel(c.getName());
		items.add(item);
		item.setContributorURI("platform://plugin/org.jmedikit.plugin");
		item.setContributionURI("bundleclass://org.jmedikit.plugin/org.jmedikit.plugin.gui.handlers.ToolPointHandler");
		
		items.add(item);*/
	}
		
}