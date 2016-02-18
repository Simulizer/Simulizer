package simulizer.simulation.cpu.user_interaction;

import java.util.Scanner;

/**this class is used for all basic I/O functions required by syscall
 * in the simulation (using the console)
 * @author Charlie Street
 *
 */
public class IOConsole implements IO
{
	private Scanner scan;
	
	/**initialises the scanner
	 * 
	 */
	public IOConsole()
	{
		this.scan = new Scanner(System.in);
	}
	
	/**method prints a string passed to it
	 * 
	 * @param str the string to be printed
	 */
	public void printString(String str)
	{
		System.out.println(str);
	}
	
	/**method will print the integer passed to it
	 * 
	 * @param num the number to be printed
	 */
	public void printInt(int num)
	{
		System.out.println(num);
	}
	
	/**method will print the character passed to it
	 * 
	 * @param letter the character to be printed
	 */
	public void printChar(char letter)
	{
		System.out.println(letter);
	}
	
	/**method will return the string read from the console
	 * 
	 * @return the string read from the console
	 */
	public String readString()
	{
		
		String read = scan.nextLine();
		return read;
	}
	
	/**reads an int from the console
	 * 
	 * @return the integer read from the console
	 */
	public int readInt()
	{
		int num = scan.nextInt();
		return num;
	}
	
	/**reads a character from the console
	 * 
	 * @return the character read from the console
	 */
	public char readChar()
	{
		char letter = scan.next().charAt(0);
		return letter;
	}
	
	/**closes the scanner object
	 * 
	 */
	public void closeScanner()
	{
		scan.close();
	}
}