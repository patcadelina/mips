package ph.edu.dlsu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.edu.dlsu.model.Instruction;
import ph.edu.dlsu.model.MemoryAddress;
import ph.edu.dlsu.model.Register;
import ph.edu.dlsu.util.BinaryHexUtil;
import ph.edu.dlsu.util.InstructionUtil;

public class MemoryAddressServiceImpl implements MemoryAddressService {

	private static final int HEX = 16;
	private static final String START_ADDRESS = "0000";
	private static Map<String, MemoryAddress> memoryMap = new HashMap<String, MemoryAddress>();
	private RegisterService registerService = new RegisterServiceImpl();

	@Override
	public void init() {
		for (int i = 0; i <= MemoryAddress.MAX_MEMORY; i++) {
			String address = BinaryHexUtil.toNBitHex(i, 4);
			memoryMap.put(address, MemoryAddress.newInstance(address, null));
		}
	}

	@Override
	public List<MemoryAddress> find(String startAddress, String endAddress) {
		Integer start = Integer.parseInt(startAddress, HEX);
		Integer end = Integer.parseInt(endAddress, HEX);
		List<MemoryAddress> memoryAddresses = new ArrayList<MemoryAddress>();
		for (; start <= end; start++) {
			memoryAddresses.add(memoryMap.get(BinaryHexUtil.toNBitHex(start.intValue(), 4)));
		}
		return memoryAddresses;
	}

	@Override
	public MemoryAddress update(MemoryAddress request) {
		MemoryAddress memoryAddress = memoryMap.get(request.getAddress());
		memoryAddress.setValue(request.getValue());
		memoryMap.put(request.getAddress(), memoryAddress);
		return memoryAddress;
	}

	@Override
	public void compile(List<Instruction> instructions) {
		List<Instruction> processed = InstructionUtil.preprocessReferences(instructions);
		for (Instruction instruction : processed) {
			String opcode = InstructionUtil.generateOpcode(instruction);
			String address = computeInstructionAddress(instruction.getLine());
			saveInstruction(address, opcode);
		}
		Register register = Register.newInstance("PC", START_ADDRESS);
		registerService.update(register);
	}

	private String computeInstructionAddress(int line) {
		return BinaryHexUtil.toNBitHex((line - 1) * 4, 4);
	}

	private void saveInstruction(String address, String opcode) {
		Map<String, String> instructionMap = buildMemoryInstruction(address, opcode);
		for (String key : instructionMap.keySet()) {
			memoryMap.put(key, MemoryAddress.newInstance(key, instructionMap.get(key)));
		}
	}

	private Map<String, String> buildMemoryInstruction(String address, String opcode) {
		Map<String, String> instructionMap = new HashMap<String, String>();
		int addStart = Integer.parseInt(address, 16);
		int addEnd = addStart + 4;
		int opcodeStart = 0;
		int opcodeEnd = 8;
		int offset = 8;
		int i = 2;
		while (addStart < addEnd) {
			instructionMap.put(BinaryHexUtil.toNBitHex(addStart, 4), opcode.substring(opcodeStart, opcodeEnd));
			addStart++;
			opcodeStart = opcodeEnd;
			opcodeEnd = offset * i++;
		}
		return instructionMap;
	}
}
