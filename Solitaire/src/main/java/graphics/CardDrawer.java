package graphics;


import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import lib342.Colors;
import lib342.opengl.Constants.PrimitiveType;
import model.Card;
import model.Denomination;
import model.PileLocation;
import model.Suit;
import mygraphicslib.GLUtilities;

public class CardDrawer implements ICardDrawer{
	private PileLocation location;
	//Radius of the card's rounded corners.
	private static final double RADIUS = Card.CORNER_RADIUS;
	
	/**
	 * Creates a new CardDrawer object.
	 */
	public CardDrawer() {
		
	}
	
	/**
	 * Draws a playing card.
	 * @param gl the GL2 object which provides access to openGL functions.
	 * @param card the card to be drawn.
	 * @param numberOnPile if the card should be drawn shifted down then this number should be greater than w
	 */
	public void drawCard(GL2 gl, Card card, int numberOnPile) {
		location = card.getLocation();
		if(card.isFaceUp()) {
			drawFaceUp(gl, card, numberOnPile);
		}
		else {
			drawFaceDown(gl, card, numberOnPile);
		}
	}

	private void drawFaceUp(GL2 gl, Card card, int numberOnPile) {
		double x = card.getLocation().getX();
		double y = card.getLocation().getY();
		double shiftY = -numberOnPile*.25;
		y = y+shiftY;
		card.setDrawnLocation(x, y);
		
		drawDenominationSymbol(gl, card.getDenomination(), card.getSuit(), card.getDrawnLocation().x, card.getDrawnLocation().y);
		drawSuitSymbol(gl, card.getSuit(), card.getDrawnLocation().x, card.getDrawnLocation().y);
	}
	
