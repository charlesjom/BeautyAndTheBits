<<<<<<< HEAD
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

public class Operation{

	private HashMap<String, Register> registers;	
	public Operation(){

	}

	public void add(Register op1, Register op2){
		int temp1, temp2;

		temp1 = op1.getValue();
		temp2 = op2.getValue();

		
		//set overflow flag to 1 temp1>=100 && temp2>=100, else set to 0
		if(temp1>=100 && temp2>=100){
			registers.get("OF").setValue(1);
		}
		else{
			registers.get("OF").setValue(0);
		}	

		temp1 = temp1 + temp2;
		op1.setValue(temp1);
	}

	public void sub(Register op1, Register op2){

		int temp1, temp2;

		temp1 = op1.getValue();
		temp2 = op2.getValue();

		
		//set overflow flag to 1 if temp1>=100 && temp2>=100, else set to 0
		if(temp1>=100 && temp2>=100){
			registers.get("OF").setValue(1);
		}
		else{
			registers.get("OF").setValue(0);
		}	

		temp1 = temp1 - temp2;
		op1.setValue(temp1);
	}

	public void cmp(Register op1, Register op2){

		int temp1, temp2;

		temp1 = op1.getValue();
		temp2 = op2.getValue();

		temp1 = temp1 - temp2;

		//if set zero flag(ZF) to 1 if the answer == 0,
		//else if the answer is < 0, set negative flag(NF) to 1
		//else if answer > 0 is set to negative flag(NF) and zero flag(ZF) to 0
		if(temp1 == 0){
			registers.get("ZF").setValue(1);
		}
		else if(temp1<0){
			registers.get("NF").setValue(1);
		}	
		else{
			registers.get("ZF").setValue(0);
			registers.get("NF").setValue(0);
		}


		op1.setValue(temp1);
	}

	public void load(Register op1, int op2){

		int temp1, temp2;

		temp1 = op1.getValue();
		temp2 = op2;

		

		//set overflow flag to 1 if temp1>=100 && temp2>=100, else set to 0
		if(temp1>=100 && temp2>=100){
			registers.get("OF").setValue(1);
		}
		else{
			registers.get("OF").setValue(0);
		}	

		op1.setValue(temp1);

	}
}
=======
public class Operation{
	int temp1, temp2, result;
	String op1, op2;
	Register oFlag, zFlag, nFlag, mbr;

	public Operation(String op1, String op2){
		this.op1 = op1;
		this.op2 = op2;

		oFlag = AMRS.registers.get("OF");
		zFlag = AMRS.registers.get("ZF");
		nFlag = AMRS.registers.get("NF");
		mbr = AMRS.registers.get("MBR");
	}

	public void operate(String operation, int currentStage) {
		switch(operation) {
			case "LOAD":
				load(currentStage);
				break;
			case "ADD":
				add(currentStage);
				break;
			case "SUB":
				sub(currentStage);
				break;
			case "CMP":
				cmp(currentStage);
				break;
		}
	}

	public void add(int currentStage){
		temp1 = AMRS.registers.get(op1).getValue();
		temp2 = AMRS.registers.get(op2).getValue();

		switch(currentStage) {
			case 1:
				result = temp1 + temp2;
				//set overflow flag to 1 resukt>=100, else set to 0
				if(result>=100) oFlag.setValue(1);
				else oFlag.setValue(0);
				break;
			
			case 2:
				mbr.setValue(result);
				break;

			case 3:
				AMRS.registers.get(op1).setValue(result);
				break;
		}
	}

	public void sub(int currentStage){
		temp1 = AMRS.registers.get(op1).getValue();
		temp2 = AMRS.registers.get(op2).getValue();

		switch(currentStage) {
			case 1:
				result = temp1 - temp2;
				//set overflow flag to 1 resukt>=100, else set to 0
				if(result>=100) oFlag.setValue(1);
				else oFlag.setValue(0);
				break;
			
			case 2:
				mbr.setValue(result);
				break;

			case 3:
				AMRS.registers.get(op1).setValue(result);
				break;
		}
	}

	public void cmp(int currentStage){
		temp1 = AMRS.registers.get(op1).getValue();
		temp2 = AMRS.registers.get(op2).getValue();

		switch(currentStage) {
			case 1:
				result = temp1 - temp2;
				//if set zero flag(ZF) to 1 if the answer == 0,
				//else if the answer is < 0, set negative flag(NF) to 1
				//else if answer > 0 is set to negative flag(NF) and zero flag(ZF) to 0
				if(result == 0)	zFlag.setValue(1);
				else if(result<0) nFlag.setValue(1);
				else {
					zFlag.setValue(0);
					nFlag.setValue(0);
				}
				break;
			
			case 2:
				mbr.setValue(result);
				break;

			case 3:
				AMRS.registers.get(op1).setValue(result);
				break;
		}
	}

	public void load(int currentStage){
		temp1 = AMRS.registers.get(op1).getValue();

		switch(currentStage) {
			case 1:
				temp2 = Integer.parseInt(op2);
				//set overflow flag to 1 if op2>=100, else set to 0
				if(temp2>=100) oFlag.setValue(1);
				else oFlag.setValue(0);
				break;
			
			case 2:
				mbr.setValue(temp2);
				break;

			case 3:
				AMRS.registers.get(op1).setValue(temp2);
				break;
		}

	}
}
>>>>>>> milestone2
