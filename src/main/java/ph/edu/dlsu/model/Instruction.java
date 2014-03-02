package ph.edu.dlsu.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Instruction {

	private int line;

	private String command;

	public Instruction() {

	}

	private Instruction(int line, String command) {
		this.line = line;
		this.command = command;
	}

	public static Instruction newInstance(int line, String command) {
		return new Instruction(line, command);
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return "Instruction [line=" + line + ", command=" + command + "]";
	}

}
