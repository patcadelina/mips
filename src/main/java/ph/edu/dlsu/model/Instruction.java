package ph.edu.dlsu.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Instruction {

	private int line;

	private String request;

	public Instruction() {

	}

	private Instruction(int line, String request) {
		this.line = line;
		this.request = request;
	}

	public static Instruction newInstance(int line, String request) {
		return new Instruction(line, request);
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "Instruction [line=" + line + ", request=" + request + "]";
	}

}
