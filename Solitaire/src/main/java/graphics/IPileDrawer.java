package graphics;

import com.jogamp.opengl.GL2;

import model.Pile;

public interface IPileDrawer {
	/**
	 * A method that draws a Pile object.
	 * @param gl a GL2 object that allows access to openGL functions.
	 * @param pile the Pile object to be drawn.
	 */
	public void drawPile(GL2 gl, Pile pile);
	
	/**
	 * A method to draw a green check mark over a given pile.
	 * @param gl a GL2 object that allows access to openGL functions.
	 * @param pile the Pile over which the check mark should be drawn.
	 */
	public void drawGreenCheck(GL2 gl, Pile pile);
}
