public class Register {
	private int value;

	public Register() {
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