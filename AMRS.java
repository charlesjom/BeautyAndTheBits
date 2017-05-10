import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AMRS {
	public static final String[] keywords = new String[]{"LOAD", "ADD", "SUB", "CMP", "PC", "MAR", "MBR", "OF", "NF", "ZF"};
	public static final String[] instruction_types = new String[]{"LOAD", "ADD", "SUB", "CMP"};
	public static final String[] otherRegisters = new String[]{"PC", "MAR", "MBR", "OF", "NF", "ZF"};
	public static final int NO_OF_REGISTERS = 38;

	private String line;
	private int lineCounter = 1;
	private int errorCounter = 0;
	private boolean withError = false;
	private int done = 0;
	
	private int clockCycles = 0;
	private ArrayList<Instruction> instructions;
	private ArrayList<Register> inUse;
	private HashMap<String, Register> registers;

	private String dest, src;

	private Instruction executing = null;
	private int result, op1, op2;

	public AMRS(String filepath) {
		instructions = new ArrayList<Instruction>();

		// file reading
		try {
			FileReader inputFile = new FileReader(filepath);
			BufferedReader buffer = new BufferedReader(inputFile);

			while ((line = buffer.readLine()) != null) {
				Instruction instruction = new Instruction(line, lineCounter);

				if (instruction.getValidity() == true) instructions.add(instruction);
				else {
					withError = true;
					errorCounter++;
				}

				lineCounter++;
			}

		} catch (Exception e) {
			System.out.println("Error! Can't read file");
		}

		if (withError == true) System.out.println("\n" + errorCounter + " error(s)");
	}

	// initialize all the registers
	private void initRegisters() {
		registers = new HashMap<String, Register>(NO_OF_REGISTERS);
		for(String s : otherRegisters) {
			registers.put(s, new Register());
		}
		for(int i=1; i<=32; i++) {
			String registerName = "R" + i;
			registers.put(registerName, new Register());
		}
	}

	// print all the registers
	private void printRegisters() {
		int i=0;
		System.out.println("Register\tValue\tRegister\tValue\tRegister\tValue");
		for (String key: registers.keySet()) {
			System.out.print(key + "\t\t" + registers.get(key).getValue() + "\t");
			i++;
			if (i>0 && i%3==0) System.out.println("");
		}
		System.out.println("");
		
	}

	// start AMRS
	public int start() {
		initRegisters();

		registers.get("PC").setValue(0);

		while (true) {
			writeBack();
			memoryAccess();
			execute();
			decode();									
			fetch();

			System.out.println("Clock Cycle: " + clockCycles);
			printRegisters();
			System.out.println("");
			clockCycles++;

			if (done == instructions.size()) break;
		}
		return clockCycles - 1;
	}

	// stages
	private void fetch() {
		int address = registers.get("PC").getValue();		// get adrress of instruction
		registers.get("MAR").setValue(address);

		if (address < instructions.size()) {
			registers.get("PC").setValue(address+1);		// increment PC
		}
	}

	private void decode() {
		if (registers.get("MAR").getValue() > 0) {
			executing = instructions.get(registers.get("MAR").getValue() - 1);	// get instruction
		}
	}

	private void execute() {
		// if there is no instruction to execute
		if (executing == null) return;
		dest = executing.getFirstOp();
		src = executing.getSecondOp();

		switch (executing.getInstType()) {
			case "LOAD":
				result = Integer.parseInt(src);
				// sets OF to 1 if the value loaded is more than 2 digits, 0 otherwise
				if (result > 99) registers.get("OF").setValue(1);	
				else registers.get("OF").setValue(0);
				break;

			case "ADD":
				op1 = registers.get(dest).getValue();
				op2 = registers.get(src).getValue();
				result = op1 + op2;
				// sets OF to 1 if the value loaded is more than 2 digits, 0 otherwise
				if (result > 99) registers.get("OF").setValue(1);	
				else registers.get("OF").setValue(0);
				break;

			case "SUB":
				op1 = registers.get(dest).getValue();
				op2 = registers.get(src).getValue();
				result = op1 - op2;
				// sets OF to 1 if the value loaded is more than 2 digits, 0 otherwise
				if (result > 99) registers.get("OF").setValue(1);
				else registers.get("OF").setValue(0);
				// sets NF to 1 if the value loaded is negative 
				if (result < 0) registers.get("NF").setValue(1);
				else registers.get("NF").setValue(0);
				break;

			case "CMP":
				op1 = registers.get(dest).getValue();
				op2 = registers.get(src).getValue();
				result = op1 - op2;
				// sets ZF to 1 if the result is 0, 0 otherwise
				if (result == 0) registers.get("ZF").setValue(1);
				else registers.get("ZF").setValue(0);
				// sets NF to 1 if the result is negative, 0 otherwise
				if (result < 0) registers.get("NF").setValue(1);
				break;
		}
		// destination queue
		// enqueue dest register
	}

	private void memoryAccess() {
		registers.get("MBR").setValue(result);
	}

	private void writeBack() {
		// check if destination queue is empty
		if (dest != null) {
			// dequeue destination

			// assign to dequeued register the value in MBR
			registers.get(dest).setValue(registers.get("MBR").getValue());
			executing = null;
			done++;
		}
	}

	public static void main(String[] args) {
		AMRS amrs = new AMRS(args[0]);
		int ccc = amrs.start();
		
		System.out.println("Total clock cycles consumed: " + ccc);
		System.out.println("Total number of stalls: ");
	}
}