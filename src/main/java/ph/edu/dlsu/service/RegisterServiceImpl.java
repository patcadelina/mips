package ph.edu.dlsu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.edu.dlsu.model.Register;

public class RegisterServiceImpl implements RegisterService {

	private static Map<String, Register> registerMap = new HashMap<String, Register>();

	@Override
	public void init() {
		registerMap.put("R0", Register.newInstance("R0", "0000000000000000000000000000000000000000000000000000000000000000"));
		registerMap.put("R1", Register.newInstance("R1", null));
		registerMap.put("R2", Register.newInstance("R2", null));
		registerMap.put("R3", Register.newInstance("R3", null));
		registerMap.put("R4", Register.newInstance("R4", null));
		registerMap.put("R5", Register.newInstance("R5", null));
		registerMap.put("R6", Register.newInstance("R6", null));
		registerMap.put("R7", Register.newInstance("R7", null));
		registerMap.put("R8", Register.newInstance("R8", null));
		registerMap.put("R9", Register.newInstance("R9", null));
		registerMap.put("R10", Register.newInstance("R10", null));
		registerMap.put("R11", Register.newInstance("R11", null));
		registerMap.put("R12", Register.newInstance("R12", null));
		registerMap.put("R13", Register.newInstance("R13", null));
		registerMap.put("R14", Register.newInstance("R14", null));
		registerMap.put("R15", Register.newInstance("R15", null));
		registerMap.put("R16", Register.newInstance("R16", null));
		registerMap.put("R17", Register.newInstance("R17", null));
		registerMap.put("R18", Register.newInstance("R18", null));
		registerMap.put("R19", Register.newInstance("R19", null));
		registerMap.put("R20", Register.newInstance("R20", null));
		registerMap.put("R21", Register.newInstance("R21", null));
		registerMap.put("R22", Register.newInstance("R22", null));
		registerMap.put("R23", Register.newInstance("R23", null));
		registerMap.put("R24", Register.newInstance("R24", null));
		registerMap.put("R25", Register.newInstance("R25", null));
		registerMap.put("R26", Register.newInstance("R26", null));
		registerMap.put("R27", Register.newInstance("R27", null));
		registerMap.put("R28", Register.newInstance("R28", null));
		registerMap.put("R29", Register.newInstance("R29", null));
		registerMap.put("R30", Register.newInstance("R30", null));
		registerMap.put("R31", Register.newInstance("R31", null));
		registerMap.put("PC", Register.newInstance("PC", null));
		registerMap.put("hi", Register.newInstance("hi", null));
		registerMap.put("lo", Register.newInstance("lo", null));
		registerMap.put("c0", Register.newInstance("c0", null));
		registerMap.put("IF/ID.IR", Register.newInstance("IF/ID.IR", null));
		registerMap.put("IF/ID.NPC", Register.newInstance("IF/ID.NPC", null));
		registerMap.put("ID/EX.A", Register.newInstance("ID/EX.A", null));
		registerMap.put("ID/EX.B", Register.newInstance("ID/EX.B", null));
		registerMap.put("ID/EX.NPC", Register.newInstance("ID/EX.NPC", null));
		registerMap.put("ID/EX.IR", Register.newInstance("ID/EX.IR", null));
		registerMap.put("ID/EX.IMM", Register.newInstance("ID/EX.Imm", null));
		registerMap.put("EX/MEM.IR", Register.newInstance("EX/MEM.IR", null));
		registerMap.put("EX/MEM.ALUOutput", Register.newInstance("EX/MEM.ALUOutput", null));
		registerMap.put("EX/MEM.cond", Register.newInstance("EX/MEM.cond", "0"));
		registerMap.put("EX/MEM.B", Register.newInstance("EX/MEM.B", null));
		registerMap.put("MEM/WB.IR", Register.newInstance("MEM/WB.IR", null));
		registerMap.put("MEM/WB.ALUOutput", Register.newInstance("MEM/WB.ALUOutput", null));
		registerMap.put("MEM/WB.LMD", Register.newInstance("MEM/WB.LMD", null));
	}

	@Override
	public List<Register> findAll() {
		List<Register> registers = new ArrayList<Register>();
		for (String key : registerMap.keySet()) {
			registers.add(registerMap.get(key));
		}
		return registers;
	}

	@Override
	public Register find(String registerId) {
		return registerMap.get(registerId);
	}

	@Override
	public Register update(Register register) {
		Register updated = registerMap.get(register.getName());
		updated.setValue(register.getValue());
		return updated;
	}

}
