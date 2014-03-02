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
import ph.edu.dlsu.util.AluUtil;
import ph.edu.dlsu.util.BinaryHexUtil;
import ph.edu.dlsu.util.InstructionUtil;

public class SystemServiceImpl implements SystemService {

	private static final int INSTRUCTION_OFFSET = 3;
	private RegisterService registerService = new RegisterServiceImpl();
	private MemoryAddressService memoryAddressService = new MemoryAddressServiceImpl();
	private Map<Integer, Set<Process>> processMap = new HashMap<Integer, Set<Process>>();

	@Override
	public Pipeline runCycle(int cycle) throws IOException {
		writeBack(cycle);
		mem(cycle);
		execute(cycle);
		decode(cycle);
		fetch(cycle);

		return Pipeline.newInstance(cycle, getCurrentProcesses(cycle));
	}

	private void writeBack(int cycle) {
		Set<Process> lastCycle = getLastCycleProcesses(cycle);
		if (!lastCycle.isEmpty()) {
			boolean hasMEM = false;
			for (Process p : lastCycle) {
				if(ProcessStatus.MEM.equals(p.getStatus())) {
					p.setStatus(ProcessStatus.WB);
					hasMEM = true;
					break;
				}
			}
			if (hasMEM) {
				Register mem_wb_ir = registerService.find("MEM/WB.IR");
				Register mem_wb_aluoutput = registerService.find("MEM/WB.ALUOutput");
				Register mem_wb_lmd = registerService.find("MEM/WB.LMD");

				String opcode = mem_wb_ir.getValue();
				String operationCode = InstructionUtil.getOperationCode(opcode);
				Operation operation = InstructionUtil.getOperation(operationCode);

				switch(operation) {
				case ALU:
					Register aluDest = registerService.find("R" + Integer.parseInt(InstructionUtil.getRd(opcode), 2));
					aluDest.setValue(mem_wb_aluoutput.getValue());
					registerService.update(aluDest);
					break;
				case IMM:
					Register immDest = registerService.find("R" + Integer.parseInt(InstructionUtil.getRt(opcode), 2));
					immDest.setValue(mem_wb_aluoutput.getValue());
					registerService.update(immDest);
					break;
				case LOAD_STORE:
					Op op = InstructionUtil.getOp(operationCode);
					if (op.equals(Op.LD)) {
						Register ldDest = registerService.find("R" + Integer.parseInt(InstructionUtil.getRt(opcode), 2));
						ldDest.setValue(mem_wb_lmd.getValue());
						registerService.update(ldDest);
					}
					break;
				case BRANCH:
				case INVALID:
				default:
					break;
				}
			}
		}
	}

	private void mem(int cycle) {
		Set<Process> lastCycle = getLastCycleProcesses(cycle);
		if (!lastCycle.isEmpty()) {
			boolean hasEX = false;
			for (Process p : lastCycle) {
				if(ProcessStatus.EX.equals(p.getStatus())) {
					p.setStatus(ProcessStatus.MEM);
					hasEX = true;
					break;
				}
			}
			if (hasEX) {
				Register mem_wb_ir = registerService.find("MEM/WB.IR");
				Register mem_wb_aluoutput = registerService.find("MEM/WB.ALUOutput");
				Register mem_wb_lmd = registerService.find("MEM/WB.LMD");

				Register ex_mem_ir = registerService.find("EX/MEM.IR");
				Register ex_mem_aluoutput = registerService.find("EX/MEM.ALUOutput");
				Register ex_mem_b = registerService.find("EX/MEM.B");

				String operationCode = InstructionUtil.getOperationCode(ex_mem_ir.getValue());
				Operation operation = InstructionUtil.getOperation(operationCode);

				switch(operation) {
				case ALU:
				case IMM:
					mem_wb_aluoutput.setValue(ex_mem_aluoutput.getValue());
					break;
				case LOAD_STORE:
					Op op = InstructionUtil.getOp(operationCode);
					if (op.equals(Op.LD)) {
						List<MemoryAddress> addresses = findMemoryAddresses(ex_mem_aluoutput);
						int begin = 0;
						int end = 8;
						int offset = 8;
						int i = 2;
						for (MemoryAddress ma : addresses) {
							ma.setValue(ex_mem_b.getValue().substring(begin, end));
							memoryAddressService.update(ma);
							begin = end;
							end = offset * i++;
						}
					}
					else if (op.equals(Op.SD)) {
						List<MemoryAddress> addresses = findMemoryAddresses(ex_mem_aluoutput);
						StringBuilder builder = new StringBuilder();
						for (MemoryAddress ma : addresses) {
							builder.append(ma.getValue());
						}
						mem_wb_lmd.setValue(builder.toString());
					}
					break;
				case BRANCH:
				case INVALID:
				default:
					break;
				}

				mem_wb_ir.setValue(ex_mem_ir.getValue());

				registerService.update(mem_wb_ir);
				registerService.update(mem_wb_aluoutput);
				registerService.update(mem_wb_lmd);
			}
		}
		
	}

