package ph.edu.dlsu.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ph.edu.dlsu.model.DataLocation;
import ph.edu.dlsu.model.Func;
import ph.edu.dlsu.model.Hazard;
import ph.edu.dlsu.model.MemoryAddress;
import ph.edu.dlsu.model.Op;
import ph.edu.dlsu.model.Operation;
import ph.edu.dlsu.model.OperationParams;
import ph.edu.dlsu.model.Pipeline;
import ph.edu.dlsu.model.Process;
import ph.edu.dlsu.model.ProcessStatus;
import ph.edu.dlsu.model.Register;
import ph.edu.dlsu.util.AluUtil;
import ph.edu.dlsu.util.BinaryHexUtil;
import ph.edu.dlsu.util.InstructionUtil;

public class SystemServiceImpl implements SystemService {

	private static final int INSTRUCTION_OFFSET = 3;
	private static boolean fetch = false;
	private static boolean stall = false;
	private static Map<Integer, Set<Process>> processMap = new HashMap<Integer, Set<Process>>();
	private static Map<String, Hazard> registerDataMap = new HashMap<String, Hazard>();
	private static Set<Process> stalled = new HashSet<Process>();

	private RegisterService registerService = new RegisterServiceImpl();
	private MemoryAddressService memoryAddressService = new MemoryAddressServiceImpl();

