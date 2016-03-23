package simulizer.simulation.cpu;

import simulizer.simulation.cpu.components.CPU;

/**
 * CPU Changed Listener
 * 
 * @author Michael
 *
 */
public interface CPUChangedListener {

	/**
	 * Called when the CPU has changed
	 * 
	 * @param cpu
	 *            the new CPU
	 */
	void cpuChanged(CPU cpu);
}
