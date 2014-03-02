package ph.edu.dlsu.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Pipeline {

	private int clock;

	private List<Process> processes;

	public Pipeline() {

	}

	private Pipeline(int clock, List<Process> processes) {
		this.clock = clock;
		this.processes = processes;
	}

	public static Pipeline newInstance(int clock, List<Process> processes) {
		return new Pipeline(clock, processes);
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}

	public List<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}

}
