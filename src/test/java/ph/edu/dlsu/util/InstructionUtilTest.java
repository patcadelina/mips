package ph.edu.dlsu.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ph.edu.dlsu.model.Func;
import ph.edu.dlsu.model.Instruction;
import ph.edu.dlsu.model.Op;
import ph.edu.dlsu.model.Operation;

public class InstructionUtilTest {

	@Test
	public void shouldDecodeDADDU() {
		Instruction instruction = new Instruction();
		instruction.setCommand("DADDU R1, R2, R3");
		String expected = "00000000010000110000100000101101";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeDSUBU() {
		Instruction instruction = new Instruction();
		instruction.setCommand("DSUBU R1, R2, R3");
		String expected = "00000000010000110000100000101111";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeOR() {
		Instruction instruction = new Instruction();
		instruction.setCommand("OR R1, R2, R3");
		String expected = "00000000010000110000100000100101";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeDSLLV() {
		Instruction instruction = new Instruction();
		instruction.setCommand("DSLLV R1, R2, R3");
		String expected = "00000000010000110000100000010100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeSLT() {
		Instruction instruction = new Instruction();
		instruction.setCommand("SLT R1, R2, R3");
		String expected = "00000000010000110000100000101010";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeBNEZ() {
		Instruction instruction = new Instruction();
		instruction.setCommand("BNEZ R1, 4");
		String expected = "00010100001000000000000000000011";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeLD() {
		Instruction instruction = new Instruction();
		instruction.setCommand("LD R1, 2004(R2)");
		String expected = "11011100010000010010000000000100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeSD() {
		Instruction instruction = new Instruction();
		instruction.setCommand("SD R1, 2004(R2)");
		String expected = "11111100010000010010000000000100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeDADDIU() {
		Instruction instruction = new Instruction();
		instruction.setCommand("DADDIU R1, R2, #1000");
		String expected = "01100100010000010001000000000000";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeANDI() {
		Instruction instruction = new Instruction();
		instruction.setCommand("ANDI R1, R2, #1000");
		String expected = "00110000010000010001000000000000";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeJ() {
		Instruction instruction = new Instruction();
		instruction.setCommand("J 5");
		String expected = "00001000000000000000000000000100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldPreprocessReferences() {
		Instruction ins1 = Instruction.newInstance(1, "DADDU R1, R2, R0");
		Instruction ins2 = Instruction.newInstance(2, "BNEZ R1, L1");
		Instruction ins3 = Instruction.newInstance(3, "DADDIU R1, R2, #2004");
		Instruction ins4 = Instruction.newInstance(4, "J L1");
		Instruction ins5 = Instruction.newInstance(5, "L1: LD R4, 2004(R3)");

		List<Instruction> instructions = new ArrayList<Instruction>();
		instructions.add(ins1);
		instructions.add(ins2);
		instructions.add(ins3);
		instructions.add(ins4);
		instructions.add(ins5);

		List<Instruction> expected = new ArrayList<Instruction>();
		Instruction exp1 = Instruction.newInstance(1, "DADDU R1, R2, R0");
		Instruction exp2 = Instruction.newInstance(2, "BNEZ R1, 3");
		Instruction exp3 = Instruction.newInstance(3, "DADDIU R1, R2, #2004");
		Instruction exp4 = Instruction.newInstance(4, "J 5");
		Instruction exp5 = Instruction.newInstance(5, "L1: LD R4, 2004(R3)");
		expected.add(exp1);
		expected.add(exp2);
		expected.add(exp3);
		expected.add(exp4);
		expected.add(exp5);

		List<Instruction> actual = InstructionUtil.preprocessReferences(instructions);

		Assert.assertEquals(expected.get(1).getCommand(), actual.get(1).getCommand());
		Assert.assertEquals(expected.get(3).getCommand(), actual.get(3).getCommand());
	}

	@Test
	public void shouldParseInstructionWithReference() {
		Instruction instruction = new Instruction();
		instruction.setCommand("L1: LD R4, 2004(R3)");
		String expected = "11011100011001000010000000000100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyArithmeticAsALU() {
		Operation expected = Operation.ALU;
		Operation actual = InstructionUtil.getOperation("000000");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyDADDIUAsALU() {
		Operation expected = Operation.IMM;
		Operation actual = InstructionUtil.getOperation("011001");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyANDIAsALU() {
		Operation expected = Operation.IMM;
		Operation actual = InstructionUtil.getOperation("001100");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyLoadAsLoadStore() {
		Operation expected = Operation.LOAD_STORE;
		Operation actual = InstructionUtil.getOperation("110111");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyStoreAsLoadStore() {
		Operation expected = Operation.LOAD_STORE;
		Operation actual = InstructionUtil.getOperation("111111");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyBNEZAsBranch() {
		Operation expected = Operation.BRANCH;
		Operation actual = InstructionUtil.getOperation("000101");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyJAsBranch() {
		Operation expected = Operation.BRANCH;
		Operation actual = InstructionUtil.getOperation("000010");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyAsDADDU() {
		Func expected = Func.DADDU;
		Func actual = InstructionUtil.getFunc("101101");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyAsDSUBU() {
		Func expected = Func.DSUBU;
		Func actual = InstructionUtil.getFunc("101111");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyAsOR() {
		Func expected = Func.OR;
		Func actual = InstructionUtil.getFunc("100101");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyAsDSLLV() {
		Func expected = Func.DSLLV;
		Func actual = InstructionUtil.getFunc("010100");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyAsSLT() {
		Func expected = Func.SLT;
		Func actual = InstructionUtil.getFunc("101010");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyAsDADDIU() {
		Op expected = Op.DADDIU;
		Op actual = InstructionUtil.getOp("011001");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldClassifyAsANDI() {
		Op expected = Op.ANDI;
		Op actual = InstructionUtil.getOp("001100");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetFuncCodeFromOpcode() {
		String opcode = "00000000010000110000100000101101";
		String expected = "101101";
		String actual = InstructionUtil.getFuncCode(opcode);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetOperationCodeFromOpcode() {
		String opcode = "00000000010000110000100000101101";
		String expected = "000000";
		String actual = InstructionUtil.getOperationCode(opcode);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetRsFromOpcode() {
		String opcode = "00000000010000110000100000101101";
		String expected = "00010";
		String actual = InstructionUtil.getRs(opcode);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetRtFromOpcode() {
		String opcode = "00000000010000110000100000101101";
		String expected = "00011";
		String actual = InstructionUtil.getRt(opcode);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetRdFromOpcode() {
		String opcode = "00000000010000110000100000101101";
		String expected = "00001";
		String actual = InstructionUtil.getRd(opcode);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetImmFromOpcode() {
		String opcode = "00000000010000110000100000101101";
		String expected = "0000100000101101";
		String actual = InstructionUtil.getImm(opcode);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetShiftValue() {
		String registerValue = "0000000000000000000000000000000000000000000000000000000000101101";
		int expected = 45;
		int actual = InstructionUtil.getShiftValue(registerValue);
		Assert.assertEquals(expected, actual);
	}

}
