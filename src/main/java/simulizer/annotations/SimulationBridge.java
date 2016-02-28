package simulizer.annotations;

import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;

/**
 * A collection of methods for accessing information about the simulation from annotations
 */
@SuppressWarnings("unused")
public class SimulationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	CPU cpu = null;

	public void stop() {
		cpu.stopRunning();
	}

	public void setClockSpeed(int tickMillis) {
		cpu.setClockSpeed(tickMillis);
	}

	public Word[] getRegisters() {
		return cpu.getRegisters();
	}

	public long getRegister(Register r) {
		Word[] regs = getRegisters();
		return DataConverter.decodeAsUnsigned(regs[r.getID()].getWord());
	}

}
