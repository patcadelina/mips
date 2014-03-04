package ph.edu.dlsu.model;

public class Hazard {

	private DataLocation dataLocation;

	private Op op;

	private Hazard(DataLocation dataLocation, Op op) {
		this.dataLocation = dataLocation;
		this.op = op;
	}

	public static Hazard newInstance(DataLocation dataLocation, Op op) {
		return new Hazard(dataLocation, op);
	}

	public DataLocation getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(DataLocation dataLocation) {
		this.dataLocation = dataLocation;
	}

	public Op getOp() {
		return op;
	}

	public void setOp(Op op) {
		this.op = op;
	}

}
