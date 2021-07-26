package model;

public enum PileLocation {
	Draw (1.5, 9.25),
	View(2.75,9.25),
	Ace1(5.25,9.25),
	Ace2(6.5,9.25),
	Ace3(7.75,9.25),
	Ace4(9,9.25),
	One(1.5,7),
	Two(2.75,7),
	Three(4,7),
	Four(5.25,7),
	Five(6.5,7),
	Six(7.75,7),
	Seven(9,7);
	
	private double x;
	private double y;
	private PileLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * gets the x coordinate of the pile location.
	 * @return x the x coordinate.
	 */
	public double getX() {
		return x;
	}
	/**
	 * gets the y coordinate of the pile location.
	 * @return y the y coordinate.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * gets all the temporary pile locations for solitaire.
	 * @return tempPileLocations the temporary pile locations.
	 */
	public static PileLocation[] getTemporaryPileLocations() {
		PileLocation[] tempPileLocations = new PileLocation[7];
		tempPileLocations[0] = PileLocation.One;
		tempPileLocations[1] = PileLocation.Two;
		tempPileLocations[2] = PileLocation.Three;
		tempPileLocations[3] = PileLocation.Four;
		tempPileLocations[4] = PileLocation.Five;
		tempPileLocations[5] = PileLocation.Six;
		tempPileLocations[6] = PileLocation.Seven;
		return tempPileLocations;
	}
	
	/**
	 * gets all the ace pile locations for solitaire.
	 * @return acePileLocations the ace pile locations.
	 */
	public static PileLocation[] getAcePileLocations() {
		PileLocation[] acePileLocations = new PileLocation[4];
		acePileLocations[0] = PileLocation.Ace1;
		acePileLocations[1] = PileLocation.Ace2;
		acePileLocations[2] = PileLocation.Ace3;
		acePileLocations[3] = PileLocation.Ace4;
		return acePileLocations;
	}
}
