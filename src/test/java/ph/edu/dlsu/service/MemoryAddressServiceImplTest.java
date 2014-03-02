package ph.edu.dlsu.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ph.edu.dlsu.model.Instruction;

public class MemoryAddressServiceImplTest {

	private MemoryAddressService memoryAddressService = new MemoryAddressServiceImpl();
	private RegisterService registerService = new RegisterServiceImpl();

	@Test
	public void shouldCompile() {
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

		registerService.init();
		memoryAddressService.compile(instructions);
	}

}
