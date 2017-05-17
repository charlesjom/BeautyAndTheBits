import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
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
	private ArrayList<Hazard> hazards;

	private boolean stalled = false;
	private boolean doneWriting = false;
	private ArrayList<Instruction> instructions;
	
	private Register execUseDest, execUseSrc, memUseDest, memUseSrc, wrtUseDest, wrtUseSrc;
	private boolean inDcd, inExec, inMemAcs, inWrtBk;

	private String dest, src;

	private Instruction decoding = null;
	private Instruction executing = null;
	private Instruction memAccessing = null;
	private Instruction writing = null;
	private int result, op1, op2;
	private Result results;

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
	public Result start() {
		initRegisters();
		hazards = new ArrayList<Hazard>();

		registers.get("PC").setValue(1);

		while (true) {
			boolean temp1, temp2, temp3;		
			System.out.println("----------------------------------");		
			System.out.println("Clock Cycle: " + clockCycles + "\n");

			inWrtBk = writeBack();
			inMemAcs = memoryAccess();
			inExec = execute();
			inDcd = decode();									
			fetch();

			// update instructions in pipeline
			if (doneWriting) {
				writing = null;
				done++;
				doneWriting = false;
			}
			if (inMemAcs) {
				writing = memAccessing;
				memAccessing = null;
			}
			if (inExec) {
				memAccessing = executing;
				executing = null;
			}
			if (inDcd && stalled == false) {
				executing = decoding;
				decoding = null;
			}

			if (stalled == true) stalls++;
			stalled = false;

			printRegisters();
			clockCycles++;

			if (done == instructions.size() || clockCycles > 20) break;

			results = new Result(stalls, clockCycles, hazards);
		}
		return results;
	}

	// stages
	private void fetch() {
		if (stalled == true) return;
		int address = registers.get("PC").getValue();		// get adrress of instruction

		registers.get("MAR").setValue(address);				// put address to MAR
		if (address > instructions.size()) return ;
		// System.out.println("FETCH");
		registers.get("PC").setValue(address+1);		// increment PC
	}

	private boolean decode() {
		if (registers.get("MAR").getValue() == 0 || registers.get("MAR").getValue() > instructions.size()) return false;
		decoding = instructions.get(registers.get("MAR").getValue() - 1);	// get instruction

		Register first = registers.get(decoding.getFirstOp());
		Register second = registers.get(decoding.getSecondOp());

		if ((first == execUseDest && first != null && inExec) || (first == memUseDest && first != null && inMemAcs) || (first == wrtUseDest && first != null && inWrtBk)) {
			stalled = true;
			if (first == wrtUseDest && first != null && inWrtBk) hazards.add(new Hazard(writing.getStatement(), decoding.getStatement(), Hazard.WAW));	// WAW
			if (first == memUseDest && first != null && inMemAcs) hazards.add(new Hazard(memAccessing.getStatement(), decoding.getStatement(), Hazard.WAW));	// WAW
			if (first == execUseDest && first != null && inExec) hazards.add(new Hazard(executing.getStatement(), decoding.getStatement(), Hazard.WAW));	// WAW
		}
		if ((second == execUseDest && second != null && inExec) || (second == memUseDest && second != null && inMemAcs) || (second == wrtUseDest && second != null && inWrtBk)) {
			stalled = true;
			if (second == wrtUseDest && second != null && inWrtBk) hazards.add(new Hazard(writing.getStatement(), decoding.getStatement(), Hazard.RAW));	// RAW
			if (second == memUseDest && second != null && inMemAcs) hazards.add(new Hazard(memAccessing.getStatement(), decoding.getStatement(), Hazard.RAW));	// RAW
			if (second == execUseDest && second != null && inExec) hazards.add(new Hazard(executing.getStatement(), decoding.getStatement(), Hazard.RAW));	// RAW
		}
		if ((first == execUseSrc && first != null && inExec) || (first == memUseSrc && first != null && inMemAcs) || (first == wrtUseSrc && first != null && inWrtBk)) {
			stalled = true;
			if (first == wrtUseSrc && first != null && inWrtBk) hazards.add(new Hazard(writing.getStatement(), decoding.getStatement(), Hazard.WAR));	// WAR
			if (first == memUseSrc && first != null && inMemAcs) hazards.add(new Hazard(memAccessing.getStatement(), decoding.getStatement(), Hazard.WAR));	// WAR
			if (first == execUseSrc && first != null && inExec) hazards.add(new Hazard(executing.getStatement(), decoding.getStatement(), Hazard.WAR));	// WAR
		}

		if (stalled == false) {
			// System.out.println("DECODE");
			decoding.initOperation(decoding.getFirstOp(), decoding.getSecondOp());
			return true;
		}
		else {
			// System.out.println("STALL");
			return false;
		}
	}

	private boolean execute() {
		// if there is no instruction to execute
		if (executing == null) return false;
		// System.out.println("EXECUTE");
		executing.getOperation().operate(executing.getInstType(), EXECUTE);
		execUseDest  = registers.get(executing.getFirstOp());
		execUseSrc  = registers.get(executing.getSecondOp());
		return true;
	}

	private boolean memoryAccess() {
		if (memAccessing == null) return false;
		// System.out.println("MEMORY ACCESS");
		memAccessing.getOperation().operate(memAccessing.getInstType(), MEMACESS);
		memUseDest  = registers.get(memAccessing.getFirstOp());
		memUseSrc  = registers.get(memAccessing.getSecondOp());
		return true;
	}

	private boolean writeBack() {
		if (writing == null) return false;
		// System.out.println("WRITE BACK");
		writing.getOperation().operate(writing.getInstType(), WRITEBK);
		wrtUseDest  = registers.get(writing.getFirstOp());
		wrtUseSrc = registers.get(writing.getSecondOp());
		doneWriting = true;
		return true;
	}

	public boolean check() {
		return withError;
	}

	public static void main(String[] args) {
		AMRS amrs = new AMRS(args[0]);
		boolean withError = amrs.check();
		if (withError) return;
		Result results = amrs.start();
		System.out.println("\n----------------------------------------------------------");
		System.out.println("Total clock cycles consumed: " + results.getClockCyclesConsumed());
		System.out.println("Total number of stalls: " + results.getStalls());
		System.out.println("Hazards Encountered:");
		results.printHazards();
	}
}
