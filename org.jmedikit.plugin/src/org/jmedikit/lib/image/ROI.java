package org.jmedikit.lib.image;

public class ROI {
	
	public float x;
	public float y;
	
	public float width;
	public float height;
	
	public ROI(){
		x = 0f;
		y = 0f;
		width = 0f;
		height = 0f;
	}
	
	public ROI(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String toString(){
		return "("+x+", "+y+")("+width+", "+height+")";
	}
}
