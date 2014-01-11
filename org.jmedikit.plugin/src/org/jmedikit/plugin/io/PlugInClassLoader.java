package org.jmedikit.plugin.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.jmedikit.lib.core.APlugIn;

import org.jmedikit.plugin.util.PreferencesConstants;
import org.osgi.service.prefs.Preferences;

public class PlugInClassLoader {
	
	public static final String PREFIX = "__";
	
	/**
	 * String muss mit Prefix beginnen. Danach folgt ein Grossbuchstabe von A-Z, gefolgt von beliebigen Zeichen
	 * wenn PREFIX = __
	 * __Test = true
	 * __T = true
	 * _Test = false
	 * __test = false
	 */
	public static final String PATTERN = "^"+PREFIX+"[A-Z].*";
	
	private static PlugInClassLoader loader = new PlugInClassLoader();
	
	private Preferences prefs;
	
	private URLClassLoader classloader;
	
	private File f;
	
	private Map<URL, String> plugins;
	
	private boolean loaded;
	
	private PlugInClassLoader(){
		prefs = ConfigurationScope.INSTANCE.getNode("org.jmedikit.plugin");
		plugins = new HashMap<URL, String>();
		String rootPath = prefs.get(PreferencesConstants.PLUGIN_DIRECTORY, "");
		loaded = false;
		try {
			f = new File(rootPath);
			locatePluginFolders();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}	
	}
	
	private void locatePluginFolders() throws MalformedURLException{
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(File file : files){
				if(file.isDirectory() && file.getName().matches(PATTERN)){
					System.out.println(file.getName());
					URL url = file.toURI().toURL();
					System.out.println("Found "+file.getPath());
					List<File> mainClasses = new ArrayList<File>();
					locatePlugInMainClass(file, mainClasses, file.getName());
					
					if(mainClasses.size() > 0){
						File classFile = mainClasses.get(0);
						String parent = file.getPath();
						String path = filenameWithoutExtension(classFile.getName());
						
						while(!classFile.getParent().equals(parent)){
							classFile = new File(classFile.getParent());
							path = classFile.getName()+"."+path;
						}
						plugins.put(url, path);
					}
				}
			}
		}
	}
	
	private void locatePlugInMainClass(File plugInRootDir, List<File> mainClasses, String pluginName){
		if(plugInRootDir.isDirectory()){
			File[] files = plugInRootDir.listFiles();
			for(File f : files){
				locatePlugInMainClass(f, mainClasses, pluginName);
			}
		}
		else{
			String filename = filenameWithoutExtension(plugInRootDir.getName());
			if(filename.equals(pluginName)){
				mainClasses.add(plugInRootDir);
			}
		}
	}
	
	private String filenameWithoutExtension(String filename){
		int pos = filename.lastIndexOf(".");
		if(pos > 0){
			return filename.substring(0, pos);
		}
		else return filename;
	}
	
	public boolean loadPlugins(){
		if(loaded == true){
			return true;
		}
		else if(loaded == false){
			System.out.println(plugins.keySet().toArray(new URL[0]).getClass().getName());
			URL[] urls = (URL[]) plugins.keySet().toArray(new URL[0]);
			classloader = new URLClassLoader(urls, getClass().getClassLoader());
			loaded = true;
			return true;
		}
		else{
			loaded = false;
			return false;
		}
	}
	
	public boolean removePlugins(){
		if(loaded == true){
			try {
				classloader.close();
				loaded = false;
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		else return true;
	}
	
	/**
	 * Gibt null zurueck, wenn die zu ladende Klasse keine Superklasse von org.jmedikit.lib.PlugIn ist
	 * 
	 * @param pluginName
	 * @return
	 */
	public Object instantiate(String pluginName){
		try {
			Class<?> c = classloader.loadClass(pluginName);
			Class<?> superC = c.getSuperclass();
			System.out.println(superC.getName());
			if(superC.getName().equals(APlugIn.class.getName())){
				System.out.println("Erzeuge obj");
				
				//Class<?>[] args = new Class<?>[]{AImage.class};

				//Constructor<?> constructor = c.getConstructor(args);
				//Object o = constructor.newInstance(img);
				Object o = c.newInstance();
				return o;
			}
			
		}catch(ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<URL, String> getPlugIns(){
		return plugins;
	}
	
	public String[] getPlugInMainClassNames(){
		return plugins.entrySet().toArray(new String[0]);
	}
	
	public URL[] getPlugInUrls(){
		return plugins.keySet().toArray(new URL[0]);
	}
	
	public static PlugInClassLoader getInstance(){
		return loader;
	}
}
