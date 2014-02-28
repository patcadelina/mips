package ph.edu.dlsu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.edu.dlsu.model.Instruction;
import ph.edu.dlsu.model.MemoryAddress;

public class MemoryAddressServiceImpl implements MemoryAddressService {

	private static final int HEX = 16;
	private static Map<String, MemoryAddress> memoryMap = new HashMap<String, MemoryAddress>();

	@Override
	public void init() {
		for (int i = 0; i <= MemoryAddress.MAX_MEMORY; i++) {
			String address = Integer.toHexString(i).toUpperCase();
			memoryMap.put(address, MemoryAddress.newInstance(address, null));
		}
	}

	@Override
	public List<MemoryAddress> find(String startAddress, String endAddress) {
		Integer start = Integer.parseInt(startAddress, HEX);
		Integer end = Integer.parseInt(endAddress, HEX);
		List<MemoryAddress> memoryAddresses = new ArrayList<MemoryAddress>();
		for (; start <= end; start++) {
			memoryAddresses.add(memoryMap.get(Integer.toHexString(start.intValue())));
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
	public String create(Instruction request) {
		// TODO Auto-generated method stub
		return null;
	}

}
