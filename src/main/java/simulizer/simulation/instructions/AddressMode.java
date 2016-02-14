package simulizer.simulation.instructions;

/**enum gives different addressing modes
 * 
 * @author Charlie Street
 *
 */
public enum AddressMode {
	RTYPE,//3 registers
	ITYPE,//2 registers and offset 
	JTYPE,//1 address
	SPECIAL,//No Operands
	LSTYPE;//a bit hacky but useful none the less
	
}
