package simulizer.ui.components.cpu.listeners;

import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.windows.CPUVisualisation;

/**
 * Used to receive messages when the CPU has been changed (e.g to pipelined)
 * @author Theo Styles
 */
public class CPUChangedListener implements simulizer.simulation.cpu.CPUChangedListener {

    private CPUVisualisation vis;

    /**
     * Sets the CPU visualisation
     * @param vis The CPU visualisation
     */
    public CPUChangedListener(CPUVisualisation vis){
        this.vis = vis;
    }

    /**
     * Called when the cpu is changed
     * @param cpu The new cpu
     */
    public void cpuChanged(CPU cpu){
        vis.attachCPU(cpu);
    }

}
