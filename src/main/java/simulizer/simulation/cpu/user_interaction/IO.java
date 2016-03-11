package simulizer.simulation.cpu.user_interaction;

/**interface for io operations
 * @author Charlie Street
 *
 */
public interface IO {
	String readString();
	int readInt();
	char readChar();
	void printString(IOStream stream, String str);
	void printInt(IOStream stream, int num);
	void printChar(IOStream stream, char letter);
	void cancelRead();
}
