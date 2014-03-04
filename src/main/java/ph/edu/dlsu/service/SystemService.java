package ph.edu.dlsu.service;

import java.io.IOException;

import ph.edu.dlsu.model.Pipeline;

public interface SystemService {

	Pipeline runCycle(int cycle) throws IOException;

	void init();

}
