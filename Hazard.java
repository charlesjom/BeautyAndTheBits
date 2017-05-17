public class Hazard{
	public static final int RAW = 1;
	public static final int WAR = 2;
	public static final int WAW = 3; 
	
	private String instruction1, instruction2;
	private int hazard; 
	
	public Hazard(String instruction1, String instruction2, int hazard){
		this.instruction1 = new String(instruction1);
		this.instruction2 = new String(instruction2);
		this.hazard = hazard; 
	}

	public String getInstructions(){
		return (instruction1 + " | " + instruction2);
	}

	public String getHazard(){
		String hazard = "";

		switch(this.hazard) {
			case RAW:
				hazard = "RAW";
				break;
			case WAR:
				hazard = "WAR";
				break;
			case WAW:
				hazard = "WAW";
				break;
		}
		return hazard;
	}
}
