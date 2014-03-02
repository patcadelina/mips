package ph.edu.dlsu.model;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Pipeline {

	private int clock;

	private Set<Process> processes;

	public Pipeline() {

	}

	private Pipeline(int clock, Set<Process> processes) {
		this.clock = clock;
		this.processes = processes;
	}

	public static Pipeline newInstance(int clock, Set<Process> processes) {
		return new Pipeline(clock, processes);
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}

	public Set<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(Set<Process> processes) {
		this.processes = processes;
	}

}
