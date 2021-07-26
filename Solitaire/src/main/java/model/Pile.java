package model;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector2d;

public abstract class Pile {
	protected ArrayList<Card> cards;
	protected final static double PILE_WIDTH = 1.0;
	protected final static double PILE_HEIGHT= 1.5;
	protected PileLocation location;
	
	/**
	 * attempt to add a card to a pile depending on the pile's card ordering.
	 * @param card the card to be added.
	 * @return true if the card was added to the pile successfully and false otherwise.
	 */
	public abstract boolean addCard(Card card);
	
	/**
	 * Constructor for Pile objects.
	 * @param location location of the new pile.
	 */
	public Pile(PileLocation location) {
		cards = new ArrayList<Card>();
		this.location = location;
	}
	
	/**
	 * gets the cards in the pile.
	 * @return cards the ArrayList of cards.
	 */
	public ArrayList<Card> getCards(){
		return cards;
	}
	
	/**
	 * gets a card at a certain index of the pile.
	 * @param index index of the card.
	 * @return card the card.
	 */
	public Card getCard(int index) {
		Card card = cards.get(index);
		return card;
	}
	
	/**
	 * the pile's location.
	 * @return location the location.
	 */
	public PileLocation getLocation() {
		return location;
	}
	
	/**
	 * gets the top card on the pile.
	 * @return topCard the top card.
	 */
	public Card getTopCard() {
		if(cards.size()==0) {
			return null;
		}
		Card topCard = cards.get(cards.size()-1);
		return topCard;
	}

	/**
	 * gets the size of the pile.
	 * @return size the size.
	 */
	public int size() {
		int size = cards.size();
		return size;
	}
	
	/**
	 * Prints to System.out a textual representation of the pile and its cards.
	 */
	public void printPile() {
		HashMap<Suit, String> suitMap = new HashMap<Suit, String>();
		suitMap.put(Suit.Club, "C"); suitMap.put(Suit.Diamond, "D"); suitMap.put(Suit.Heart, "H"); suitMap.put(Suit.Spade, "S");
		for(int i = size()-1; i>=0; i--) {
			Card card = cards.get(i);
			System.out.print(card.getDenomination().getValue() + suitMap.get(card.getSuit()) + " ");
		}
	}
	
	/**
	 * removes the top card from the pile.
	 */
	public void removeTopCard() {
		if(size()>0) {
			cards.remove(cards.size()-1);
		}
	}

	public void removeCard(int index) {
		cards.remove(index);
	}
}
