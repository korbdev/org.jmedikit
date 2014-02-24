package org.jmedikit.lib.core;

import java.util.ArrayList;

/**
 * 
 * <p>Die Repräsentation einen Knotens im DICOM-Baum</p>
 * 
 * @author rkorb
 *
 */
public abstract class ADicomTreeItem{
	
	public static final int TREE_ROOT_LEVEL = 0;
	public static final int TREE_PATIENT_LEVEL = 1;
	public static final int TREE_STUDY_LEVEL = 2;
	public static final int TREE_SERIES_LEVEL = 3;
	public static final int TREE_OBJECT_LEVEL = 4;
	
	/**
	 * Unique Identifier des Knotens
	 */
	private String uid;
	
	/**
	 * Enthält den Elternknoten
	 */
	private ADicomTreeItem parent;
	
	/**
	 * Eine Liste der Kindknoten
	 */
	private ArrayList<ADicomTreeItem> children;
	
	/**
	 * Die Höhe des Knotens. Kann maximal 4 werden
	 */
	protected int level;
	
	/**
	 * Der Konstruktor erzeugt einen minimalen Baum.
	 * 
	 * @param uid
	 */
	public ADicomTreeItem(String uid) {
		this.uid = uid;
		level = TREE_ROOT_LEVEL;
		children = new ArrayList<ADicomTreeItem>();
	}
	
	/**
	 * <p> Diese Methode gibt die Höhe des Knotens zurück</p>
	 * @return level 
	 */
	public int getLevel(){
		return level;
	}
	
	/**
	 * <p>Gibt den Elternknoten zurück</p>
	 * 
	 * @return parent
	 */
	public ADicomTreeItem getParent(){
		return parent;
	}
	
	/**
	 * <p>Setzt den Elternknoten neu und verschiebt damit den Teilbaum</p>
	 * @param item neuer Elternknoten
	 */
	private void setParent(ADicomTreeItem item){
		parent = item;
	}
	
	/**
	 * <p>Gibt eine Liste aller Kindknoten zurück</p>
	 * 
	 * @return children
	 */
	public ArrayList<ADicomTreeItem> getChildren(){
		return children;
	}
	
	/**
	 * <p>Gibt das Kindelement des spezifischen Index zurück</p>
	 * 
	 * @param index spezifischer Kindknoten
	 * @return child at index
	 */
	public ADicomTreeItem getChild(int index){
		return children.get(index);
	}
	
	/**
	 * Gibt den Unique Identifier des Knotens zurück
	 * @return
	 */
	public String getUid(){
		return uid;
	}
	
	/**
	 * <p>Vergibt einen neuen Unique Identifier</p>
	 * 
	 * @param uid
	 */
	public void setUid(String uid){
		this.uid = uid;
	}
	
	/**
	 * <p>Fügt einen neuen Kindknoten in die Liste hinzu</p>
	 * @param item
	 * @return false wenn das Element schon existiert oder nicht eingefügt werden konnte
	 */
	public boolean addChild(ADicomTreeItem item){
		if(children.contains(item)){
			return false;
		}
		else{
			item.setParent(this);
			return children.add(item);
		}
	}
	
	/**
	 * <p> Die Methode löscht einen Kindknoten aus der List</p>
	 * 
	 * @param item zu entfernender Kindknoten
	 * @return true wenn der Kindknoten entfernt werden konnte
	 */
	public boolean removeChild(ADicomTreeItem item){
		return children.remove(item);
	}

	/**
	 * <p>Gibt die Anzahl der Kinder zurück</p>
	 * @return #Kindknoten
	 */
	public int size(){
		return children.size();
	}
	
	/**
	 * <p>Prüft, ob der Knoten ein Blatt ist</p>
	 * @return true wenn die Liste der Kinder leer ist
	 */
	public boolean isLeaf(){
		if(children.isEmpty()){
			return true;
		}
		else return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ADicomTreeItem other = (ADicomTreeItem) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
	
}
