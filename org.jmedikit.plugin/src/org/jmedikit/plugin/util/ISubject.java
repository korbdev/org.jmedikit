package org.jmedikit.plugin.util;


public interface ISubject {
	public void registerObserver(IObserver o);
	public void removeObserver(IObserver o);
	public void notifyObservers(int x, int y, int z);
}