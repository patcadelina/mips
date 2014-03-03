package ph.edu.dlsu.model;

public class OperationParams {

	private final String opcode;

	private final String operationCode;

	private final Operation operation;

	private final Func func;

	private final Op op;

	public static class Builder {
		private String opcode;
		private String operationCode;
		private Operation operation;
		private Func func;
		private Op op;

		public Builder opcode(String opcode) {
			this.opcode = opcode;
			return this;
		}

		public Builder operationCode(String operationCode) {
			this.operationCode = operationCode;
			return this;
		}

		public Builder operation(Operation operation) {
			this.operation = operation;
			return this;
		}

		public Builder func(Func func) {
			this.func = func;
			return this;
		}

		public Builder op(Op op) {
			this.op = op;
			return this;
		}

		public OperationParams build() {
			return new OperationParams(this);
		}
	}

	public OperationParams(Builder builder) {
		this.func = builder.func;
		this.op = builder.op;
		this.opcode = builder.opcode;
		this.operation = builder.operation;
		this.operationCode = builder.operationCode;
	}

	public String getOpcode() {
		return opcode;
	}

	public String getOperationCode() {
		return operationCode;
	}

	public Operation getOperation() {
		return operation;
	}

	public Func getFunc() {
		return func;
	}

	public Op getOp() {
		return op;
	}

}
