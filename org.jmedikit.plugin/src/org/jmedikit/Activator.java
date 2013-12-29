package org.jmedikit;

import java.io.File;
import java.net.URL;
import org.jmedikit.plugin.io.PlugInClassLoader;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator{

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		File f = new File("C:\\Users\\rkorb\\Desktop\\plugins\\Testplugin\\bin\\");
	    URL url = f.toURI().toURL();       // file:/c:/myclasses/
	    URL[] urls = new URL[]{url};
		
		/*URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
	    Class<?> c;
	    Object paramsObj[] = {};
		try {
			c = loader.loadClass("main.abc.JmkPlgn");
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
		loader.close();*/
		
		PlugInClassLoader loader = PlugInClassLoader.getInstance();
		loader.loadPlugins();
		/*System.out.println("ORG.JMEDIKIT.PLUGIN STARTUP");
		
		File f = new File("C:\\Users\\rkorb\\Desktop\\plugins\\Testplugin\\bin\\");
		System.out.println(f.getAbsolutePath()+", "+f.getPath()+", "+System.getProperty("user.dir"));
		//JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// Convert File to a URL
	    URL url = new URL("file://"+f.getPath());          // file:/c:/myclasses/
	    System.out.println(url.getPath());
	    url = f.toURI().toURL();       // file:/c:/myclasses/
	    System.out.println(url.getPath());
	    URL[] urls = new URL[]{url};
		
	    URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
	    Class<?> c;
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

		MessageDialog.openConfirm(new Shell(), "Confirm", "Please confirm");
		
	    File[] javaFiles = new File[]{f};  
	      
	      
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();  
	    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);  
	      
	    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFiles));  
	      
	    List<String> optionList = new ArrayList<String>();  
	    optionList.addAll(Arrays.asList("-classpath", "C:\\Users\\rkorb\\Desktop\\jmedikit\\jmedikit\\plugins\\org.jmedikit.plugin_1.0.0.201312212324.jar"));  
	    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnits);  
	    System.out.println(task.call());*/
	    //compiler.run(null, null, null, javaFiles[0].getPath());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
