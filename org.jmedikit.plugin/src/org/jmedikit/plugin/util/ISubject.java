package org.jmedikit.plugin.util;


public interface ISubject {
	public void registerObserver(IObserver o);
	public void removeObserver(IObserver o);
	public void notifyObservers(float x, float y, float z);
	public void notifyObservers(int z);
}
