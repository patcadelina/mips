package ph.edu.dlsu.util;

import java.util.ArrayList;
import java.util.List;

import ph.edu.dlsu.model.Instruction;
import ph.edu.dlsu.model.Instructions;

public class InstructionUtil {

	private static final String COLON = ":";
	private static final String COMMA = ",";
	private static final String SPACE = " ";
	private static final String EMPTY_STRING = "";

	private InstructionUtil() {

	}

	public static List<Instruction> preprocessReferences(List<Instruction> instructions) {
		List<Instruction> processed = new ArrayList<Instruction>(instructions);
		for (Instruction instruction : processed) {
			String removedCommas = instruction.getRequest().replaceAll(COMMA, EMPTY_STRING);
			String[] params = removedCommas.split(" ");
			boolean hasReference = false;
			int paramCount = 0;
			for (String param : params) {
				if (isAddressReference(param)) {
					hasReference = true;
					int lineRef = findLineReference(param, instructions);
					if (params[0].equals(Instructions.BNEZ.name()) || params[1].equals(Instructions.BNEZ.name())) {
						params[paramCount] = Integer.toString(lineRef - instruction.getLine());
					} else {
						params[paramCount] = Integer.toString(lineRef);
					}
				}
				paramCount++;
			}
			if (hasReference) {
				StringBuilder builder = new StringBuilder();
				int argCount = 1;
				for (String param : params) {
					if (isAddressReference(param)) {
						builder.append(param);
						builder.append(SPACE);
						argCount++;
						continue;
					}
					if (isSupportedInstruction(param)) {
						builder.append(param);
						builder.append(SPACE);
						argCount++;
						continue;
					}
					builder.append(param);
					if (argCount < params.length) {
						builder.append(COMMA);
						builder.append(SPACE);
					}
					argCount++;
				}
				instruction.setRequest(builder.toString());
			}
		}
		return processed;
	}

	private static boolean isAddressReference(String param) {
		return param.startsWith("L") && !param.endsWith(":") && !param.equals(Instructions.LD.name());
	}

	private static int findLineReference(String reference, List<Instruction> instructions) {
		for (Instruction instruction : instructions) {
			if (reference.equals(getArg0(instruction))) {
				return instruction.getLine();
			}
		}
		return 0;
	}

	private static String getArg0(Instruction instruction) {
		return instruction.getRequest().replaceAll(COMMA, EMPTY_STRING).split(" ")[0].replaceAll(COLON, EMPTY_STRING);
	}

	private static boolean isSupportedInstruction(String param) {
		return null != Instructions.get(param);
	}

	public static String generateOpcode(Instruction instruction) {
		String removedCommas = instruction.getRequest().replaceAll(COMMA, EMPTY_STRING);
		String[] params = removedCommas.split(SPACE);
		String[] instArgs = null;
		int arg = 0;
		for (String param : params) {
			if (arg == 0) {
				if (param.endsWith(COLON)) {
					instArgs = new String[params.length - 1];
				} else {
					instArgs = new String[params.length];
					instArgs[arg++] = param;
				}
			} else {
				if (param.startsWith("#")) {
					instArgs[arg++] = param.substring(1, 5);
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
			break;
		case "DSUBU":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101111");
			break;
		case "OR":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000100111");
			break;
		case "DSLLV":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000010100");
			break;
		case "SLT":
			builder.append("000000");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101010");
			break;
		case "BNEZ":
			builder.append("000101");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000");
			builder.append(toNBitBinary(computeOffset(Integer.parseInt(instArgs[2])), 16));
			break;
		case "LD":
			builder.append("110111");
			String[] args = instArgs[2].replace("(", SPACE).replace(")", EMPTY_STRING).split(SPACE);
			builder.append(toNBitBinary(Integer.parseInt(args[1].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(args[0]), 2), 16));
			break;
		case "SD":
			builder.append("111111");
			args = instArgs[2].replace("(", SPACE).replace(")", EMPTY_STRING).split(SPACE);
			builder.append(toNBitBinary(Integer.parseInt(args[1].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(args[0]), 2), 16));
			break;
		case "DADDIU":
			builder.append("011001");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(instArgs[3]), 2), 16));
			break;
		case "ANDI":
			builder.append("001100");
			builder.append(toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(instArgs[3]), 2), 16));
			break;
		case "J":
			builder.append("000010");
			builder.append(toNBitBinary(computeOffset(Integer.parseInt(instArgs[1])), 26));
			break;
		default:

		}
		return builder.toString();
	}

	public static String toNBitBinary(int num, int n) {
		StringBuilder builder = new StringBuilder();
		String partial = Long.toBinaryString(num);
		for (int i = 0; i < n - partial.length(); i++) {
			builder.append("0");
		}
		return builder.append(partial).toString();
	}

	private static int computeOffset(int line) {
		return line - 1;
	}

}