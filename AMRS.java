import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AMRS {
	public static final String[] keywords = new String[]{"LOAD", "ADD", "SUB", "CMP", "PC", "MAR", "MBR", "OF", "NF", "ZF"};
	public static final String[] instruction_types = new String[]{"LOAD", "ADD", "SUB", "CMP"};
	public static final int NO_OF_REGISTERS = 38;

	private final String[] otherRegisters = new String[]{"PC", "MAR", "MBR", "OF", "NF", "ZF"};

	private String line;
	private int lineCounter = 1;
	private int errorCounter = 0;
	private boolean withError = false;
	
	private int clockCycles = 0;
	private ArrayList<Instruction> instructions;
	private ArrayList<Register> inUse;
	private HashMap<String, Register> registers;

	private Instruction fetching, decoding, executing, memaccessing, writing;

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

	public void start() {
		initRegisters();
		// while (true) {

		// }
		printRegisters();
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
		for (String key: registers.keySet()) {
			Register r = registers.get(key);
			System.out.println(key + " : " + r.getValue());
		}
	}

	// fetch
	private void fetch(Instruction i) {

	}

	// decode
	private void decode(Instruction i) {

	}

	// execute
	private void execute(Instruction i) {

	}

	// memory access
	private void memoryAccess(Instruction i) {

	}

	// write back
	private void writeBack(Instruction i) {

	}

	public static void main(String[] args) {
		AMRS amrs = new AMRS(args[0]);
		amrs.start();

		System.out.println("Total clock cycles consumed: ");
		System.out.println("Total number of stalls: ");
	}
}