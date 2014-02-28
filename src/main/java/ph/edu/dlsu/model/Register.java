package ph.edu.dlsu.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Register {

	private String name;

	private Long value;

	public Register() {

	}

	private Register(String name, Long value) {
		this.name = name;
		this.value = value;
	}

	public static Register newInstance(String name, Long value) {
		return new Register(name, value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Register [name=" + name + ", value=" + value + "]";
	}

}
