package org.jmedikit.plugin.gui.tools;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.gui.DicomCanvas;

/**
 * Das Abstrakte Werkzeug definiert die grundlegenden Operationen, die ein Werkzeug zur Verf�gung stellen muss.
 * 
 * @author rkorb
 *
 */
public abstract class ATool {

	/**
	 * Die Zeichenfl�che f�r die Werkzeuge
	 */
	protected DicomCanvas canvas;
	
	/**
	 * start wird erfasst, sobald eine Maustaste gedr�ckt wird
	 */
	protected Point2D<Integer> start;
	
	/**
	 * actual liefert die aktuelle Position des Mauszeigers auf dem {@link DicomCanvas}
	 */
	protected Point2D<Integer> actual;
	
	/**
	 * end wird festgelegt, sobald zuvor gedr�ckte Maustaste losgelassen wird
	 */
	protected Point2D<Integer> end;
	
	/**
	 * true wenn eine Maustaste gedr�ckt ist
	 */
	protected boolean mouseDown;
	
	/**
	 * true wenn keine Maustaste gedr�ckt ist
	 */
	protected boolean mouseUp;
	
	/*public ATool(){
		System.out.println("Tool created");
		
		start = new Point2D<Integer>(0, 0);
		actual = new Point2D<Integer>(0, 0);
		end = new Point2D<Integer>(0, 0);
		
		mouseDown = false;
		mouseUp = true;
	}*/
	
	public ATool(DicomCanvas c){
		start = new Point2D<Integer>(0, 0);
		actual = new Point2D<Integer>(0, 0);
		end = new Point2D<Integer>(0, 0);
		
		mouseDown = false;
		mouseUp = true;
		canvas = c;
	}
	
	public void setCanvas(DicomCanvas c){
		canvas = c;
	}
	
	/**
	 * Diese Eventbehandlung wird ausgef�hrt, wenn die Maus bewegt wird
	 * 
	 * @param e
	 */
	public void handleMouseMove(Event e){
		actual.setPoint(e.x, e.y);
		actionMouseMove(e);
	}
	
	/**
	 * Diese Eventbehandlung wird ausgef�hrt, wenn eine Maustaste gedr�ckt wird. Die genaue Tastennummer kann aus dem Event e ausgelesen werden.
	 * 
	 * @param e
	 */
	public void handleMouseDown(Event e){
		mouseDown = true;
		mouseUp = false;
		start.setPoint(e.x, e.y);
		actionMouseDown(e);
	}
	
	/**
	 * Diese Eventbehandlung wird ausgef�hrt, wenn eine zuvor gedr�ckte Maustaste losgelassen wird
	 * @param e
	 */
	public void handleMouseUp(Event e){
		mouseDown = false;
		mouseUp = true;
		end.setPoint(e.x, e.y);
		actionMouseUp(e);
	}
	
	/**
	 * Diese Eventbehandlung wird ausgef�hrt, wenn die Maus das {@link DicomCanvas} betritt
	 * @param e
	 */
	public void handleMouseEnter(Event event) {
		actionMouseEnter(event);
	}

	/**
	 * Diese Eventbehandlung wird ausgef�hrt, wenn die Maus das {@link DicomCanvas} verl�sst
	 * @param e
	 */
	public void handleMouseExit(Event event) {
		actionMouseExit(event);
	}
	
	/**
	 * Diese Eventbehandlung wird ausgef�hrt, wenn das Mausrad bet�tigt wird. Die Drehrichtung muss aus dem Event e bestimmt werden.
	 * @param e
	 */
	public void handleMouseWheel(Event event){
		actionMouseWheel(event);
	}
	
	/**
	 * Konkrete Werkzeuge implementieren das Verhalten bei einer Mausbewegung �ber das {@link DicomCanvas}
	 * 
	 * @param e
	 */
	public abstract void actionMouseMove(Event e);
	
	/**
	 * Konkrete Werkzeuge implementieren das Verhalten bei einem Mausklick
	 * 
	 * @param e
	 */
	public abstract void actionMouseDown(Event e);

	/**
	 * Konkrete Werkzeuge implementieren das Verhalten beim Loslassen der zuvor gedr�ckten Maustaste
	 * 
	 * @param e
	 */
	public abstract void actionMouseUp(Event e);
	
	/**
	 * Konkrete Werkzeuge implementieren das Verhalten beim Betreten der Maus des {@link DicomCanvas}
	 * 
	 * @param e
	 */
	public abstract void actionMouseEnter(Event e);
	
	/**
	 * Konkrete Werkzeuge implementieren das Verhalten beim Verlassen der Maus des {@link DicomCanvas}
	 * 
	 * @param e
	 */
	public abstract void actionMouseExit(Event e);
	
	/**
	 * Konkrete Werkzeuge implementieren das Verhalten bei einer Bet�tigung des Mausrads
	 * 
	 * @param e
	 */
	public abstract void actionMouseWheel(Event e);
	
	/**
	 * Diese Methode wird vor der Koordinatenberechnung der Bilder im {@link DicomCanvas} aufgerufen. 
	 * Darf <b>nicht</b> null zur�ckgeben, da sonst ein Fehler auftritt.
	 * 
	 * @param toDraw
	 * @return 
	 */
	public abstract GC preCalculation(GC toDraw);
	
	
	/**
	 * Diese Methode wird nach dem Zeichenprozess im {@link DicomCanvas} aufgerufen. 
	 * Darf <b>nicht</b> null zur�ckgeben, da sonst ein Fehler auftritt.
	 * 
	 * @param toDraw
	 * @return 
	 */
	public abstract GC postCalculation(GC toDraw);

	
}
