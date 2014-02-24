package org.jmedikit;

import org.jmedikit.plugin.io.PlugInClassLoader;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author rkorb
 *
 * Der Activator wird vor dem Programmstart ausgeführt
 */
public class Activator implements BundleActivator{

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * Bei Plug-in-Start werden die Plug-ins der Anwender aus dem Plug-in Directory geladen
	 * 
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		PlugInClassLoader loader = PlugInClassLoader.getInstance();
		loader.loadPlugins();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
