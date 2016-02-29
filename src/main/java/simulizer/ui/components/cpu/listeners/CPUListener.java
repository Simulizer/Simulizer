package simulizer.ui.components.cpu.listeners;

import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.listeners.DataMovementMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.simulation.listeners.StageEnterMessage;
import simulizer.ui.windows.CPUVisualisation;

public class CPUListener extends SimulationListener {

    CPUVisualisation vis;
    CPU simCpu;
    simulizer.ui.components.CPU cpu;

    public CPUListener(simulizer.ui.components.CPU cpu, CPU simCpu, CPUVisualisation vis){
        this.vis = vis;
        this.cpu = cpu;
        this.simCpu = simCpu;
    }

    public void processDataMovementMessage(DataMovementMessage m) {
        if(m.getInstruction().isPresent()){
            switch(m.getInstruction().get().getInstruction()){
                case li:
                    System.out.println("li");
                    break;
                case la:
                    System.out.println("la");
                    break;
                case syscall:
                    System.out.println("sys call");
                    break;
                default:
                    System.out.println(m.getInstruction().get().getInstruction());
                    break;
            }
        }
    }

    public void processStageEnterMessage(StageEnterMessage m) {

        switch (m.getStage()){
            case Fetch:
                cpu.controlUnit.highlight(2);
                cpu.controlUnitToIM1.animateData(simCpu.getClockSpeed());
                cpu.instructionMemory.highlight(2);
                cpu.codeMemoryToIR.animateData(simCpu.getClockSpeed());
                cpu.ir.highlight(2);
                cpu.irToPlusFour.animateData(simCpu.getClockSpeed());
                cpu.unknown.highlight(2);
                cpu.plusFourToPc.animateData(simCpu.getClockSpeed());
                cpu.programCounter.highlight(2);
                break;
            case Decode:
                System.out.println("Decode");
                break;
            case Execute:
                System.out.println("Execute");
                break;
        }


    }


}
