package solitiare;


import model.Denomination;
import model.PileLocation;

public class KeyStrokeMapper {
	public char firstKeyStroke;
	public char secondKeyStroke;
	public char thirdKeyStroke;
	public char forthKeyStroke;
	
	/**
	 * Maps the first keystroke for Solitaire actions.
	 * @param c the KeyEvent character.
	 * @param controller the controller.
	 */
	public void mapFirstKeyStroke(char c, IGameController controller) {
		firstKeyStroke = c;
		if(c == 'd' || c == 'D') {
			controller.moveCardFromDrawToViewPile();
		}
		
		else if(c == 'A') {
			controller.moveCardFromViewPileToAcePile();
		}
		
		else if(c == '*') {
			controller.printTextualRepresentationOfGame();
		}
		else if(c=='a') {
			
		}
		else{
			try {
				int number = Integer.parseInt(String.valueOf(c));
				if(number>0&&number<8) {
					PileLocation location = PileLocation.getTemporaryPileLocations()[number-1];
					controller.moveCardFromViewPile(location);
				}
			}
			catch(NumberFormatException e) {
				
			}
		}
	}
	
	/**
	 * Maps the second keystroke for multi-keystroke Solitaire actions.
	 * @param c the KeyEvent char.
	 * @param controller the controller.
	 */
	public void mapSecondKeyStroke(char c, IGameController controller) {
		secondKeyStroke = c;
		if(firstKeyStroke == 'a') {
			try{
				int number = Integer.parseInt(String.valueOf(c));
				if(number>0&&number<8) {
					PileLocation location = PileLocation.getTemporaryPileLocations()[number-1];
					controller.moveCardFromTemporaryPileToAcePile(location);
				}
			}
			catch(NumberFormatException i) {
				
			}
		}
	}
	
	public void mapThirdKeyStroke(char c, IGameController controller) {
		thirdKeyStroke = c;
	}
	
	public void mapFourthKeyStroke(char c, IGameController controller) {
		forthKeyStroke = c;
		if(firstKeyStroke == 'm') {
			int sourcePileNumber;
			Denomination startingDeno;
			int destinationPileNumber;
			try {
				sourcePileNumber = Integer.parseInt(String.valueOf(secondKeyStroke));
				String denoValue = String.valueOf(thirdKeyStroke).toUpperCase();
				Denomination deno = Denomination.getDenomination(denoValue);
				if(thirdKeyStroke =='1') {
					deno = Denomination.getDenomination("10");
				}
				destinationPileNumber = Integer.parseInt(String.valueOf(forthKeyStroke));
				PileLocation sourcePileLocation = PileLocation.getTemporaryPileLocations()[sourcePileNumber-1];
				PileLocation destinationPileLocation = PileLocation.getTemporaryPileLocations()[destinationPileNumber-1];
				controller.moveCardToAndFromTemporaryPile(sourcePileLocation, destinationPileLocation, deno);
			}
			catch(NumberFormatException e) {
			}
		}
	}
}
