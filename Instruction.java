import java.util.Arrays;
import java.lang.NumberFormatException;

public class Instruction {
	private String instructionType;
	private String parameter1;
	private String parameter2;
	private Operation operation;
	
	private int lineCount;
	private boolean valid;
	private boolean done;
	public String[] errorMessages = new String[] {"Need one instruction type and two operands.",
		"Destination register can't be an immediate value", "Can't use reserved keywords as operands.",
		"Instruction type not valid.", "Source can only be a register or an integer"
	};
	private boolean[] errorMsgStatus;

	public Instruction(String statement, int lineCount) {
		this.valid = true;
		this.done = false;
		this.lineCount = lineCount;
		errorMsgStatus = new boolean[errorMessages.length];
		initialize(statement);
	}

	// initialize instruction
	public void initialize(String statement) {
		// check if instruction is valid or not
		// error checking
		for (boolean b: errorMsgStatus) {
			b = false;
		}

		String instruction = statement.replaceAll(",", " ");
		String[] instrParts = instruction.split("\\s+");

		if (instrParts.length != 3) {
			valid = false;
			errorMsgStatus[0] = true;
		}
		if (isNumber(instrParts[1])) {
			valid = false;
			errorMsgStatus[1] = true;
		}
		if (Arrays.asList(AMRS.keywords).contains(instrParts[1]) || Arrays.asList(AMRS.keywords).contains(instrParts[2])) {
			valid = false;
			errorMsgStatus[2] = true;
		}
		
		if (Arrays.asList(AMRS.instruction_types).contains(instrParts[0]) == false) {
			valid = false;
			errorMsgStatus[3] = true;
		}
		if (isIntegerRegister(instrParts[1]) == false || isNumber(instrParts[1])) {
			valid = false;
			errorMsgStatus[4] = true;
		}

		if (valid == false) {
			System.out.println("Error at line " + lineCount);
			for (int i = 0; i<errorMsgStatus.length; i++) {
				if (errorMsgStatus[i] == true) System.out.println("\t" + errorMessages[i]);
			}
		}
		else {
			instructionType = instrParts[0];
			parameter1 = instrParts[1];
			parameter2 = instrParts[2];
		}
	}

	private boolean isNumber(String part) {
		try {
			Integer.parseInt(part);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private boolean isIntegerRegister(String part) {
		return part.startsWith("R");
	}

	// getters
	public String getInstType() {
		return this.instructionType;
	}

	public boolean isValid() {
		return this.valid;
	}

	public String getFirstOp() {
		return this.parameter1;
	}

	public String getSecondOp() {
		return this.parameter2;
	}
<<<<<<< HEAD
=======

	public void initOperation(String op1, String op2) {
		operation = new Operation(op1, op2);
	}

	public Operation getOperation() {
		return this.operation;
	}
>>>>>>> milestone2
}