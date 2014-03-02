package ph.edu.dlsu.util;

import java.util.ArrayList;
import java.util.List;

import ph.edu.dlsu.model.Func;
import ph.edu.dlsu.model.Instruction;
import ph.edu.dlsu.model.Instructions;
import ph.edu.dlsu.model.Op;
import ph.edu.dlsu.model.Operation;

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
			String removedCommas = instruction.getCommand().replaceAll(COMMA, EMPTY_STRING);
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
				instruction.setCommand(builder.toString());
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
		return instruction.getCommand().replaceAll(COMMA, EMPTY_STRING).split(" ")[0].replaceAll(COLON, EMPTY_STRING);
	}

	private static boolean isSupportedInstruction(String param) {
		return null != Instructions.get(param);
	}

	public static String generateOpcode(Instruction instruction) {
		String removedCommas = instruction.getCommand().replaceAll(COMMA, EMPTY_STRING);
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
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101101");
			break;
		case "DSUBU":
			builder.append("000000");
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101111");
			break;
		case "OR":
			builder.append("000000");
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000100111");
			break;
		case "DSLLV":
			builder.append("000000");
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000010100");
			break;
		case "SLT":
			builder.append("000000");
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[3].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000101010");
			break;
		case "BNEZ":
			builder.append("000101");
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append("00000");
			builder.append(BinaryHexUtil.toNBitBinary(computeOffset(Integer.parseInt(instArgs[2])), 16));
			break;
		case "LD":
			builder.append("110111");
			String[] args = instArgs[2].replace("(", SPACE).replace(")", EMPTY_STRING).split(SPACE);
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(args[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(args[0]), 2), 16));
			break;
		case "SD":
			builder.append("111111");
			args = instArgs[2].replace("(", SPACE).replace(")", EMPTY_STRING).split(SPACE);
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(args[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(args[0]), 2), 16));
			break;
		case "DADDIU":
			builder.append("011001");
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(instArgs[3]), 2), 16));
			break;
		case "ANDI":
			builder.append("001100");
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[2].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(instArgs[1].substring(1)), 5));
			builder.append(BinaryHexUtil.toNBitBinary(Integer.parseInt(BinaryHexUtil.toBinaryString(instArgs[3]), 2), 16));
			break;
		case "J":
			builder.append("000010");
			builder.append(BinaryHexUtil.toNBitBinary(computeOffset(Integer.parseInt(instArgs[1])), 26));
			break;
		default:

		}
		return builder.toString();
	}

	private static int computeOffset(int line) {
		return line - 1;
	}

	public static Operation getOperation(String opcode) {
		switch (opcode) {
		case "000000":
			return Operation.ALU;
		case "011001":
		case "001100":
			return Operation.IMM;
		case "110111":
		case "111111":
			return Operation.LOAD_STORE;
		case "000101":
		case "000010":
			return Operation.BRANCH;
		default:
			return Operation.INVALID;
		}
	}

	public static String getFuncCode(String opcode) {
		return opcode.substring(26, 32);
	}

	public static String getOperationCode(String opcode) {
		return opcode.substring(0, 6);
	}

	public static String getRegisterA(String opcode) {
		return opcode.substring(6, 11);
	}

	public static String getRegisterB(String opcode) {
		return opcode.substring(11, 16);
	}

	public static String getImm(String opcode) {
		return opcode.substring(16, 32);
	}

	public static Func getFunc(String opcode) {
		switch (opcode) {
		case "101101": return Func.DADDU;
		case "101111": return Func.DSUBU;
		case "100111": return Func.OR;
		case "010100": return Func.DSLLV;
		case "101010": return Func.SLT;
		default: return Func.INVALID;
		}
	}

	public static Op getOp(String opcode) {
		switch(opcode) {
		case "011001": return Op.DADDIU;
		case "001100": return Op.ANDI;
		default: return Op.INVALID;
		}
	}

	public static int getShiftValue(String value) {
		return Integer.parseInt(value.substring(58, 64), 2);
	}

}