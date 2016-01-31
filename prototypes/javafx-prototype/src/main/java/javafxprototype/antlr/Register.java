package javafxprototype.antlr;

public class Register {
	private int value = 0;
	private String id;
	
	private static Register t0 = new Register("t0");
	private static Register t1 = new Register("t1");
	private static Register t2 = new Register("t2");
	private static Register[] registers = {t0, t1, t2};
	
	private Register(String id) {
		this.id = id;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getId() {
		return id;
	}
	
	public static Register getRegister(String id) {
		switch (id) {
		case "$t0": return t0;
		case "$t1": return t1;
		case "$t2": return t2;
		default: throw new IllegalArgumentException("Unknown register");
		}
	}
	
	public static String info() {
		String svar = "";
		for (Register r : registers) {
			svar += String.format("%s = %d%n", r.getId(), r.getValue());
		}
		
		return svar;
	}
}
