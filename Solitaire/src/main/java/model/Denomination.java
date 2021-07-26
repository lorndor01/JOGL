package model;

public enum Denomination {
	King("K"),
	Queen("Q"),
	Jack("J"),
	Ten("10"),
	Nine("9"),
	Eight("8"),
	Seven("7"),
	Six("6"),
	Five("5"),
	Four("4"),
	Three("3"),
	Two("2"),
	Ace("A");
	
	
	private String value;
	private Denomination(String s) {
		value = s;
	}
	
	/**
	 * gets the value associated with a certain denomination.
	 * @return value the denomination value.
	 */
	public String getValue() {
		return value;
	}

	public static Denomination getDenomination(String value) {
		for(Denomination deno : Denomination.values()) {
			if(value.equals(deno.getValue())) {
				return deno;
			}
		}
		return null;
	}
}
