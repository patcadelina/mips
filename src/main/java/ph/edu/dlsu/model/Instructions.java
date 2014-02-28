package ph.edu.dlsu.model;

public enum Instructions {

	J('J'),
	DSLLV('R'), OR('R'), SLT('R'), DADDU('R'), DSUBU('R'),
	BNEZ('I'), ANDI('I'), DADDIU('I'), LD('I'), SD('I');

	private final char type;

	private Instructions(char type) {
		this.type = type;
	}

	public char getType() {
		return type;
	}

}
