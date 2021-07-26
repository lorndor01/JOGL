package model;

import org.joml.Vector2d;

import model.Denomination;
import model.Suit;
import model.PileLocation;
public class Card {
	private Denomination deno;
	private Suit suit; 
	private boolean faceUp;
	private PileLocation location;
	private Vector2d drawnLocation;
	/**
	 * The cards width.
	 */
	public static final double CARD_WIDTH = 1;
	/**
	 * The cards height.
	 */
	public static final double CARD_HEIGHT = 1.5; 
	/**
	 * The radius for the cards rounded corners.
	 */
	public static final double CORNER_RADIUS = .1;
	
	/**
	 * Creates a new Card object that is faceDown.
	 * @param deno the card's denomination.
	 * @param suit the card's suit.
	 * @param location the card's pile location.
	 */
	public Card(Denomination deno, Suit suit, PileLocation location) {
		this.deno = deno;
		this.suit = suit;
		faceUp = false;
		this.location = location;
		setDrawnLocation(location.getX(), location.getY());
	}
	
	/**
	 * Simulates turning the card over.
	 */
	public void turnOver() {
		faceUp = !faceUp;
	}

	/**
	 * gets the card's denomination.
	 * @return deno the denomination.
	 */
	public Denomination getDenomination() {
		return deno;
	}

	/**
	 * gets the card's suit.
	 * @return suit the suit.
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * checks to see if the card is face up.
	 * @return faceUp true if the card is face up and false otherwise.
	 */
	public boolean isFaceUp() {
		return faceUp;
	}
	
	/**
	 * Moves the card to a new pile location.
	 * @param newLocation the cards next pile location.
	 */
	public void move(PileLocation newLocation) {
		location = newLocation;
	}

	/**
	 * gets the cards pile location.
	 * @return location the pile location.
	 */
	public PileLocation getLocation() {
		return location;
	}

	/**
	 * sets the cards pile location.
	 * @param newLocation the new pile location.
	 */
	public void setLocation(PileLocation newLocation) {
		location = newLocation;
	}
	
	/**
	 * sets where the card should be drawn.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 */
	public void setDrawnLocation(double x, double y) {
		drawnLocation = new Vector2d(x, y);
	}
	
	/**
	 * gets the card's drawn location as a Vector2d object.
	 * @return drawnLocation the card's drawn location.
	 */
	public Vector2d getDrawnLocation() {
		return drawnLocation;
	}

}
