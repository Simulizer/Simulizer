package simulizer.cmd;

import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.utils.StringUtils;

import java.util.Scanner;

/**
 * like ConsoleIO but used for Cmd mode rather than tests
 */
public class CmdIO implements IO {

    private Scanner scan;
    private final boolean printDebugStream;

    /**initialises the scanner
     *
     */
    public CmdIO(boolean printDebugStream)
    {
        this.scan = new Scanner(System.in, "UTF-8");
        this.printDebugStream = printDebugStream;
    }

    /**method prints a string passed to it
     *
     * @param str the string to be printed
     */
    public void printString(IOStream stream, String str)
    {
        switch(stream) {
            case STANDARD: System.out.print(str); break;
            case ERROR: System.err.print(str); break;
            case DEBUG: if(printDebugStream) System.out.print(str); break;
        }
    }

    /**method will print the integer passed to it
     *
     * @param num the number to be printed
     */
    public void printInt(IOStream stream, int num)
    {
        printString(stream, Integer.toString(num));
    }

    /**method will print the character passed to it
     *
     * @param letter the character to be printed
     */
    public void printChar(IOStream stream, char letter)
    {
        printString(stream, Character.toString(letter));
    }

    /**method will return the string read from the console
     *
     * @return the string read from the console
     */
    public String readString(IOStream stream)
    {
        return scan.nextLine();
    }

    /**reads an int from the console
     *
     * @return the integer read from the console
     */
    public int readInt(IOStream stream)
    {
        return scan.nextInt();
    }

    /**reads a character from the console
     *
     * @return the character read from the console
     */
    public char readChar(IOStream stream)
    {
        return StringUtils.nextChar(scan);
    }

    /**closes the scanner object
     *
     */
    public void closeScanner()
    {
        scan.close();
    }

    @Override
    public void cancelRead() {
        // TODO Cancel Read
    }
}
