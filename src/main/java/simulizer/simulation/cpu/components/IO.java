package simulizer.simulation.cpu.components;

import java.util.Scanner;

/**this class is used for all basic I/O functions required by syscall
 * in the simulation
 * @author Charlie Street
 *
 */
public class IO 
{
	/**method prints a string passed to it
	 * 
	 * @param str the string to be printed
	 */
	public static void printString(String str)
	{
		System.out.println(str);
	}
	
	/**method will print the integer passed to it
	 * 
	 * @param num the number to be printed
	 */
	public static void printInt(int num)
	{
		System.out.println(num);
	}
	
	/**method will print the character passed to it
	 * 
	 * @param letter the character to be printed
	 */
	public static void printChar(char letter)
	{
		System.out.println(letter);
	}
	
	/**method will return the string read from the console
	 * 
	 * @return the string read from the console
	 */
	public static String readString()
	{
		Scanner readStr = new Scanner(System.in);
		String read = readStr.nextLine();
		readStr.close();
		return read;
	}
	
	/**reads an int from the console
	 * 
	 * @return the integer read from the console
	 */
	public static int readInt()
	{
		Scanner readInt = new Scanner(System.in);
		int num = readInt.nextInt();
		readInt.close();
		return num;
	}
	
	/**reads a character from the console
	 * 
	 * @return the character read from the console
	 */
	public static char readChar()
	{
		Scanner readChar = new Scanner(System.in);
		char letter = readChar.next().charAt(0);
		readChar.close();
		return letter;
	}
}