	private void drawFaceDown(GL2 gl, Card card, int numberOnPile) {
		
		double width = card.CARD_WIDTH;
		double height = card.CARD_HEIGHT;
		double x = location.getX();
		double y = location.getY();
		double shiftY = -numberOnPile*.25;
		y = y+shiftY;
		card.setDrawnLocation(x, y);
		x = card.getDrawnLocation().x;
		y = card.getDrawnLocation().y;

		gl.glColor3fv(Colors.RED, 0);

		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawRoundedRectangle(gl, x, y, width, height, RADIUS);
		}
		gl.glEnd();
		gl.glColor3fv(Colors.WHITE,0);
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawRoundedRectangle(gl, x, y, width, height, RADIUS);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINES.getValue());
		{
			for(int i =1; i<6; i++) {
				gl.glVertex2d(x-(width/2), (y-(height/2))+i*(height/6));
				gl.glVertex2d(x+(width/2), (y-(height/2))+i*(height/6));
			}
			for(int i=1; i<4; i++) {
				gl.glVertex2d((x-(width/2))+i*(width/4), y-(height/2));
				gl.glVertex2d((x-(width/2))+i*(width/4), y+(height/2));
			}
		}
		gl.glEnd();
	}

	private void drawSuitSymbol(GL2 gl, Suit suit, double x, double y) {
		switch(suit) {
		case Heart:
			drawHeart(gl, suit, x, y);
			break;
		case Spade:
			drawSpade(gl, suit, x, y);
			break;
		case Diamond:
			drawDiamond(gl, suit, x, y);
			break;
		case Club:
			drawClub(gl, suit, x, y);
			break;
		}
	}
	
	private void drawDenominationSymbol(GL2 gl, Denomination deno, Suit suit, double x, double y) {
		double width = Card.CARD_WIDTH;
		double height = Card.CARD_HEIGHT;
		gl.glColor3fv(Colors.WHITE, 0);
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawRoundedRectangle(gl, x, y, width, height, RADIUS);
		}
		gl.glEnd();
		if(suit.getValue() == true) {
			gl.glColor3fv(Colors.RED, 0);
		}
		else {
			gl.glColor3fv(Colors.BLACK, 0);
		}
		switch(deno) {
		case Two:
			drawTwo(gl, deno, x, y);
			break;
		case Three:
			drawThree(gl, deno, x, y);
			break;
		case Four:
			drawFour(gl, deno, x, y);
			break;
		case Five:
			drawFive(gl, deno, x, y);
			break;
		case Six:
			drawSix(gl, deno, x, y);
			break;
		case Seven:
			drawSeven(gl, deno, x, y);
			break;
		case Eight:
			drawEight(gl, deno, x, y);
			break;
		case Nine:
			drawNine(gl, deno, x, y);
			break;
		case Ten:
			drawTen(gl, deno, x, y);
			break;
		case Jack:
			drawJack(gl, deno, x, y);
			break;
		case Queen:
			drawQueen(gl, deno, x, y);
			break;
		case King:
			drawKing(gl, deno, x, y);
			break;
		case Ace:
			drawAce(gl, deno, x ,y);
			break;
		}
	}
	
	private void drawTwo(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
		gl.glVertex2d(topXCor, topYCor);
		gl.glVertex2d(topXCor-.1, topYCor);
		GLUtilities.drawArc(gl, x-.35, topYCor+.15, .05, 0, 180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor+.1,bottomYCor-.2);
			gl.glVertex2d(bottomXCor,bottomYCor-.2);
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.05, .05, 0, 180);
		}
		gl.glEnd();
	}
	
	private void drawThree(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.05, .05, -120, 210);
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.15, .05, -90, 210);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.05, .05, 120, -210);
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.15, .05, 90, -210);
		}
		gl.glEnd();
	}
	
	private void drawFour(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor, topYCor);
			gl.glVertex2d(topXCor, topYCor+.2);
			gl.glVertex2d(topXCor, topYCor+.1);
			gl.glVertex2d(topXCor-.1, topYCor+.1);
			gl.glVertex2d(topXCor-.1, topYCor+.2);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor, bottomYCor);
			gl.glVertex2d(bottomXCor, bottomYCor-.1);
			gl.glVertex2d(bottomXCor+.1, bottomYCor-.1);
			gl.glVertex2d(bottomXCor+.1, bottomYCor);
			gl.glVertex2d(bottomXCor+.1, bottomYCor-.2);
		}
		gl.glEnd();
	}

	private void drawFive(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, topXCor-.06, topYCor+.06, .06, -90, 180);
			gl.glVertex2d(topXCor-.06,topYCor+.2);
			gl.glVertex2d(topXCor,topYCor+.2);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor+.12, bottomYCor);
			gl.glVertex2d(bottomXCor+.06, bottomYCor);
			gl.glVertex2d(bottomXCor+.06, bottomYCor-.08);
			GLUtilities.drawArc(gl, bottomXCor+.06, bottomYCor-.14, .06, 90, -180);
		}
		gl.glEnd();
	}
	
	private void drawSix(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.05, .05, -180, 360);
			gl.glVertex2d(topXCor-.03, topYCor+.2);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor+.07, bottomYCor);
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.15, .05,-180, 360);
		}
		gl.glEnd();
	}
	
	private void drawSeven(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor-.08, topYCor);
			gl.glVertex2d(topXCor, topYCor+.2);
			gl.glVertex2d(topXCor-.1, topYCor+.2);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor, bottomYCor);
			gl.glVertex2d(bottomXCor+.1, bottomYCor);
			gl.glVertex2d(bottomXCor+.02, bottomYCor-.2);
		}
		gl.glEnd();
	}
	
	private void drawEight(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.05, .05, 90, 360);
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.15, .05, -90, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.05, .05, -90, 360);
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.15, .05, 90, 360);
		}
		gl.glEnd();
	}
	
	private void drawNine(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor-.07, topYCor);
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.15, .05, 0, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.05, .05, 0, 360);
			gl.glVertex2d(bottomXCor+.03, bottomYCor-.2);
		}
		gl.glEnd();

	}
	
	private void drawTen(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor-.1, topYCor);
			gl.glVertex2d(topXCor-.1, topYCor+.2);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
		{
			GLUtilities.drawArc(gl, topXCor-.025, topYCor+.025, .025, 180, 180);
			gl.glVertex2d(topXCor, topYCor+.175);
			GLUtilities.drawArc(gl, topXCor-.025, topYCor+.175, .025, 0, 180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor, bottomYCor);
			gl.glVertex2d(bottomXCor, bottomYCor-.2);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
		{
			GLUtilities.drawArc(gl, bottomXCor+.075, bottomYCor-.025, .025, 0, 180);
			gl.glVertex2d(bottomXCor+.05, bottomYCor-.175);
			GLUtilities.drawArc(gl, bottomXCor+.075, bottomYCor-.175, .025, 180, 180);
		}
		gl.glEnd();
	}
	
	private void drawJack(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor-.1, topYCor+.2);
			gl.glVertex2d(topXCor, topYCor+.2);
			gl.glVertex2d(topXCor-.05, topYCor+.2);
			gl.glVertex2d(topXCor-.05, topYCor+.025);
			GLUtilities.drawArc(gl, topXCor-.075, topYCor+.025, .025, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor, bottomYCor);
			gl.glVertex2d(bottomXCor+.1, bottomYCor);
			gl.glVertex2d(bottomXCor+.05, bottomYCor);
			gl.glVertex2d(bottomXCor+.05, bottomYCor-.175);
			GLUtilities.drawArc(gl, bottomXCor+.025, bottomYCor-.175, .025, 0, -180);
		}
		gl.glEnd();
	}
	
	private void drawQueen(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
		{
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.075, .05, 180, 180);
			gl.glVertex2d(topXCor, topYCor+.15);
			GLUtilities.drawArc(gl, topXCor-.05, topYCor+.15, .05, 0, 180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor-.05, topYCor+.075);
			gl.glVertex2d(topXCor, topYCor);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
		{
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.05, .05, 0, 180);
			gl.glVertex2d(bottomXCor, bottomYCor-.125);
			GLUtilities.drawArc(gl, bottomXCor+.05, bottomYCor-.125, .05, 180, 180);
			
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor+.1, bottomYCor-.2);
			gl.glVertex2d(bottomXCor+.05, bottomYCor-.125);
		}
		gl.glEnd();
	}
	
	private void drawKing(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor-.1, topYCor+.2);
			gl.glVertex2d(topXCor-.1, topYCor);
			gl.glVertex2d(topXCor-.1, topYCor+.1);
			gl.glVertex2d(topXCor, topYCor+.2);
			gl.glVertex2d(topXCor-.1, topYCor+.1);
			gl.glVertex2d(topXCor, topYCor);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor, bottomYCor);
			gl.glVertex2d(bottomXCor, bottomYCor-.2);
			gl.glVertex2d(bottomXCor, bottomYCor-.1);
			gl.glVertex2d(bottomXCor+.1, bottomYCor);
			gl.glVertex2d(bottomXCor, bottomYCor-.1);
			gl.glVertex2d(bottomXCor+.1, bottomYCor-.2);
			
		}
		gl.glEnd();
	}

	private void drawAce(GL2 gl, Denomination deno, double x, double y) {
		double topXCor = x-.3;
		double topYCor = y+.5;
		double bottomXCor = x+.15;
		double bottomYCor = y-.5;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(topXCor, topYCor);
			gl.glVertex2d(topXCor-.05, topYCor+.2);
			gl.glVertex2d(topXCor-.1, topYCor);
			gl.glVertex2d(topXCor-.08, topYCor+.08);
			gl.glVertex2d(topXCor-.02, topYCor+.08);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(bottomXCor, bottomYCor-.2);
			gl.glVertex2d(bottomXCor+.05, bottomYCor);
			gl.glVertex2d(bottomXCor+.1, bottomYCor-.2);
			gl.glVertex2d(bottomXCor+.08, bottomYCor-.12);
			gl.glVertex2d(bottomXCor+.02, bottomYCor-.12);
		}
		gl.glEnd();
	}
	
	private void drawDiamond(GL2 gl, Suit suit, double x, double y) {
		double xTop = x-.2;
		double yTop = y+.7;
		double xBottom = x+.35;
		double yBottom = y-.7;
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xTop, yTop);
			gl.glVertex2d(xTop+.05, yTop-.1);
			gl.glVertex2d(xTop, yTop-.2);
			gl.glVertex2d(xTop-.05, yTop-.1);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xBottom, yBottom);
			gl.glVertex2d(xBottom-.05, yBottom+.1);
			gl.glVertex2d(xBottom, yBottom+.2);
			gl.glVertex2d(xBottom+.05, yBottom+.1);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x+.1, y);
			gl.glVertex2d(x, y+.2);
			gl.glVertex2d(x-.1, y);
			gl.glVertex2d(x, y-.2);
		}
		gl.glEnd();
	}
	
	private void drawHeart(GL2 gl, Suit suit, double x, double y) {
		double xTop = x-.15;
		double yTop = y+.5;
		double xBottom = x+.35;
		double yBottom = y-.5;
		//Top left heart
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xTop-.05, yTop);
			gl.glVertex2d(xTop-.1, yTop+.175);
			gl.glVertex2d(xTop, yTop+.175);
			
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xTop-.075, yTop+.175, .025, 180, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xTop-.025, yTop+.175, .025, 180, -180);
		}
		gl.glEnd();
		//Bottom right heart.
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xBottom+.05, yBottom-.2);
			gl.glVertex2d(xBottom, yBottom-.025);
			gl.glVertex2d(xBottom+.1, yBottom-.025);		
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xBottom+.025, yBottom-.025, .025, 180, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xBottom+.075, yBottom-.025, .025, 180, -180);
		}
		gl.glEnd();
		// Big center heart
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x, y-.2);
			gl.glVertex2d(x-.1, y+.15);
			gl.glVertex2d(x+.1, y+.15);	
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x-.05, y+.15, .05, 0, 180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x+.05, y+.15, .05, 0, 180);
		}
		gl.glEnd();
	}
	
	private void drawClub(GL2 gl, Suit suit, double x, double y) {
		double xTop = x-.15;
		double yTop = y+.5;
		double xBottom = x+.35;
		double yBottom = y-.5;
		//Bottom right club
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xBottom+.025, yBottom-.2);
			gl.glVertex2d(xBottom+.075, yBottom-.2);
			gl.glVertex2d(xBottom+.05, yBottom-.125);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(xBottom+.05, yBottom-.175);
			gl.glVertex2d(xBottom+.05, yBottom-.1);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xBottom+.05, yBottom-.0625, .0375, -90, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xBottom+.02, yBottom-.1, .0375, 0, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xBottom+.08, yBottom-.1, .0375, -180, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
		}
		gl.glEnd();
		//Top left club.
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xTop-.025, yTop);
			gl.glVertex2d(xTop-.075, yTop);
			gl.glVertex2d(xTop-.05, yTop+.075);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(xTop-.05, yTop+.05);
			gl.glVertex2d(xTop-.05, yTop+.1);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xTop-.05, yTop + .1375, .0375, -90, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xTop-.08, yTop+.1, .0375, 0, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xTop-.02, yTop+.1, .0375, 0, 360);
		}
		gl.glEnd();
		//Big center club.
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x-.05, y-.15);
			gl.glVertex2d(x+.05, y-.15);
			gl.glVertex2d(x, y);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		{
			gl.glVertex2d(x, y-.05);
			gl.glVertex2d(x, y+.05);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x-.075, y, .075, 0, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x+.075, y, .075, 180, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x, y+.125, .075, -90, 360);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x, y, .075, 0, 360);
		}
		gl.glEnd();
	}
	
	private void drawSpade(GL2 gl, Suit suit, double x, double y) {
		double xTop = x-.15;
		double yTop = y+.5;
		double xBottom = x+.35;
		double yBottom = y-.5;
		//Bottom right spade
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xBottom+.025, yBottom-.2);
			gl.glVertex2d(xBottom+.075, yBottom-.2);
			gl.glVertex2d(xBottom+.05, yBottom-.125);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xBottom+.025, yBottom-.125, .025, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xBottom+.075, yBottom-.125, .025, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xBottom, yBottom-.125);
			gl.glVertex2d(xBottom+.1, yBottom-.125);
			gl.glVertex2d(xBottom+.05, yBottom);
		}
		gl.glEnd();
		
		//Top left spade.
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xTop-.025, yTop);
			gl.glVertex2d(xTop-.075, yTop);
			gl.glVertex2d(xTop-.05, yTop+.075);
		}
		gl.glEnd();
		
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xTop-.075, yTop+.075, .025, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, xTop-.025, yTop+.075, .025, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(xTop, yTop+.075);
			gl.glVertex2d(xTop-.1, yTop+.075);
			gl.glVertex2d(xTop-.05, yTop+.2);
		}
		gl.glEnd();
		
		//Big center spade.
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x-.05, y-.2);
			gl.glVertex2d(x+.05, y-.2);
			gl.glVertex2d(x, y-.05);
		}
		gl.glEnd();
		
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x-.05, y-.05, .05, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, x+.05, y-.05, .05, 0, -180);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(x-.1, y-.05);
			gl.glVertex2d(x+.1, y-.05);
			gl.glVertex2d(x, y+.2);
		}
		gl.glEnd();
	}
}
