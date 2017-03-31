public class AMRS {
	public AMRS(String filepath) {
		System.out.println(filepath);
	}

	public static void main(String[] args) {
		AMRS amrs = new AMRS(args[0]);
	}
}