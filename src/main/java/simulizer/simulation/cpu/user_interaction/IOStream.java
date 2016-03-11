package simulizer.simulation.cpu.user_interaction;

public enum IOStream {
	STANDARD(0, "Standard"), ERROR(1, "Error"), DEBUG(2, "Debug");

	private int id;
	private String name;

	private IOStream(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
