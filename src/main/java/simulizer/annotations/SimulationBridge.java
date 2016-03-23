package simulizer.annotations;

import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.MainMemory;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;
import simulizer.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of methods for accessing information about the simulation from annotations
 *
 * @author mbway
 */
@SuppressWarnings("unused")
public class SimulationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	CPU cpu = null;

	public void pause() { cpu.pause(); }
	public void stop() {
		cpu.stopRunning();
	}

	/**
	 * Technically setting the frequency rather than 'speed'
	 */
	public void setSpeed(double freq) {
		cpu.setCycleFreq(freq);
	}

	public Word[] getRegisters() {
		if(cpu == null)
			throw new IllegalStateException();
		return cpu.getRegisters();
	}

	public long getRegisterU(Register r) {
		Word[] regs = getRegisters();
		return DataConverter.decodeAsUnsigned(regs[r.getID()].getWord());
	}
	public long getRegisterS(Register r) {
		Word[] regs = getRegisters();
		return DataConverter.decodeAsSigned(regs[r.getID()].getWord());
	}

	public void setRegisterU(Register r, long val) {
		Word w = new Word(DataConverter.encodeAsUnsigned(val));
		getRegisters()[r.getID()] = w;
	}
	public void setRegisterS(Register r, long val) {
		Word w = new Word(DataConverter.encodeAsSigned(val));
		getRegisters()[r.getID()] = w;
	}

	public List<Long> readUnsignedWordsFromMem(int firstAddress, int lastAddress) throws MemoryException, HeapException, StackException {
		MainMemory mem = cpu.getMainMemory();
		List<Long> words = new ArrayList<>();

		assert (lastAddress > firstAddress) && ((lastAddress - firstAddress) % 4 == 0);
		for(int i = firstAddress; i <= lastAddress; i+=4) {
			words.add(DataConverter.decodeAsUnsigned(mem.readFromMem(i, 4)));
		}
		return words;
	}

}
