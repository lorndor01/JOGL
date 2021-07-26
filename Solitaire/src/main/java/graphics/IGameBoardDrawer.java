package graphics;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public interface IGameBoardDrawer {
	/**
	 * A method that draws a game board.
	 * @param gl a GL2 object that allows access to openGL functions.
	 */
	public void drawGameBoard(GL2 gl);
}
