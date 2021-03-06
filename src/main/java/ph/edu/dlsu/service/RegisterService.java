package ph.edu.dlsu.service;

import java.util.List;

import ph.edu.dlsu.model.Register;

public interface RegisterService {

	void init();

	List<Register> findSystemRegisters();

	List<Register> findGPRs();

	Register find(String registerId);

	Register update(Register register);

}
