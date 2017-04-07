public class Operation{
	
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