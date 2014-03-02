package ph.edu.dlsu.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ph.edu.dlsu.model.Func;
import ph.edu.dlsu.model.MemoryAddress;
import ph.edu.dlsu.model.Op;
import ph.edu.dlsu.model.Operation;
import ph.edu.dlsu.model.Pipeline;
import ph.edu.dlsu.model.Process;
import ph.edu.dlsu.model.ProcessStatus;
import ph.edu.dlsu.model.Register;
import ph.edu.dlsu.util.BinaryHexUtil;
import ph.edu.dlsu.util.InstructionUtil;

public class SystemServiceImpl implements SystemService {

	private static final int INSTRUCTION_OFFSET = 3;
	private RegisterService registerService = new RegisterServiceImpl();
	private MemoryAddressService memoryAddressService = new MemoryAddressServiceImpl();
	private Map<Integer, Set<Process>> processMap = new HashMap<Integer, Set<Process>>();

	@Override
	public Pipeline runCycle(int cycle) throws IOException {
		execute(cycle);
		decode(cycle);
		fetch(cycle);
	
		return null;
	}

	private void execute(int cycle) throws IOException {
		Set<Process> lastCycle = getLastCycleProcesses(cycle);
		if (!lastCycle.isEmpty()) {
			boolean hasID = false;
			for (Process p : lastCycle) {
				if(ProcessStatus.ID.equals(p.getStatus())) {
					p.setStatus(ProcessStatus.EX);
					hasID = true;
					break;
				}
			}
			if (hasID) {
				Register ex_mem_ir = registerService.find("EX/MEM.IR");
				Register ex_mem_aluoutput = registerService.find("EX/MEM.ALUOutput");
				Register ex_mem_cond = registerService.find("EX/MEM.cond");
				Register ex_mem_b = registerService.find("EX/MEM.B");

				Register id_ex_ir = registerService.find("ID/EX.IR");
				Register id_ex_a = registerService.find("ID/EX.A");
				Register id_ex_b = registerService.find("ID/EX.B");
				
				String operationCode = InstructionUtil.getOperationCode(id_ex_ir.getValue());
				Operation operation = InstructionUtil.getOperation(operationCode);
				if (Operation.ALU.equals(operation)) {
					Func func = InstructionUtil.getFunc(InstructionUtil.getFuncCode(id_ex_ir.getValue()));
					switch(func) {
					case DADDU:
						ex_mem_aluoutput.setValue(executeDADDU(id_ex_a, id_ex_b));
						break;
					case DSUBU:
						ex_mem_aluoutput.setValue(executeDSUBU(id_ex_a, id_ex_b));
						break;
					case OR:
						ex_mem_aluoutput.setValue(executeOR(id_ex_a, id_ex_b));
						break;
					case DSLLV:
						ex_mem_aluoutput.setValue(executeDSLLV(id_ex_a, id_ex_b));
						break;
					case SLT:
						ex_mem_aluoutput.setValue(executeSLT(id_ex_a, id_ex_b));
						break;
					case INVALID:
					default:
					}
				}

				else if (Operation.IMM.equals(operation)) {
					Op op = InstructionUtil.getOp(operationCode);
				}

				else if (Operation.LOAD_STORE.equals(operation)) {
					
				}

				else if (Operation.BRANCH.equals(operation)) {
					
				}
			}
		}
	}

	private Set<Process> getLastCycleProcesses(int cycle) {
		return null == processMap.get(cycle - 1) ? new HashSet<Process>() : processMap.get(cycle - 1);
	}

