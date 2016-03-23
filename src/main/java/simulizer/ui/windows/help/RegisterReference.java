package simulizer.ui.windows.help;

import simulizer.assembler.representation.Register;

/**
 * Register Reference
 * 
 * @author Michael
 *
 */
public class RegisterReference extends SimpleTablePairWindow {
	//@formatter:off
	public RegisterReference() {
		super("Register", "Purpose");
		String[][] data = new String[Register.values().length][];
		for(int i = 0; i < Register.values().length; i++){
			Register register = Register.values()[i];
			data[i] = new String[]{register.name(), register.getDescription()};
		}
		setData(data);
	}
	//@formatter:on
}
