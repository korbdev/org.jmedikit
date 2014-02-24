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
import org.jmedikit.lib.core.APlugIn2D;
import org.jmedikit.lib.core.APlugIn3D;
import org.jmedikit.plugin.util.PreferencesConstants;
import org.osgi.service.prefs.Preferences;

/**
 * Der Plug-in-Manager des Architekturmusters. Diese Klasse ist als Singleton implementiert, um eine Instantiierung über mehrere Classloader und 
 * die dadurch entstehenden Komplikationen zu vermeiden. Der Manager übernimmt neben dem initialen Lader der Plug-in-Klassen auch die Instantiierung
 * der konkreten Plug-in-Objekte
 * 
 * @author rkorb
 *
 */
public class PlugInClassLoader {
	
	/**
	 * Prefix, der die Hauptklasse eines Plug-ins und den Root-Ordner des Plug-ins kenntlich macht
	 */
	public static final String PREFIX = "__";
	
	/**
	 * String muss mit Prefix beginnen. Danach folgt ein Grossbuchstabe von A-Z oder ein Kleinbuchstabe von a-z, gefolgt von beliebigen Zeichen
	 * <ul>wenn PREFIX = __ dann
	 * <li>__Test = true</li>
	 * <li>__T = true</li>
	 *<li>_Test = false</li>
	 * <li>__test = true</li>
	 * </ul>
	 */
	public static final String PATTERN = "^"+PREFIX+"[A-Za-z0-9].*";
	
	/**
	 * Die private einzige Instanz des Plug-in-Managers
	 */
	private static PlugInClassLoader loader = new PlugInClassLoader();
	
	/**
	 * Pfad zum Plug-in-Verzeichnis
	 */
	private Preferences prefs;
	
	/**
	 * Classloader zum Laden der Plug-ins
	 */
	private URLClassLoader classloader;
	
	/**
	 * File zum Plug-in-Verzeichnis
	 */
	private File f;
	
	/**
	 * Die Menge der Plug-ins, Enthalt Pfad zum Plug-in als {@link URL} und den Namen der Hauptklasse als String
	 */
	private Map<URL, String> plugins;
	
	/**
	 * true wenn Plug-ins bereits vom Classloader geladen wurden
	 */
	private boolean loaded;
	
	/**
	 * Privater Konstruktor des Managers um eine explizite Objekterzeugung zu verhindern. Sucht bei der Instantiierung nach
	 * den verfügbaren Plug-ins
	 * 
	 */
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
	
	/**
	 * Diese Methode sucht anhand des Plug-in-Verzeichnisses nach Ordner die Plug-ins enthalten und baut den Namen der Hauptklassen zusammen. Der voll qualifizierte Klassenname
	 * muss für eine Instantiierung bekannt sein. Der volle Klassenname setzt sich aus den Packages und dem tatsächlichen Klassennamen zusammen.
	 * 
	 * @throws MalformedURLException
	 */
	private void locatePluginFolders() throws MalformedURLException{
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(File file : files){
				if(file.isDirectory() && file.getName().matches(PATTERN)){
					//System.out.println(file.getName());
					URL url = file.toURI().toURL();
					//System.out.println("Found "+file.getPath());
					List<File> mainClasses = new ArrayList<File>();
					locatePlugInMainClass(file, mainClasses, file.getName());
					
					if(mainClasses.size() > 0){
						File classFile = mainClasses.get(0);
						String parent = file.getPath();
						String path = filenameWithoutExtension(classFile.getName());
						
						//Baut den Klassennamen aus den Packages zusammen
						while(!classFile.getParent().equals(parent)){
							classFile = new File(classFile.getParent());
							path = classFile.getName()+"."+path;
							System.out.println(path);
						}
						plugins.put(url, path);
					}
				}
			}
		}
	}
	
	/**
	 * Sucht innerhalb eines Plug-in-Ordner nach der Hauptklasse und fügt diese der Liste hinzu
	 * 
	 * @param plugInRootDir Wurzelverzeichnis des Plug-ins
	 * @param mainClasses Liste der Hauptklassen, wobei nur die erste gefundene Hauptklasse zählt
	 * @param pluginName Name des Plug-ins
	 */
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
	
	/**
	 * Gibt den Dateinamen ohne Dateierweiterung zurück
	 * 
	 * @param filename
	 * @return
	 */
	private String filenameWithoutExtension(String filename){
		int pos = filename.lastIndexOf(".");
		if(pos > 0){
			return filename.substring(0, pos);
		}
		else return filename;
	}
	
	
	/**
	 * Diese Methode lädt die zuvor ermittelten Plug-ins mit Hilfe des {@link URLClassLoader}s. 
	 * 
	 * @return true wenn Plug-ins geladen werden oder bereits zuvor geladen wurden, sonst false
	 */
	public boolean loadPlugins(){
		if(loaded == true){
			return true;
		}
		else if(loaded == false){
			//System.out.println(plugins.keySet().toArray(new URL[0]).getClass().getName());
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
	
	/**
	 * Löscht die geladenen Plug-ins
	 * 
	 * @return
	 */
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
	 * Methode zur Instantiierung der konkreten Plug-in-Objekte. 
	 * Gibt null zurueck, wenn die zu Superklasse der Superklasse nicht vom Typ {@link APlugIn}, da von {@link APlugIn} nochmals zwischen {@link APlugIn2D} und {@link APlugIn3D} differenziert wird.
	 * 
	 * @param pluginName
	 * @return
	 */
	public Object instantiate(String pluginName){
		try {
			Class<?> c = classloader.loadClass(pluginName);
			Class<?> superC = c.getSuperclass();
			System.out.println(superC.getName());
			if(superC.getSuperclass().getName().equals(APlugIn.class.getName())){
				//System.out.println("Erzeuge obj");
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
	
	/**
	 * Gibt die Menge der gefundenen Plug-in-Strukturen zurück.
	 * 
	 * @return
	 */
	public Map<URL, String> getPlugIns(){
		return plugins;
	}
	
	/**
	 * Gibt eine Liste der gefundenen Namen der Hauptklassen aller Plug-ins zurück.
	 * 
	 * @return
	 */
	public String[] getPlugInMainClassNames(){
		return plugins.entrySet().toArray(new String[0]);
	}
	
	/**
	 * Gibt eine Liste der gefundenen {@link URL}s der Hauptklassen aller Plug-ins zurück.
	 * 
	 * @return
	 */
	public URL[] getPlugInUrls(){
		return plugins.keySet().toArray(new URL[0]);
	}
	
	/**
	 * Gibt die einzige Instanz des Plug-in-Managers zurück.
	 * @return
	 */
	public static PlugInClassLoader getInstance(){
		return loader;
	}
}
