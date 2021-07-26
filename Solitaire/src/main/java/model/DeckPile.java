package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * 
 * @author Liam Orndorff
 *
 */
public class DeckPile extends Pile{

	/**
	 * Constructor for DeckPile which makes one call to the superclass constructor.
	 * @param location Location on the gameboard where the pile of cards will be.
	 */
	public DeckPile(PileLocation location) {
		super(location);
		
	}

	/**
	 * Adds a card to the deck.
	 * @param card Card to be added.
	 */
	@Override
	public boolean addCard(Card card) {
		card.setLocation(location);
		cards.add(card);
		return true;
	}
	
	/**
	 * Sets the cards field to a random deck of 52 cards.
	 */
	public void setPileToRandomDeck() {
		cards = generateRandomDeck();
	}

	/**
	 * Sets the cards field to non-random deck of 52 cards.
	 */
	public void setPileToNonRandomDeck() {
		cards = generateNonRandomDeck();
	}
	
	/**
	 * Generates a new random array list of 52 cards.
	 * @return randomDeck
	 */
	public ArrayList<Card> generateRandomDeck() {
		ArrayList<Card> nonRandomDeck = generateNonRandomDeck();
		ArrayList<Card> randomDeck = new ArrayList<Card>();
		Random random = new Random();
		Iterator<Card> it = nonRandomDeck.iterator();
		while(randomDeck.size()<52) {
			int number = random.nextInt(nonRandomDeck.size());
			randomDeck.add(nonRandomDeck.get(number));
			nonRandomDeck.remove(number);
		}
		return randomDeck;
	}
	
	/**
	 * Generates a new non random array list of 52 cards. Card ordering is based on ordering
	 * needed for SoliaireWindow's test mode.
	 * @return nonRandomDeck
	 */
	public ArrayList<Card> generateNonRandomDeck() {
		Suit[] suits = Suit.values();
		Denomination[] denos = Denomination.values();
		ArrayList<Card> nonRandomDeck = new ArrayList<Card>();
		for(Suit suit : suits) {
			int i = denos.length-1;
			while(i>=0) {
				nonRandomDeck.add(new Card(denos[i],suit, PileLocation.Draw));
				i--;
			}
		}
		return nonRandomDeck;
	}
	
	/**
	 * Takes the top card, turns it over, and moves it to the new location.
	 * @param newPile The top cards next pile.
	 */
	public void moveTopCard(Pile newPile) {
		Card card = cards.get(cards.size()-1);
		card.setLocation(newPile.getLocation());
		if(newPile instanceof DeckPile) {
			card.turnOver();
		}
		newPile.addCard(card);
		cards.remove(cards.size()-1);
	}
}
