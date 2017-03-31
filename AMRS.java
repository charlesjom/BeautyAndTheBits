<<<<<<< HEAD
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

public class AMRS {
	private String line;
	private ArrayList<Instruction> instructions;
	private int lineCounter = 1;
	private int errorCounter = 0;
	private boolean withError = false;
	private final String[] keywords = new String[]{"LOAD", "ADD", "SUB", "CMP", "PC", "MAR", "MBR", "OF", "NF", "ZF"};

	public AMRS(String filepath) {
		
		instructions = new ArrayList<Instruction>();

		// file reading
		try {
			FileReader inputFile = new FileReader(filepath);
			BufferedReader buffer = new BufferedReader(inputFile);

			while ((line = buffer.readLine()) != null) {
				
				// parsing
				String statement = line.replaceAll(",", " ");
				String[] instrParts = statement.split("\\s+");
				
				// error checking
				if (instrParts.length != 3) {
					System.out.println(line + "\n\tError at line " + lineCounter+ ". Need one instruction type and two operands.");
					errorCounter++;
					withError = true;
				}
				else if (isNumber(instrParts[1])) {
					System.out.println(line + "\n\tError at line " + lineCounter + ". Unable to load into an immediate value.");
					errorCounter++;
					withError = true;
				}
				else if (Arrays.asList(keywords).contains(instrParts[1]) || Arrays.asList(keywords).contains(instrParts[2])) {
					System.out.println(line + "\n\tError at line " + lineCounter + ". Can't use reserved keywords as operands.");
					errorCounter++;
					withError = true;
				}
				else {
					Instruction i = new Instruction(instrParts[0], instrParts[1], instrParts[2]);
					instructions.add(i);
				}

				lineCounter++;
			}

		} catch (Exception e) {
			System.out.println("Error! Can't read file");
		}

		if (withError == true) System.out.println(errorCounter + " error(s)");

	}

	private boolean isNumber(String part) {
		try {
			Integer.parseInt(part);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		AMRS amrs = new AMRS(args[0]);
	}
}