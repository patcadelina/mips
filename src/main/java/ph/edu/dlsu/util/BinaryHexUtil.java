package ph.edu.dlsu.util;

import java.math.BigInteger;

public class BinaryHexUtil {

	private BinaryHexUtil() {

	}

	public static String toHexString(String binary) {
		return new BigInteger(binary, 2).toString(16).toUpperCase();
	}

	public static String toBinaryString(String hex) {
		return new BigInteger(hex, 16).toString(2);
	}

}
