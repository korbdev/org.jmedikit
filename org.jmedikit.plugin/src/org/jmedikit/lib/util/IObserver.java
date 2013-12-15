package org.jmedikit.lib.util;

public interface IObserver {
	public void update(int index);
	public void updateScoutingLine(int xIndex, int yIndex, String mprType);
}
