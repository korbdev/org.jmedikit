package org.jmedikit.plugin.util;

public interface IObserver {
	public void update(int index);
	//public void updateScoutingLine(int xIndex, int yIndex, String mprType);
	public void updateScoutingLine(float x, float y, float z, String mprType);
	public void updateScoutingLine(int z, String mprType);
}
