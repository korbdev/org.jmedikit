package org.jmedikit.lib.core;

import java.util.ArrayList;

public abstract class ADicomTreeItem{
	
	public static final int TREE_ROOT_LEVEL = 0;
	public static final int TREE_PATIENT_LEVEL = 1;
	public static final int TREE_STUDY_LEVEL = 2;
	public static final int TREE_SERIES_LEVEL = 3;
	public static final int TREE_OBJECT_LEVEL = 4;
	
	private String uid;
	
	private ADicomTreeItem parent;
	
	private ArrayList<ADicomTreeItem> children;
	
	protected int level;
	
	public ADicomTreeItem(String uid) {
		this.uid = uid;
		level = TREE_ROOT_LEVEL;
		children = new ArrayList<ADicomTreeItem>();
	}
	
	public int getLevel(){
		return level;
	}
	
	public ADicomTreeItem getParent(){
		return parent;
	}
	
	private void setParent(ADicomTreeItem item){
		parent = item;
	}
	
	public ArrayList<ADicomTreeItem> getChildren(){
		return children;
	}
	
	public ADicomTreeItem getChild(int index){
		return children.get(index);
	}
	
	public String getUid(){
		return uid;
	}
	
	public void setUid(String uid){
		this.uid = uid;
	}
	
	public boolean addChild(ADicomTreeItem item){
		if(children.contains(item)){
			return false;
		}
		else{
			item.setParent(this);
			return children.add(item);
		}
	}
	
	public boolean removeChild(ADicomTreeItem item){
		return children.remove(item);
	}

	public int size(){
		return children.size();
	}
	
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
