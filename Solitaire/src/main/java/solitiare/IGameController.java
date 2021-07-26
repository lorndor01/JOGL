package solitiare;

import model.Denomination;
import model.PileLocation;
import model.Suit;

public interface IGameController {
	public void moveCardFromViewPile(PileLocation nextLocation);
	public void moveCardFromViewPileToAcePile();
	public void moveCardToAndFromTemporaryPile(PileLocation location, PileLocation nextLocation, Denomination startingDeno);
	public void moveCardFromTemporaryPileToAcePile(PileLocation location);
	public void moveCardFromDrawToViewPile();
	public void dealCards();
	public void printTextualRepresentationOfGame();
}