	private List<MemoryAddress> findMemoryAddresses(Register ex_mem_aluoutput) {
		String startAddress = BinaryHexUtil.toNBitHex(Long.parseLong(ex_mem_aluoutput.getValue(), 2), 4);
		String endAddress = BinaryHexUtil.toNBitHex(Long.parseLong(startAddress, 16) + 7, 4);
		return memoryAddressService.find(startAddress, endAddress);
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
				Register id_ex_imm = registerService.find("ID/EX.Imm");
				Register id_ex_npc = registerService.find("ID/EX.NPC");

				String operationCode = InstructionUtil.getOperationCode(id_ex_ir.getValue());
				Operation operation = InstructionUtil.getOperation(operationCode);

				if (Operation.ALU.equals(operation)) {
					Func func = InstructionUtil.getFunc(InstructionUtil.getFuncCode(id_ex_ir.getValue()));
					switch(func) {
					case DADDU:
						ex_mem_aluoutput.setValue(AluUtil.executeDADDU(id_ex_a, id_ex_b));
						break;
					case DSUBU:
						ex_mem_aluoutput.setValue(AluUtil.executeDSUBU(id_ex_a, id_ex_b));
						break;
					case OR:
						ex_mem_aluoutput.setValue(AluUtil.executeOR(id_ex_a, id_ex_b));
						break;
					case DSLLV:
						ex_mem_aluoutput.setValue(AluUtil.executeDSLLV(id_ex_a, id_ex_b));
						break;
					case SLT:
						ex_mem_aluoutput.setValue(AluUtil.executeSLT(id_ex_a, id_ex_b));
						break;
					case INVALID:
					default:
						break;
					}
					ex_mem_cond.setValue("0");
				}

				else if (Operation.IMM.equals(operation)) {
					Op op = InstructionUtil.getOp(operationCode);
					if (op.equals(Op.DADDIU)) {
						ex_mem_aluoutput.setValue(AluUtil.executeDADDIU(id_ex_a, id_ex_imm));
					}
					else if (op.equals(Op.ANDI)) {
						ex_mem_aluoutput.setValue(AluUtil.executeANDI(id_ex_a, id_ex_imm));
					}
					ex_mem_cond.setValue("0");
				}

				else if (Operation.LOAD_STORE.equals(operation)) {
					ex_mem_aluoutput.setValue(AluUtil.executeDADDIU(id_ex_a, id_ex_imm));
					ex_mem_cond.setValue("0");
				}

				else if (Operation.BRANCH.equals(operation)) {
					ex_mem_aluoutput.setValue(AluUtil.executeDADDIU(id_ex_npc, id_ex_imm));
					Op op = InstructionUtil.getOp(operationCode);
					if (op.equals(Op.J)) {
						ex_mem_cond.setValue("1");
					} else if (op.equals(Op.BNEZ)) {
						ex_mem_cond.setValue(AluUtil.executeNEZ(id_ex_a) ? "1" : "0");
					}
				}

				ex_mem_ir.setValue(id_ex_ir.getValue());
				ex_mem_b.setValue(id_ex_b.getValue());

				registerService.update(ex_mem_ir);
				registerService.update(ex_mem_aluoutput);
				registerService.update(ex_mem_cond);
				registerService.update(ex_mem_b);
			}
		}
	}

	private Set<Process> getLastCycleProcesses(int cycle) {
		return null == processMap.get(cycle - 1) ? new HashSet<Process>() : processMap.get(cycle - 1);
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
				Register rs = registerService.find("R" + Integer.parseInt(InstructionUtil.getRs(opcode), 2));
				Register rt = registerService.find("R" + Integer.parseInt(InstructionUtil.getRt(opcode), 2));

				id_ex_a.setValue(rs.getValue());
				id_ex_b.setValue(rt.getValue());
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
