public class Instruction {
	private String instructionType;
	private String parameter1;
	private String parameter2;

	public Instruction(String inst, String param1, String param2) {
		this.instructionType = inst;
		this.parameter1 = param1;
		this.parameter2 = param2;
	}

	public String getInstType() {
		return instructionType;
	}
}