	@Override
	public void init() {
		processMap = new HashMap<Integer, Set<Process>>();
		registerDataMap = new HashMap<String, Hazard>();
	}

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
			String address = "";
			for (Process p : lastCycle) {
				if(ProcessStatus.MEM.equals(p.getStatus())) {
					p.setStatus(ProcessStatus.WB);
					address = p.getAddress();
					hasMEM = true;
					break;
				}
			}
			if (hasMEM) {
				Register mem_wb_ir = registerService.find("MEM/WB.IR");
				Register mem_wb_aluoutput = registerService.find("MEM/WB.ALUOutput");
				Register mem_wb_lmd = registerService.find("MEM/WB.LMD");

				String opcode = mem_wb_ir.getValue();
				OperationParams params = buildOperationParams(mem_wb_ir);
				if (params.getOperation().equals(Operation.BRANCH)) {
					Set<Process> processes = getCurrentProcesses(cycle);
					processes.add(Process.newInstance(address, ProcessStatus.WB));
					processMap.put(cycle, processes);
					return;
				}

				switch(params.getOperation()) {
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
					Op op = InstructionUtil.getOp(params.getOperationCode());
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

				Set<Process> processes = getCurrentProcesses(cycle);
				processes.add(Process.newInstance(address, ProcessStatus.WB));
				processMap.put(cycle, processes);

				filterRegisterDataMap();
			}
		}
	}

	private void filterRegisterDataMap() {
		Map<String, Hazard> filtered = new HashMap<String, Hazard>(); 
		for (String key : registerDataMap.keySet()) {
			if (!DataLocation.MEM.equals(registerDataMap.get(key).getDataLocation())) {
				filtered.put(key, registerDataMap.get(key));
			}
		}
		registerDataMap = filtered;
	}

	private Set<Process> getLastCycleProcesses(int cycle) {
		Set<Process> processes = new HashSet<Process>();
		if (null == processMap.get(cycle - 1)) {
			return processes;
		} else {
			processes.addAll(processMap.get(cycle - 1));
			for (Process p : stalled) {
				System.out.println("Adding stalled proc: " + p);
				processes.add(p);
			}
			processMap.put(cycle - 1, processes);
			stalled = new HashSet<Process>();
			return processes;
		}
	}

	private void mem(int cycle) {
		Set<Process> lastCycle = getLastCycleProcesses(cycle);
		if (!lastCycle.isEmpty()) {
			boolean hasEX = false;
			String address = "";
			for (Process p : lastCycle) {
				if(ProcessStatus.EX.equals(p.getStatus())) {
					p.setStatus(ProcessStatus.MEM);
					address = p.getAddress();
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

				String opcode = ex_mem_ir.getValue();
				OperationParams params = buildOperationParams(ex_mem_ir);
				if (params.getOperation().equals(Operation.BRANCH)) {
					Set<Process> processes = getCurrentProcesses(cycle);
					processes.add(Process.newInstance(address, ProcessStatus.MEM));
					processMap.put(cycle, processes);
					return;
				}

				switch(params.getOperation()) {
				case ALU:
					mem_wb_aluoutput.setValue(ex_mem_aluoutput.getValue());
					updateRegisterDataMapLocation(InstructionUtil.getRd(opcode), Hazard.newInstance(DataLocation.MEM, null));
					break;
				case IMM:
					mem_wb_aluoutput.setValue(ex_mem_aluoutput.getValue());
					updateRegisterDataMapLocation(InstructionUtil.getRt(opcode), Hazard.newInstance(DataLocation.MEM, null));
					break;
				case LOAD_STORE:
					if (params.getOp().equals(Op.SD)) {
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
						updateRegisterDataMapLocation(InstructionUtil.getRt(opcode), Hazard.newInstance(DataLocation.MEM, Op.LD));
					}
					else if (params.getOp().equals(Op.LD)) {
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

				mem_wb_ir.setValue(opcode);

				registerService.update(mem_wb_ir);
				registerService.update(mem_wb_aluoutput);
				registerService.update(mem_wb_lmd);

				Set<Process> processes = getCurrentProcesses(cycle);
				processes.add(Process.newInstance(address, ProcessStatus.MEM));
				processMap.put(cycle, processes);
			}
		}
		
	}

	private void updateRegisterDataMapLocation(String registerId, Hazard hazard) {
		if (null != registerDataMap.get(registerId)) {
			registerDataMap.remove(registerId);
		}
		registerDataMap.put(registerId, hazard);
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
			String address = "";
			for (Process p : lastCycle) {
				if(ProcessStatus.ID.equals(p.getStatus())) {
					p.setStatus(ProcessStatus.EX);
					address = p.getAddress();
					hasID = true;
					break;
				}
			}
			if (hasID) {
				Register ex_mem_ir = registerService.find("EX/MEM.IR");
				Register ex_mem_aluoutput = registerService.find("EX/MEM.ALUOutput");
				Register ex_mem_b = registerService.find("EX/MEM.B");

				Register id_ex_ir = registerService.find("ID/EX.IR");
				Register id_ex_a = registerService.find("ID/EX.A");
				Register id_ex_b = registerService.find("ID/EX.B");
				Register id_ex_imm = registerService.find("ID/EX.Imm");

				String opcode = id_ex_ir.getValue();
				OperationParams params = buildOperationParams(id_ex_ir);
				if (params.getOperation().equals(Operation.BRANCH)) {
					Register ex_mem_cond = registerService.find("EX/MEM.cond");
					if (ex_mem_cond.getValue().equals("1")) {
						fetch = true;
						ex_mem_cond.setValue("0");
						registerService.update(ex_mem_cond);

						Set<Process> processes = getCurrentProcesses(cycle);
						processes.add(Process.newInstance(address, ProcessStatus.EX));
						processMap.put(cycle, processes);
					}
					return;
				}

				if (Operation.ALU.equals(params.getOperation())) {
					Register rs = Register.newInstance("rs", resolveRsValue(opcode, id_ex_a));
					Register rt = Register.newInstance("rt", resolveRtValue(opcode, id_ex_b));
					switch(params.getFunc()) {
					case DADDU:
						ex_mem_aluoutput.setValue(AluUtil.executeDADDU(rs, rt));
						break;
					case DSUBU:
						ex_mem_aluoutput.setValue(AluUtil.executeDSUBU(rs, rt));
						break;
					case OR:
						ex_mem_aluoutput.setValue(AluUtil.executeOR(rs, rt));
						break;
					case DSLLV:
						ex_mem_aluoutput.setValue(AluUtil.executeDSLLV(rs, rt));
						break;
					case SLT:
						ex_mem_aluoutput.setValue(AluUtil.executeSLT(rs, rt));
						break;
					case INVALID:
					default:
						break;
					}
					registerDataMap.put(InstructionUtil.getRd(opcode), Hazard.newInstance(DataLocation.EX, null));
				}

				else if (Operation.IMM.equals(params.getOperation())) {
					Register rs = Register.newInstance("rs", resolveRsValue(opcode, id_ex_a));
					if (params.getOp().equals(Op.DADDIU)) {
						ex_mem_aluoutput.setValue(AluUtil.executeDADDIU(rs, id_ex_imm));
					}
					else if (params.getOp().equals(Op.ANDI)) {
						ex_mem_aluoutput.setValue(AluUtil.executeANDI(rs, id_ex_imm));
					}
					registerDataMap.put(InstructionUtil.getRt(opcode), Hazard.newInstance(DataLocation.EX, null));
				}

				else if (Operation.LOAD_STORE.equals(params.getOperation())) {
					Register rs = Register.newInstance("rs", resolveRsValue(opcode, id_ex_a));
					ex_mem_aluoutput.setValue(AluUtil.executeDADDIU(rs, id_ex_imm));

					if (Op.LD.equals(params.getOp())) {
						registerDataMap.put(InstructionUtil.getRt(opcode), Hazard.newInstance(DataLocation.EX, Op.LD));
					}
				}

				ex_mem_ir.setValue(id_ex_ir.getValue());
				ex_mem_b.setValue(id_ex_b.getValue());

				registerService.update(ex_mem_ir);
				registerService.update(ex_mem_aluoutput);
				registerService.update(ex_mem_b);

				Set<Process> processes = getCurrentProcesses(cycle);
				processes.add(Process.newInstance(address, ProcessStatus.EX));
				processMap.put(cycle, processes);
			}
		}
	}

	private String resolveRsValue(String opcode, Register register) {
		String rs = InstructionUtil.getRs(opcode);
		if (registerDataMap.containsKey(rs)) {
			switch(registerDataMap.get(rs).getDataLocation()) {
			case EX:
				return registerService.find("EX/MEM.ALUOutput").getValue();
			case MEM:
				return registerService.find("MEM/WB.ALUOutput").getValue();
			case NIL:
			default:
			}
		}
		return register.getValue();
	}

	private String resolveRtValue(String opcode, Register register) {
		String rt = InstructionUtil.getRt(opcode);
		if (registerDataMap.containsKey(rt)) {
			switch(registerDataMap.get(rt).getDataLocation()) {
			case EX:
				return registerService.find("EX/MEM.ALUOutput").getValue();
			case MEM:
				return registerService.find("MEM/WB.ALUOutput").getValue();
			case NIL:
			default:
			}
		}
		return register.getValue();
	}

	private void decode(int cycle) {
		Set<Process> lastCycle = getLastCycleProcesses(cycle);
		if (!lastCycle.isEmpty()) {
			boolean hasIF = false;
			String address = "";
			Process cached = new Process();
			for (Process p : lastCycle) {
				if(ProcessStatus.IF.equals(p.getStatus())) {
					cached.setAddress(p.getAddress());
					cached.setStatus(ProcessStatus.IF);
					p.setStatus(ProcessStatus.ID);
					address = p.getAddress();
					hasIF = true;
					break;
				}
			}
			if(!stalled.isEmpty()){
				hasIF = true;
				stalled.clear();
			}
			if (hasIF) {
				if (fetch) {
					fetch = !fetch;
					return;
				}

				Register pc = registerService.find("PC");
				Register if_id_ir = registerService.find("IF/ID.IR");
				Register if_id_npc = registerService.find("IF/ID.NPC");

				String opcode = if_id_ir.getValue();
				OperationParams params = buildOperationParams(if_id_ir);
				if (params.getOperation().equals(Operation.BRANCH) && params.getOp().equals(Op.BNEZ)) {
					String registerId = InstructionUtil.getRs(opcode);
					if (registerDataMap.containsKey(registerId) && registerDataMap.get(registerId).equals(DataLocation.EX)) {
						stall = true;
						stalled.add(cached);
						return;
					} else {
						String nextPC = incrementPC(pc);
						Register register = registerService.find("R" + Integer.parseInt(registerId, 2));
						if (AluUtil.executeNEZ(register)) {
							String branchAddress = BinaryHexUtil.toNBitHex(Long.parseLong(nextPC, 16) + Long.parseLong(InstructionUtil.getImm(opcode), 16) * 4, 4);
							if_id_npc.setValue(branchAddress);
							pc.setValue(branchAddress);

							Register ex_mem_cond = registerService.find("EX/MEM.cond");
							ex_mem_cond.setValue("1");
							registerService.update(ex_mem_cond);
						} else {
							if_id_npc.setValue(nextPC);
							pc.setValue(nextPC);
						}
						registerService.update(if_id_npc);
						registerService.update(pc);
						
						Set<Process> processes = getCurrentProcesses(cycle);
						processes.add(Process.newInstance(address, ProcessStatus.ID));
						processMap.put(cycle, processes);
						
						return;
					}
				}
				Register id_ex_a = registerService.find("ID/EX.A");
				Register id_ex_b = registerService.find("ID/EX.B");
				Register id_ex_imm = registerService.find("ID/EX.Imm");
				Register id_ex_npc = registerService.find("ID/EX.NPC");
				Register id_ex_ir = registerService.find("ID/EX.IR");

				Register rs = registerService.find("R" + Integer.parseInt(InstructionUtil.getRs(opcode), 2));
				Register rt = registerService.find("R" + Integer.parseInt(InstructionUtil.getRt(opcode), 2));
				
				String rsId = InstructionUtil.getRs(opcode);
				String rtId = InstructionUtil.getRt(opcode);
				if (registerDataMap.containsKey(rsId) && registerDataMap.get(rsId).getOp().equals(Op.LD)) {
					stall = true;
					stalled.add(cached);
					return;
				}
				if (registerDataMap.containsKey(rtId) && registerDataMap.get(rtId).getOp().equals(Op.LD)) {
					stall = true;
					stalled.add(cached);
					return;
				}

				id_ex_a.setValue(rs.getValue());
				id_ex_b.setValue(rt.getValue());
				id_ex_imm.setValue(InstructionUtil.getImm(opcode));
				id_ex_npc.setValue(if_id_npc.getValue());
				id_ex_ir.setValue(opcode);

				registerService.update(id_ex_a);
				registerService.update(id_ex_b);
				registerService.update(id_ex_imm);
				registerService.update(id_ex_npc);
				registerService.update(id_ex_ir);

				Set<Process> processes = getCurrentProcesses(cycle);
				processes.add(Process.newInstance(address, ProcessStatus.ID));
				processMap.put(cycle, processes);
			}
		}
	}

	private OperationParams buildOperationParams(Register ir) {
		String opcode = ir.getValue();
		String operationCode = InstructionUtil.getOperationCode(opcode);
		Operation operation = InstructionUtil.getOperation(operationCode);
		OperationParams.Builder builder = new OperationParams.Builder().opcode(opcode).operationCode(operationCode).operation(operation);

		switch(operation) {
		case ALU:
			Func func = InstructionUtil.getFunc(InstructionUtil.getFuncCode(opcode));
			builder.func(func);
			break;

		case IMM:
		case LOAD_STORE:
		case BRANCH:
			Op op = InstructionUtil.getOp(operationCode);
			builder.op(op);
			break;

		case INVALID:
		default:
			break;
		}

		return builder.build();
	}

	private void fetch(int cycle) {
		if (stall) {
			stall = !stall;
			return;
		}
		Register pc = registerService.find("PC");
		String nextPC = incrementPC(pc);
		Register if_id_ir = registerService.find("IF/ID.IR");
		Register if_id_npc = registerService.find("IF/ID.NPC");

		String opcode = fetchInstruction(pc.getValue());
		if (null == opcode || "".equals(opcode)) {
			return;
		}
		if_id_ir.setValue(opcode);
		if_id_npc.setValue(nextPC);
		pc.setValue(nextPC);

		registerService.update(if_id_ir);
		registerService.update(if_id_npc);
		registerService.update(pc);

		Set<Process> processes = getCurrentProcesses(cycle);
		processes.add(Process.newInstance(decrementPC(pc), ProcessStatus.IF));
		processMap.put(cycle, processes);
	}

	private Set<Process> getCurrentProcesses(int cycle) {
		return null == processMap.get(cycle) ? new HashSet<Process>() : processMap.get(cycle);
	}

	private String fetchInstruction(String startAddress) {
		String end = BinaryHexUtil.toNBitHex(Integer.parseInt(startAddress, 16) + INSTRUCTION_OFFSET, 4);
		List<MemoryAddress> memoryAddresses = memoryAddressService.find(startAddress, end);
		StringBuilder builder = new StringBuilder();
		for (MemoryAddress ma : memoryAddresses) {
			if (null == ma.getValue()) {
				break;
			}
			builder.append(ma.getValue());
		}
		return builder.toString();
	}

	private String incrementPC(Register pc) {
		int nextPC = Integer.parseInt(pc.getValue(), 16) + 4;
		return BinaryHexUtil.toNBitHex(nextPC, 4);
	}

	private String decrementPC(Register pc) {
		int nextPC = Integer.parseInt(pc.getValue(), 16) - 4;
		return BinaryHexUtil.toNBitHex(nextPC, 4);
	}

}
