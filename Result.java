import java.util.ArrayList;

public class Result {
	private int stalls;
	private int clockCycles;
	private ArrayList<Hazard> hazardsEncountered;

	public Result (int stalls, int cc, ArrayList<Hazard> he) {
		this.stalls = stalls;
		this.clockCycles = cc;
		this.hazardsEncountered = he;
	}

	public int getStalls() {
		return this.stalls;
	}

	public int getClockCyclesConsumed() {
		return this.clockCycles;
	}

	public void printHazards() {
		for (Hazard hazard : hazardsEncountered) {
			System.out.println(hazard.getInstructions() + " - " + hazard.getHazard());
		}
	}
}