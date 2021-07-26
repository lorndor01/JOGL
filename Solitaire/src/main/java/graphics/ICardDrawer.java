package graphics;

import com.jogamp.opengl.GL2;

import model.Card;

public interface ICardDrawer {
	/**
	 * A method that should draw a playing card using its attributes.
	 * @param gl a GL2 object which allows access to openGL functions.
	 * @param card the Card object to be drawn.
	 * @param numberOnPile which number on a pile this card is.
	 */
	public void drawCard(GL2 gl, Card card, int numberOnPile);
}
