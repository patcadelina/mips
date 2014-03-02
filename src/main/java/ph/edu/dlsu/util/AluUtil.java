package ph.edu.dlsu.util;

import java.io.IOException;

import ph.edu.dlsu.model.Register;

public class AluUtil {

	private AluUtil() {

	}

	public static String executeDADDU(Register id_ex_a, Register id_ex_b) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		long b = Long.parseLong(id_ex_b.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a + b, 64);
	}

	public static String executeDSUBU(Register id_ex_a, Register id_ex_b) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		long b = Long.parseLong(id_ex_b.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a - b, 64);
	}

	public static String executeOR(Register id_ex_a, Register id_ex_b) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		long b = Long.parseLong(id_ex_b.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a | b, 64);
	}

	public static String executeSLT(Register id_ex_a, Register id_ex_b) throws IOException {
		long a = BinaryHexUtil.parseSignedBinary(id_ex_a.getValue());
		long b = BinaryHexUtil.parseSignedBinary(id_ex_b.getValue());
		return BinaryHexUtil.toNBitBinary(a < b ? 1 : 0, 64);
	}

	public static String executeDSLLV(Register id_ex_a, Register id_ex_b) {
		int shift = InstructionUtil.getShiftValue(id_ex_b.getValue());
		String shifted = id_ex_a.getValue().substring(shift);
		StringBuilder builder = new StringBuilder(shifted);
		for (; shift > 0; shift--) {
			builder.append("0");
		}
		return builder.toString();
	}

	public static String executeDADDIU(Register id_ex_a, Register id_ex_imm) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		long imm = Long.parseLong(id_ex_imm.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a + imm, 64);
	}

	public static String executeANDI(Register id_ex_a, Register id_ex_imm) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		long imm = Long.parseLong(id_ex_imm.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a & imm, 64);
	}

	public static boolean executeNEZ(Register id_ex_a) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		return a != 0;
	}

}
