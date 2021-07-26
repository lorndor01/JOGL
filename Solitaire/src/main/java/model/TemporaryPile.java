package model;

import java.util.LinkedList;
import model.Denomination;
public class TemporaryPile extends Pile{
	//A temporary pile's card ordering starting with King and ending with Ace denomination.
	private LinkedList<Denomination> cardOrdering;
	
	/**
	 * Constructor for TemporaryPile.
	 * @param location Location for temporary pile.
	 */
	public TemporaryPile(PileLocation location) {
		super(location);
		cardOrdering = new LinkedList<Denomination>();
		for(Denomination deno : Denomination.values()) {
			cardOrdering.add(deno);
		}
	}
	
	/**
	 *Attempts to add a card to the temporary pile.
	 *@param card The card that you want to try to add.
	 *@return returns true if the card is added successfully and false otherwise.
	 */
	public boolean addCard(Card card) {
		if(cards.size()==0 || (cards.get(cards.size()-1).isFaceUp()==false)) {
			cards.add(card);
			card.setLocation(location);
			return true;
		}
		else {
			Card topCard = cards.get(cards.size()-1);
			int i = 0;
			if(topCard.getDenomination()==Denomination.Ace) {
				return false;
			}
			while(topCard.getDenomination() != cardOrdering.get(i)) {
				i++;
			}
			if(card.getDenomination()==cardOrdering.get(i+1)) {
				if(topCard.getSuit().getValue() != card.getSuit().getValue()) {
					cards.add(card);
					card.setLocation(location);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
     * Method to access the temporary pile's required card ordering.
	 * @return card ordering starting with King ending with Ace.
	 */
	public LinkedList<Denomination> cardOrdering(){
		return cardOrdering;
	}
	
}
