package simulizer.simulation.data.representation;

/** this class will provide information about the registers such as a mapping of register numbers to pseudonyms an vice versa
 * @author Charlie Street */
public class RegisterInfo {
	// the array of registers and their names
	//@formatter:off
	private static String[] registerNames = new String[]{
	   "$zero","$at","$v0","$v1","$a0","$a1","$a2","$a3",
	   "$t0","$t1","$t2","$t3","$t4","$t5","t6","t7",
	   "$s0","$s1","$s2","$s3","$s4","$s5","$s6","$s7",
	   "$t8","$t9","$k0","$k1","$gp","$sp","$fp","$ra"
	};
	//@formatter:on

	/** this method will return the name given to a register at a given index
	 * @param index the index of the register in the array of registers in question
	 * @return the registers name */
	public static String numberToName(int index) {
		return registerNames[index];
	}

	/** this method does the opposite of numberToName it takes a register and returns its index
	 * @param name the name of the register
	 * @return the registers associated index */
	public static int nameToNumber(String name) {
		for (int i = 0; i < registerNames.length; i++) {
			if (registerNames[i].equals(name))// if correct name found
			{
				return i;
			}
		}

		return -1; // if incorrect name entered
	}
}
