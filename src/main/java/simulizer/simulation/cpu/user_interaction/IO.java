package simulizer.simulation.cpu.user_interaction;

/**interface for io operations
 * @author Charlie Street
 *
 */
public interface IO {
	String readString();
	int readInt();
	char readChar();
	void printString(String str);
	void printInt(int num);
	void printChar(char letter);
}
