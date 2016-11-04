package simulizer.ui.windows.help;

import simulizer.assembler.representation.Instruction;

/**
 * Instruction Reference
 * 
 * @author Michael
 *
 */
@SuppressWarnings("unused")
public class InstructionReference extends SimpleTablePairWindow {
	//@formatter:off
	public InstructionReference() {
		super("Instruction", "Action", 0.2);
		String[][] data = new String[Instruction.values().length][];
		for(int i = 0; i < Instruction.values().length; i++){
			Instruction instruction = Instruction.values()[i];
			data[i] = new String[]{instruction.name(), instruction.getPurpose()};
		}
		setData(data);
	}
	//@formatter:on
}
