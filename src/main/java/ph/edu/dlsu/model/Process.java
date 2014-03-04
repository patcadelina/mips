package ph.edu.dlsu.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Process {

	private String address;

	private ProcessStatus status;

	public Process() {

	}

	private Process(String address, ProcessStatus status) {
		this.address = address;
		this.status = status;
	}

	public static Process newInstance(String address, ProcessStatus status) {
		return new Process(address, status);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ProcessStatus getStatus() {
		return status;
	}

	public void setStatus(ProcessStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Process [address=" + address + ", status=" + status + "]";
	}

}
