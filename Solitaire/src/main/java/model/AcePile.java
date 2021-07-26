package model;

import java.util.LinkedList;

public class AcePile extends Pile{
	private Suit suit;
	private LinkedList<Denomination> cardOrdering;
	
	/**
	 * Constructor for AcePile objects. Sets the required card ordering for Solitaire ace piles.
	 * @param location the Location of the AcePile.
	 * @param suit the AcePile's suit;
	 */
	public AcePile(PileLocation location, Suit suit) {
		super(location);
		this.suit = suit;
		cardOrdering = new LinkedList<Denomination>();
		Denomination[] denominations = Denomination.values();
		for(int i = denominations.length-1; i>=0 ; i--) {
			cardOrdering.add(denominations[i]);
		}
	}
	
	/**
	 * Adds a card to the pile.
	 * @param card the Card to be added.
	 */
	public boolean addCard(Card card) {
		if(card.getSuit()==suit) {
			if((cards.size()==0)&&(card.getDenomination()==Denomination.Ace)) {
				card.setLocation(location);
				cards.add(card);
				return true;
			}
			if((cards.size()==0)&&(card.getDenomination()!=Denomination.Ace)) {
				return false;
			}
			else if((card.getDenomination()==cardOrdering.get(cards.size()))) {
				card.setLocation(location);
				cards.add(card);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * gets the card ordering for the ace pile.
	 * @return cardOrdering required card ordering of the ace pile.
	 */
	public LinkedList<Denomination> getCardOrdering(){
		return cardOrdering;
	}

	/**
	 * gets the ace pile's suit.
	 * @return suit the ace pile's suit.
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * Gets the next required denomination based on the card ordering for Solitaire ace piles.
	 * @return nextDeno the next required denomination.
	 */
	public Denomination nextDenominationNeeded() {
		Card card = getTopCard();
		int i = 0;
		while(cardOrdering.get(i)!=card.getDenomination()) {
			i++;
		}
		Denomination nextDeno = cardOrdering.get(i+1);
		return nextDeno;
	}
}
