package ph.edu.dlsu.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ph.edu.dlsu.model.Instruction;

public class InstructionUtilTest {

	@Test
	public void shouldDecodeDADDU() {
		Instruction instruction = new Instruction();
		instruction.setRequest("DADDU R1, R2, R3");
		String expected = "00000000010000110000100000101101";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeDSUBU() {
		Instruction instruction = new Instruction();
		instruction.setRequest("DSUBU R1, R2, R3");
		String expected = "00000000010000110000100000101111";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeOR() {
		Instruction instruction = new Instruction();
		instruction.setRequest("OR R1, R2, R3");
		String expected = "00000000010000110000100000100111";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeDSLLV() {
		Instruction instruction = new Instruction();
		instruction.setRequest("DSLLV R1, R2, R3");
		String expected = "00000000010000110000100000010100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeSLT() {
		Instruction instruction = new Instruction();
		instruction.setRequest("SLT R1, R2, R3");
		String expected = "00000000010000110000100000101010";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeBNEZ() {
		Instruction instruction = new Instruction();
		instruction.setRequest("BNEZ R1, 4");
		String expected = "00010100001000000000000000000011";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeLD() {
		Instruction instruction = new Instruction();
		instruction.setRequest("LD R1, 2004(R2)");
		String expected = "11011100010000010010000000000100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeSD() {
		Instruction instruction = new Instruction();
		instruction.setRequest("SD R1, 2004(R2)");
		String expected = "11111100010000010010000000000100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeDADDIU() {
		Instruction instruction = new Instruction();
		instruction.setRequest("DADDIU R1, R2, #1000");
		String expected = "01100100010000010001000000000000";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeANDI() {
		Instruction instruction = new Instruction();
		instruction.setRequest("ANDI R1, R2, #1000");
		String expected = "00110000010000010001000000000000";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldDecodeJ() {
		Instruction instruction = new Instruction();
		instruction.setRequest("J 5");
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

		Assert.assertEquals(expected.get(1).getRequest(), actual.get(1).getRequest());
		Assert.assertEquals(expected.get(3).getRequest(), actual.get(3).getRequest());
	}

	@Test
	public void shouldParseInstructionWithReference() {
		Instruction instruction = new Instruction();
		instruction.setRequest("L1: LD R4, 2004(R3)");
		String expected = "11011100011001000010000000000100";
		String actual = InstructionUtil.generateOpcode(instruction);
		Assert.assertEquals(expected, actual);
	}

}
