public class Results {
	int stalls;
	int clockCycles;

	public Results(int stalls, int cc) {
		this.stalls = stalls;
		this.clockCycles = cc;
	}

	public int getStalls() {
		return this.stalls;
	}

	public int getClockCyclesConsumed() {
		return this.clockCycles;
	}
}