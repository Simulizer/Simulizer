package simulizer.annotations;

import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.MainMemory;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection of methods for accessing information about the simulation from annotations
 *
 * @author mbway
 */
@SuppressWarnings({"unused", "WeakerAccess"})
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
		return DataConverter.decodeAsUnsigned(cpu.getRegister(r).getBytes());
	}
	public long getRegisterS(Register r) {
		return DataConverter.decodeAsSigned(cpu.getRegister(r).getBytes());
	}

	public void setRegisterU(Register r, long val) {
		Word w = new Word(DataConverter.encodeAsUnsigned(val));
        cpu.setRegister(r, w);
	}
	public void setRegisterS(Register r, long val) {
		Word w = new Word(DataConverter.encodeAsSigned(val));
		cpu.setRegister(r, w);
	}

	public List<Long> readUnsignedWordsFromMem(int startAddress, int length) throws MemoryException, HeapException, StackException {
		MainMemory mem = cpu.getMainMemory();
		List<Long> words = new ArrayList<>();

		assert (length > 0) && (length % 4 == 0);
		int end = startAddress+length;
		for(int i = startAddress; i <= end; i+=4) {
			words.add(DataConverter.decodeAsUnsigned(mem.readFromMem(i, 4)));
		}
		return words;
	}
	public String readStringFromMem(int startAddress) throws MemoryException, HeapException, StackException {
		MainMemory mem = cpu.getMainMemory();
        byte[] data = mem.readUntilNull(startAddress);
		return new String(data, StandardCharsets.UTF_8);
	}
	public byte[] readBytesFromMem(int startAddress, int length) throws MemoryException, HeapException, StackException {
		MainMemory mem = cpu.getMainMemory();
        return mem.readFromMem(startAddress, length);
	}
	public boolean[] readBoolsFromMem(int startAddress, int length) throws MemoryException, HeapException, StackException {
		MainMemory mem = cpu.getMainMemory();
		byte[] bytes = mem.readFromMem(startAddress, length);
		boolean[] bools = new boolean[bytes.length];
		for(int i = 0; i < bytes.length; ++i)
			bools[i] = bytes[i] != 0;
		return bools;
	}
	
	public byte[] getLo() {
		return cpu.getLo().getBytes();
	}
	
	public byte[] getHi() {
		return cpu.getHi().getBytes();
	}
}
