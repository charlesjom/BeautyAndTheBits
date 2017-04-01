public class Register {
	private int value;

	public Register(int value) {
		this.value = value;
	}

	public boolean setValue(int newValue) {
		this.value = newValue;

		if (this.value == newValue) return true;
		return false;
	}

	public int getValue() {
		return this.value;
	}
}