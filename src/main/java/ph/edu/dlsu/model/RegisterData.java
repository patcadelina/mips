package ph.edu.dlsu.model;

public class RegisterData {

	private Register register;

	private DataLocation dataLocation;

	public RegisterData() {

	}

	private RegisterData(Register register, DataLocation dataLocation) {
		this.register = register;
		this.dataLocation = dataLocation;
	}

	public static RegisterData newInstance(Register register, DataLocation dataLocation) {
		return new RegisterData(register, dataLocation);
	}

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register register) {
		this.register = register;
	}

	public DataLocation getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(DataLocation dataLocation) {
		this.dataLocation = dataLocation;
	}

}
