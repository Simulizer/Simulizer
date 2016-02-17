package simulizer.simulation.cpu.user_interaction;

/**interface for io operations
 * @author Charlie Street
 *
 */
public interface IO {
	public String readString();
	public int readInt();
	public char readChar();
	public void printString(String str);
	public void printInt(int num);
	public void printChar(char letter);
}
