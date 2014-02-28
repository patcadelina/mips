package ph.edu.dlsu.util;

import ph.edu.dlsu.model.Instruction;

public class InstructionUtil {

	private InstructionUtil() {

	}

	public static String generateOpcode(Instruction instruction) {
		String removedCommas = instruction.getRequest().replaceAll(",", "");
		String[] params = removedCommas.split(" ");
		String[] instArgs = null;
		int arg = 0;
		for (String param : params) {
			if (arg == 0) {
				if (param == "LD" || !param.startsWith("L")) {
					instArgs = new String[params.length];
					instArgs[arg++] = param;
				} else {
					instArgs = new String[params.length - 1];
				}
			} else {
				if (param.startsWith("#")) {
					instArgs[arg++] = param.substring(0, 4);
					instArgs[arg++] = param.substring(6, 7);
				} else {
					instArgs[arg++] = param;
				}
			}
		}
		return opcode(instArgs);
	}

	private static String opcode(String[] instArgs) {
		StringBuilder builder = new StringBuilder();
		switch (instArgs[0]) {
		case "DADDU":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101101");
		case "DSUBU":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101111");
		case "OR":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000100111");
		case "DSLLV":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000010100");
		case "SLT":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101010");
		case "BNEZ":

		case "LD":
			builder.append("110111");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toBinaryString(instArgs[2]));
		case "SD":
			builder.append("111111");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toBinaryString(instArgs[2]));
		case "DADDIU":
			builder.append("011001");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toBinaryString(instArgs[3].substring(1)));
		case "ANDI":
			builder.append("001100");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toBinaryString(instArgs[3].substring(1)));
		case "J":

		default:

		}
		return builder.toString();
	}

	private static String toNBitBinary(int num, int n) {
		StringBuilder builder = new StringBuilder();
		String partial = Long.toBinaryString(num);
		for (int i = 0; i < n - partial.length(); i++) {
			builder.append("0");
		}
		return builder.append(partial).toString();
	}
}