	private String executeDADDU(Register id_ex_a, Register id_ex_b) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		long b = Long.parseLong(id_ex_b.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a + b, 64);
	}

	private String executeDSUBU(Register id_ex_a, Register id_ex_b) {
		long a = Long.parseLong(id_ex_a.getValue(), 2);
		long b = Long.parseLong(id_ex_b.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a - b, 64);
	}

	private String executeOR(Register id_ex_a, Register id_ex_b) {
		int a = Integer.parseInt(id_ex_a.getValue(), 2);
		int b = Integer.parseInt(id_ex_b.getValue(), 2);
		return BinaryHexUtil.toNBitBinary(a | b, 64);
	}

	private String executeSLT(Register id_ex_a, Register id_ex_b) throws IOException {
		long a = BinaryHexUtil.parseSignedBinary(id_ex_a.getValue());
		long b = BinaryHexUtil.parseSignedBinary(id_ex_b.getValue());
		return BinaryHexUtil.toNBitBinary(a < b ? 1 : 0, 64);
	}

	private String executeDSLLV(Register id_ex_a, Register id_ex_b) {
		int shift = InstructionUtil.getShiftValue(id_ex_b.getValue());
		String shifted = id_ex_a.getValue().substring(shift);
		StringBuilder builder = new StringBuilder(shifted);
		for (; shift > 0; shift--) {
			builder.append("0");
		}
		return builder.toString();
	}

	private void decode(int cycle) {
		Set<Process> lastCycle = getLastCycleProcesses(cycle);
		if (!lastCycle.isEmpty()) {
			boolean hasIF = false;
			for (Process p : lastCycle) {
				if(ProcessStatus.IF.equals(p.getStatus())) {
					p.setStatus(ProcessStatus.ID);
					hasIF = true;
					break;
				}
			}
			if (hasIF) {
				Register if_id_ir = registerService.find("IF/ID.IR");
				Register if_id_npc = registerService.find("IF/ID.NPC");

				Register id_ex_a = registerService.find("ID/EX.A");
				Register id_ex_b = registerService.find("ID/EX.B");
				Register id_ex_imm = registerService.find("ID/EX.Imm");
				Register id_ex_npc = registerService.find("ID/EX.NPC");
				Register id_ex_ir = registerService.find("ID/EX.IR");

				String opcode = if_id_ir.getValue();
				Register registerA = registerService.find("R" + Integer.parseInt(InstructionUtil.getRegisterA(opcode), 2));
				Register registerB = registerService.find("R" + Integer.parseInt(InstructionUtil.getRegisterB(opcode), 2));

				id_ex_a.setValue(registerA.getValue());
				id_ex_b.setValue(registerB.getValue());
				id_ex_imm.setValue(InstructionUtil.getImm(opcode));
				id_ex_npc.setValue(if_id_npc.getValue());
				id_ex_ir.setValue(if_id_ir.getValue());

				registerService.update(id_ex_a);
				registerService.update(id_ex_b);
				registerService.update(id_ex_imm);
				registerService.update(id_ex_npc);
				registerService.update(id_ex_ir);
			}
		}
	}

	private void fetch(int cycle) {
		Register pc = registerService.find("PC");
		Set<Process> processes = getCurrentProcesses(cycle);
		processes.add(Process.newInstance(pc.getValue(), ProcessStatus.IF));
		processMap.put(cycle, processes);
		
		Register if_id_ir = registerService.find("IF/ID.IR");
		String opcode = fetchInstruction(pc.getValue());
		if_id_ir.setValue(opcode);

		Register if_id_npc = registerService.find("IF/ID.NPC");
		if (isExMemCondSet()) {
			Register ex_mem_aluoutput = registerService.find("EX/MEM.ALUOutput");
			if_id_npc.setValue(ex_mem_aluoutput.getValue());
			pc.setValue(ex_mem_aluoutput.getValue());
		} else {
			String nextPC = incrementPC(pc);
			if_id_npc.setValue(nextPC);
			pc.setValue(nextPC);
		}

		registerService.update(if_id_ir);
		registerService.update(pc);
		registerService.update(if_id_npc);
	}

	private Set<Process> getCurrentProcesses(int cycle) {
		return null == processMap.get(cycle) ? new HashSet<Process>() : processMap.get(cycle);
	}

	private boolean isExMemCondSet() {
		return registerService.find("EX/MEM.cond").getValue().equals("0000000000000000000000000000000000000000000000000000000000000001");
	}

	private String fetchInstruction(String startAddress) {
		String end = BinaryHexUtil.toNBitHex(Integer.parseInt(startAddress, 16) + INSTRUCTION_OFFSET, 4);
		List<MemoryAddress> memoryAddresses = memoryAddressService.find(startAddress, end);
		StringBuilder builder = new StringBuilder();
		for (MemoryAddress ma : memoryAddresses) {
			builder.append(ma.getValue());
		}
		return builder.toString();
	}

	private String incrementPC(Register pc) {
		int nextPC = Integer.parseInt(pc.getValue(), 16) + 4;
		return BinaryHexUtil.toNBitHex(nextPC, 4);
	}

}
