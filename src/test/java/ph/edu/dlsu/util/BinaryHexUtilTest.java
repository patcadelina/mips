package ph.edu.dlsu.util;

import org.junit.Assert;
import org.junit.Test;

public class BinaryHexUtilTest {

	private static final String HEX_MAX_64BIT = "FFFFFFFFFFFFFFFF";
	private static final String BINARY_MAX_64BIT = "1111111111111111111111111111111111111111111111111111111111111111";
	private static final String HEX_DATA = "87FCA502";
	private static final String BINARY_DATA = "10000111111111001010010100000010";

	@Test
	public void shouldConvertToHex() {
		String expected = BINARY_DATA;
		String actual = BinaryHexUtil.toBinaryString(HEX_DATA);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldConvertHexMaxValue() {
		String expected = BINARY_MAX_64BIT;
		String actual = BinaryHexUtil.toBinaryString(HEX_MAX_64BIT);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldConvertToBinary() {
		String expected = HEX_DATA;
		String actual = BinaryHexUtil.toHexString(BINARY_DATA);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldConvertBinaryMaxValue() {
		String expected = BINARY_MAX_64BIT;
		String actual = BinaryHexUtil.toBinaryString(HEX_MAX_64BIT);
		Assert.assertEquals(expected, actual);
	}

}
