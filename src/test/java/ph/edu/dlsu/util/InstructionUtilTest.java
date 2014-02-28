package ph.edu.dlsu.util;

import org.junit.Test;

import ph.edu.dlsu.model.Instruction;

public class InstructionUtilTest {

	@Test
	public void should() {
		Instruction instruction = new Instruction();
		instruction.setRequest("DADDU R1, R2, R3");
		System.out.println(InstructionUtil.generateOpcode(instruction));
	}

}
