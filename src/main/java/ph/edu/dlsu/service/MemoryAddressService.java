package ph.edu.dlsu.service;

import java.util.List;

import ph.edu.dlsu.model.Instruction;
import ph.edu.dlsu.model.MemoryAddress;

public interface MemoryAddressService {

	void init();

	List<MemoryAddress> find(String startAddress, String endAddress);

	MemoryAddress update(MemoryAddress request);

	String create(Instruction request);

}
