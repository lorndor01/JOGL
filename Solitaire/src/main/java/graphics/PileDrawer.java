package graphics;

import java.util.ArrayList;

import org.joml.Vector2d;

import com.jogamp.opengl.GL2;

import lib342.Colors;
import lib342.opengl.Constants.PrimitiveType;
import model.AcePile;
import model.Card;
import model.DeckPile;
import model.Pile;
import model.TemporaryPile;
import mygraphicslib.GLUtilities;

public class PileDrawer implements IPileDrawer{
	private static PileDrawer pileDrawer;
	
	/**
	 * Draws a given pile.
	 * @param gl a GL2 object that allows access to openGL functions.
	 * @param pile the Pile object to be drawn.
	 */
	public void drawPile(GL2 gl, Pile pile) {
		ArrayList<Card> cards = pile.getCards();
		CardDrawer drawer = new CardDrawer();
		if(pile instanceof TemporaryPile) {
			for(int i =0; i<cards.size(); i++) {
				Card card = cards.get(i);
				drawer.drawCard(gl, card, i);
			}
		}
		else {
			for(int i =0; i<cards.size(); i++) {
				Card card = cards.get(i);
				drawer.drawCard(gl, card, 0);
			}
		}
	}

	/**
	 * Returns an instance of the PileDrawer class.
	 * @return pileDrawer a PileDrawer instance.
	 */
	public static PileDrawer getInstance() {
		// TODO Auto-generated method stub
		if(pileDrawer == null) {
			pileDrawer = new PileDrawer();
		}
		return pileDrawer;
	}

	/**
	 * Draws a green check mark over a given pile.
	 * @param gl a GL2 object that allows access to openGL functions.
	 * @param pile the Pile object over which a green check mark should be drawn.
	 */
	public void drawGreenCheck(GL2 gl, Pile pile) {
		gl.glLineWidth(10f);
		gl.glColor3fv(Colors.GREEN, 0);
		double xLoc = pile.getLocation().getX();
		double yLoc = pile.getLocation().getY();
		double r = pile.getTopCard().CARD_WIDTH/2;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, xLoc, yLoc, r, 0, 360);
		}
		gl.glEnd();
		
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			Vector2d point = GLUtilities.pointOnCircle(xLoc, yLoc, r, 50);
			gl.glVertex2d(point.x, point.y);
			point = GLUtilities.pointOnCircle(xLoc, yLoc, r, 270);
			gl.glVertex2d(point.x, point.y);
			point = GLUtilities.pointOnCircle(xLoc, yLoc, r/2, 200);
			gl.glVertex2d(point.x, point.y);
		}
		gl.glEnd();
	}
}
