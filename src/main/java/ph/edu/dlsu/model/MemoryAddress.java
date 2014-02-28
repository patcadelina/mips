package ph.edu.dlsu.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MemoryAddress {

	public static final int MIN_UPDATABLE = 0x2000;
	public static final int MAX_MEMORY = 0x3FFF;

	private String address;

	private Long value;

	public MemoryAddress() {

	}

	private MemoryAddress(String address, Long value) {
		this.address = address;
		this.value = value;
	}

	public static MemoryAddress newInstance(String address, Long value) {
		return new MemoryAddress(address, value);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "MemoryAddress [address=" + address + ", value=" + value + "]";
	}

}
