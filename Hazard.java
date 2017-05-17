public class Hazard{
	public static final int RAW = "1";
	public static final int WAR = "2";
	public static final int WAW = "3"; 
	
	private String instruction1, instruction2;
	private int hazard; 
	
	public Hazard(String instruction1, String instrucion2, int hazard){
		this.instruction1 = instruction1;
		this.instruction2 = instruction2;
		this.hazard = hazard; 
	}


	public String getInstructions(){
		return (instruction1 + "|" + instruction2);
	}

	public String getHazard(){
		switch(this.hazard){
		case RAW: return "RAW";
		case WAR: return "WAR";
		case WAW: return "WAW";
	}
}
