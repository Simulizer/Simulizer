package simulizer.ui.components.cpu.listeners;

import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.windows.CPUVisualisation;

public class CPUChangedListener implements simulizer.simulation.cpu.CPUChangedListener {

    CPUVisualisation vis;
    public CPUChangedListener(CPUVisualisation vis){
        this.vis = vis;
    }

    public void cpuChanged(CPU cpu){
        vis.attachCPU(cpu);
    }

}
