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
	public static long getUnsignedLongValue(String binString)
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
	
	/**method will get the signed value using twos complement of the string given
	 * in the case the first character is 0, normal unsigned conversion can be done
	 * @param binString the string to be converted
	 * @return the long value of the twos complement binary value
	 */
	public static long getSignedLongValue(String binString)
	{
		if(binString.charAt(0)=='1')//if negative
		{
			
			return getUnsignedLongValue(switchSigns(binString)) * -1;
		}
		else//positive number
		{
			return getUnsignedLongValue(binString);
		}
	}
	
	/**method takes a binary string and changes it's sign using two's complement
	 * 
	 * @param binString the string to switch
	 * @return the string with the sign switched
	 */
	private static String switchSigns(String binString)
	{
		boolean firstOne = false;
		String positive = "";
		for(int i = binString.length()-1; i>=0; i--)
		{
			if(!firstOne)//if first one not been met yet
			{
				if(binString.charAt(i)=='1')
				{
					firstOne = true;
				}
				positive = binString.charAt(i) + positive;
			}
			else
			{
				if(binString.charAt(i)=='1')
				{
					positive = '0' + positive;
				}
				else
				{
					positive = '1' + positive;
				}
			}
		}
		
		return positive;
	}
	
	/**method converts a long integer into a twos complement binary string
	 * 
	 * @param value the value to convert
	 * @return the long in the binary string form
	 */
	public static String getSignedBinaryString(long value)
	{
		String result;
		if(value<0)
		{
			result = getUnsignedBinaryString(value*-1);//getting positive value first
			result = switchSigns(result);
		}
		else
		{
			result = getUnsignedBinaryString(value);
		}
		
		return result;
	}
	
	/**this method will take a POSITIVE long and convert it into a binary string
	 * it will not take values greater then 2^32 - 1 for now
	 * @param value the long value to convert
	 * @return the binary representation of said number
	 */
	public static String getUnsignedBinaryString(long value)
	{
		assert(value>=0);//FOR TESTING
		String val = Long.toBinaryString(value);
		while(val.length() != 32)
		{
			val = "0" + val;
		}
		return val;
	}
}
