package model;

public enum Suit {
	Spade(false),
	Heart(true),
	Diamond(true),
	Club(false);

	private boolean value;
	
	Suit(boolean b) {
		value = b;
	}
	
	/**
	 * gets the value associated with a specified suit.
	 * @return value true if Heart or Diamond and false otherwise.
	 */
	public boolean getValue() {
		return value;
	}
}
