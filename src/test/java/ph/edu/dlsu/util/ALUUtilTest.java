package ph.edu.dlsu.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import ph.edu.dlsu.model.Register;

public class ALUUtilTest {

	@Test
	public void shouldExecuteDADDU() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000101");
		Register id_ex_b = Register.newInstance("R2", "0000000000000000000000000000000000000000000000000000000000000111");
		String expected = "0000000000000000000000000000000000000000000000000000000000001100";
		String actual = AluUtil.executeDADDU(id_ex_a, id_ex_b);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldExecuteDSUBU() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000111");
		Register id_ex_b = Register.newInstance("R2", "0000000000000000000000000000000000000000000000000000000000000101");
		String expected = "0000000000000000000000000000000000000000000000000000000000000010";
		String actual = AluUtil.executeDSUBU(id_ex_a, id_ex_b);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldExecuteOR() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000111");
		Register id_ex_b = Register.newInstance("R2", "0000000000000000000000000000000000000000000000000000000000000101");
		String expected = "0000000000000000000000000000000000000000000000000000000000000111";
		String actual = AluUtil.executeOR(id_ex_a, id_ex_b);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldNotSetOnExecuteSLT() throws IOException {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000111");
		Register id_ex_b = Register.newInstance("R2", "0000000000000000000000000000000000000000000000000000000000000101");
		String expected = "0000000000000000000000000000000000000000000000000000000000000000";
		String actual = AluUtil.executeSLT(id_ex_a, id_ex_b);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldSetOnExecuteSLT() throws IOException {
		Register id_ex_a = Register.newInstance("R1", "1111111111111111111111111111111111111111111111111111111111111111");
		Register id_ex_b = Register.newInstance("R2", "0000000000000000000000000000000000000000000000000000000000000000");
		String expected = "0000000000000000000000000000000000000000000000000000000000000001";
		String actual = AluUtil.executeSLT(id_ex_a, id_ex_b);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldExecuteDSLLV() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000111");
		Register id_ex_b = Register.newInstance("R2", "0000000000000000000000000000000000000000000000000000000000000101");
		String expected = "0000000000000000000000000000000000000000000000000000000011100000";
		String actual = AluUtil.executeDSLLV(id_ex_a, id_ex_b);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldExecuteDADDIU() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000111");
		Register id_ex_imm = Register.newInstance("ID/EX.IMM", "0001000000000000");
		String expected = "0000000000000000000000000000000000000000000000000001000000000111";
		String actual = AluUtil.executeDADDIU(id_ex_a, id_ex_imm);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldExecuteANDI() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000111");
		Register id_ex_imm = Register.newInstance("ID/EX.IMM", "0001000000000100");
		String expected = "0000000000000000000000000000000000000000000000000000000000000100";
		String actual = AluUtil.executeANDI(id_ex_a, id_ex_imm);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldReturnTrueOnExecuteNEZ() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000111");
		boolean expected = true;
		boolean actual = AluUtil.executeNEZ(id_ex_a);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void shouldReturnFalseOnExecuteNEZ() {
		Register id_ex_a = Register.newInstance("R1", "0000000000000000000000000000000000000000000000000000000000000000");
		boolean expected = false;
		boolean actual = AluUtil.executeNEZ(id_ex_a);
		Assert.assertEquals(expected, actual);
	}

}
