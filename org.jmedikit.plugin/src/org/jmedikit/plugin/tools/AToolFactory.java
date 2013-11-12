package org.jmedikit.plugin.tools;

import org.jmedikit.plugin.gui.DicomCanvas;


public abstract class AToolFactory {
	
	public ATool createTool(String toolname, DicomCanvas c){
		ATool tool = produce(toolname, c);
		return tool;
	}
	
	protected abstract ATool produce(String toolname, DicomCanvas c);
}
