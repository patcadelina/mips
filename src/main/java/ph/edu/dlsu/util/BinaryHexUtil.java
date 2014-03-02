package ph.edu.dlsu.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public static String toNBitHex(int num, int n) {
		StringBuilder builder = new StringBuilder();
		String partial = Long.toHexString(num).toUpperCase();
		for (int i = 0; i < n - partial.length(); i++) {
			builder.append("0");
		}
		return builder.append(partial).toString();
	}

	public static String toNBitHex(long num, int n) {
		StringBuilder builder = new StringBuilder();
		String partial = Long.toHexString(num).toUpperCase();
		for (int i = 0; i < n - partial.length(); i++) {
			builder.append("0");
		}
		return builder.append(partial).toString();
	}

	public static String toNBitBinary(int num, int n) {
		StringBuilder builder = new StringBuilder();
		String partial = Long.toBinaryString(num);
		for (int i = 0; i < n - partial.length(); i++) {
			builder.append("0");
		}
		return builder.append(partial).toString();
	}

	public static String toNBitBinary(long num, int n) {
		StringBuilder builder = new StringBuilder();
		String partial = Long.toBinaryString(num);
		for (int i = 0; i < n - partial.length(); i++) {
			builder.append("0");
		}
		return builder.append(partial).toString();
	}

	public static long parseSignedBinary(String binary) throws IOException {
		if (binary.startsWith("1")) {
			InputStream stream = new ByteArrayInputStream(binary.getBytes());
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			int c;
			try {
				while ((c = reader.read()) != -1) {
					builder.append(c == 49 ? 0 : 1);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				reader.close();
			}
			long value = Long.parseLong(builder.toString(), 2) + 1;
			return value *= -1;
		} else {
			return Long.parseLong(binary, 2);
		}
	}

}
