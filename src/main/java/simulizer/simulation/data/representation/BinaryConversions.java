package simulizer.simulation.data.representation;

/**this class is just an auxillary class to store useful conversions to and from my binary representation
 * 
 * @author Charlie Street
 *
 */
public class BinaryConversions
{
	/**this method takes a binary string and converts it to type long
	 * this should not be used for negative numbers, that will only be for arithmetic which will be used in the ALU
	 * @param binString a POSITIVE binary string
	 * @return the long value of said string
	 */
	public static long getLongValue(String binString)
	{
		assert(binString.length()==32);//for testing
		
		long binPower = 1;
		long result=0;
		
		for(int i = binString.length()-1; i>=0; i--)
		{
			if(binString.charAt(i)=='1')
			{
				result += binPower;
			}
			binPower *= 2;//all powers of 2
		}
		
		return result;
	}
	
	/**this method will take a POSITIVE long and convert it into a binary string
	 * it will not take values greater then 2^32 - 1 for now
	 * @param value the long value to convert
	 * @return the binary representation of said number
	 */
	public static String getBinaryString(long value)
	{
		String val = Long.toBinaryString(value);
		while(val.length() != 32)
		{
			val = "0" + val;
		}
		return val;
	}
}
