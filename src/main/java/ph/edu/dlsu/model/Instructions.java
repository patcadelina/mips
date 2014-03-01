package ph.edu.dlsu.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Instructions {

	J('J'),
	DSLLV('R'), OR('R'), SLT('R'), DADDU('R'), DSUBU('R'),
	BNEZ('I'), ANDI('I'), DADDIU('I'), LD('I'), SD('I');

	private final char type;

	private static final Map<String, Instructions> lookup = new HashMap<String, Instructions>();

	private Instructions(char type) {
		this.type = type;
	}

	static {
		for (Instructions i : EnumSet.allOf(Instructions.class)) {
			lookup.put(i.name(), i);
		}
	}

	public static Instructions get(String name) {
		return lookup.get(name);
	}

	public char getType() {
		return type;
	}

}
