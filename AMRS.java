import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.IOException;

public class AMRS {
	public static final String[] keywords = new String[]{"LOAD", "ADD", "SUB", "CMP", "PC", "MAR", "MBR", "OF", "NF", "ZF"};
	public static final String[] instruction_types = new String[]{"LOAD", "ADD", "SUB", "CMP"};
	public static final String[] otherRegisters = new String[]{"PC", "MAR", "MBR", "OF", "NF", "ZF"};
	public static final int NO_OF_REGISTERS = 38;
	public static final int EXECUTE = 1;
	public static final int MEMACESS = 2;
	public static final int WRITEBK = 3;

	public static HashMap<String, Register> registers;

	private String line;
	private int lineCounter = 1;
	private int errorCounter = 0;
	private boolean withError = false;
	private int done = 0;
	
	private int clockCycles = 1;
	private int stalls = 0;
	private boolean stalled = false;
	private ArrayList<Instruction> instructions;

	
	private Register execUseDest, execUseSrc, memUseDest, memUseSrc, wrtUseDest, wrtUseSrc;
	private boolean inExec, inMemAcs, inWrtBk;

	private String dest, src;

	private Instruction decoding = null;
	private Instruction executing = null;
	private Instruction memAccessing = null;
	private Instruction writing = null;
	private int result, op1, op2;
	private Results results;

	public AMRS(String filepath) {
		instructions = new ArrayList<Instruction>();

		// file reading
		try {
			FileReader inputFile = new FileReader(filepath);
			BufferedReader buffer = new BufferedReader(inputFile);

			while ((line = buffer.readLine()) != null) {
				Instruction instruction = new Instruction(line, lineCounter);

				if (instruction.isValid()) instructions.add(instruction);
				else {
					withError = true;
					errorCounter++;
				}

				lineCounter++;
			}

		} catch (IOException e) {
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
	public Results start() {
		initRegisters();

		registers.get("PC").setValue(1);

		while (true) {
			boolean temp1, temp2, temp3;
			System.out.println("----------------------------------");
			System.out.println("Clock Cycle: " + clockCycles + "\n");
			
			// System.out.println(executing + " " + memAccessing + " " + writing);

			writeBack();
			memoryAccess();
			temp3 = writing != null;

			execute();
			temp2 = memAccessing != null;
		
			// System.out.println(inExec);
			// System.out.println(inMemAcs);
			// System.out.println(inWrtBk);

			decode();
			// System.out.println(executing + " " + memAccessing + " " + writing);
			temp1 = executing != null;

			fetch();

			inExec = temp1;
			inMemAcs = temp2;
			inWrtBk = temp3;

			// printRegisters();
			clockCycles++;
			if (stalled == true) stalls++;
			stalled = false;

			if (done == instructions.size()) break;

			results = new Results(stalls, clockCycles);
		}
		return results;
	}

	// stages
	private void fetch() {
		if (stalled == true) return;
		int address = registers.get("PC").getValue();		// get adrress of instruction
		registers.get("MAR").setValue(address);				// put address to MAR
		if (address > instructions.size()) return ;
		System.out.println("FETCH");
		registers.get("PC").setValue(address+1);		// increment PC
	}

	private void decode() {
		if (registers.get("MAR").getValue() == 0 || registers.get("MAR").getValue() > instructions.size()) return ;
		decoding = instructions.get(registers.get("MAR").getValue() - 1);	// get instruction
		Instruction instBefore = null;
		// Hazard encountered = null;

		if (registers.get("MAR").getValue() - 2 > 0) instBefore = instructions.get(registers.get("MAR").getValue() - 2);	// get instruction

		if (instBefore != null) {		// no stall because first instruction
			Register first = registers.get(decoding.getFirstOp());
			Register second = registers.get(decoding.getSecondOp());

			if ((first == execUseDest && first != null && inExec) || (first == memUseDest && first != null && inMemAcs) || (first == wrtUseDest && first != null && inWrtBk)) {
				stalled = true;
				// encountered = new Hazard(instBefore.getStatement(), decoding.getStatement(), 1);	// WAW
				System.out.println("STALL");
			}
			else if ((second == execUseDest && second != null && inExec) || (second == memUseDest && second != null && inMemAcs) || (second == wrtUseDest && second != null && inWrtBk)) {
				stalled = true;
				// encountered = new Hazard(instBefore.getStatement(), decoding.getStatement(), 2);	// RAW
				System.out.println("STALL");
			}
			else if ((first == execUseSrc && first != null && inExec) || (first == memUseSrc && first != null && inMemAcs) || (first == wrtUseSrc && first != null && inWrtBk)) {
				stalled = true;
				// encountered = new Hazard(instBefore.getStatement, decoding.getStatement(), 3);		// WAR
				System.out.println("STALL");
			}
			else {
				System.out.println("DECODE");
				decoding.initOperation(decoding.getFirstOp(), decoding.getSecondOp());
				executing = decoding;
				decoding = null;
			}
		}
		else {
			System.out.println("DECODE");
			decoding.initOperation(decoding.getFirstOp(), decoding.getSecondOp());
			executing = decoding;
			decoding = null;
		}
	}

	private void execute() {
		// if there is no instruction to execute
		if (executing == null) return ;
		System.out.println("EXECUTE");
		executing.getOperation().operate(executing.getInstType(), EXECUTE);
		execUseDest  = registers.get(executing.getFirstOp());
		execUseSrc  = registers.get(executing.getSecondOp());
		memAccessing = executing;
		executing = null;
	}

	private void memoryAccess() {
		if (memAccessing == null) return ;
		System.out.println("MEMORY ACCESS");
		memAccessing.getOperation().operate(memAccessing.getInstType(), MEMACESS);
		memUseDest  = registers.get(memAccessing.getFirstOp());
		memUseSrc  = registers.get(memAccessing.getSecondOp());
		writing = memAccessing;
		memAccessing = null;
	}

	private void writeBack() {
		if (writing == null) return ;
		System.out.println("WRITE BACK");
		writing.getOperation().operate(writing.getInstType(), WRITEBK);
		wrtUseDest  = registers.get(writing.getFirstOp());
		wrtUseSrc = registers.get(writing.getSecondOp());
		writing = null;
		done++;
	}

	public static void main(String[] args) {
		AMRS amrs = new AMRS(args[0]);
		Results result = amrs.start();
		
		System.out.println("-----------------------------------------------\nAMRS done...");
		System.out.println("Total clock cycles consumed: " + result.getClockCyclesConsumed());
		System.out.println("Total number of stalls: " + result.getStalls());
		System.out.println("Hazards Encountered:");
	}
}