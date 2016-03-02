package simulizer.ui.components.cpu.listeners;

import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.listeners.DataMovementMessage;
import simulizer.simulation.listeners.InstructionTypeMessage;
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
                case beq:
                case beqz:
                case bgtz:
                case bgez:
                case bne:
                case blez:
                case bltz:
                    int speed = simCpu.getClockSpeed() / 3 / 5;

                    cpu.ir.highlight();
                    cpu.irToRegister1.animateData(speed);
                    cpu.irToRegister2.animateData(speed);
                    cpu.irToSignExtender.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.register.highlight();
                    cpu.signExtender.highlight();
                    cpu.registerToALU1.animateData(speed);
                    cpu.registerToALU2.animateData(speed);
                    cpu.signExtenderToShift.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.shiftToAdder.animateData(speed);
                    cpu.plusFourToAdder.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.alu.highlight();
                    cpu.adder.highlight();
                    cpu.aluToMux.animateData(speed);
                    cpu.adderToMux.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.muxAdder.highlight();
                    cpu.muxToPC.animateData(speed);
                    sleepIfNotPipelined(speed);
                    break;
                case sw:
                    processIType();
                    break;
                case lw:
                    speed = simCpu.getClockSpeed() / 3 / 4;

                    cpu.ir.highlight();
                    cpu.irToRegister1.animateData(speed);
                    cpu.irToRegister3.animateData(speed);
                    cpu.irToSignExtender.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.register.highlight();
                    cpu.signExtender.highlight();
                    cpu.registerToALU1.animateData(speed);
                    cpu.signExtenderToALU.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.alu.highlight();
                    cpu.aluToMemory.animateData(speed);
                    sleepIfNotPipelined(speed);
                    break;
                case j:
                case jal:
                case jr:
                    speed = simCpu.getClockSpeed() / 3 / 3;
                    cpu.ir.highlight();
                    cpu.irToShift.animateData(speed);
                    cpu.programCounter.highlight();
                    cpu.pcToPlusFour.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.plusFour.highlight();
                    cpu.shiftLeftIR.highlight();
                    cpu.plusFourToMuxWithShift.animateData(speed);
                    cpu.shiftToMux.animateData(speed);
                    sleepIfNotPipelined(speed);

                    cpu.muxAdder.highlight();
                    cpu.muxToPC.animateData(speed);
                    sleepIfNotPipelined(speed);
                    break;
                case li:
                    speed = simCpu.getClockSpeed() / 3;
                    cpu.ir.highlight();
                    cpu.irToRegister3.animateData(speed);
                    sleepIfNotPipelined(speed);
                default:
                    System.out.println(m.getInstruction().get().getInstruction());
                    break;
            }
        }
    }

    public void processInstructionTypeMessage(InstructionTypeMessage m){
        switch(m.getMode()){
            case RTYPE:
                processRType();
                break;
            case ITYPE:
                processIType();
                break;
        }
    }

    public void processIType(){
        int speed = simCpu.getClockSpeed() / 3 / 4;

        cpu.ir.highlight();
        cpu.irToRegister1.animateData(speed);
        cpu.irToRegister3.animateData(speed);
        cpu.irToSignExtender.animateData(speed);
        sleepIfNotPipelined(speed);

        cpu.register.highlight();
        cpu.signExtender.highlight();
        cpu.registerToALU1.animateData(speed);
        cpu.signExtenderToALU.animateData(speed);
        sleepIfNotPipelined(speed);

        cpu.alu.highlight();
        cpu.aluToMemory.animateData(speed);
        sleepIfNotPipelined(speed);

        cpu.mainMemory.highlight();
        cpu.dataMemoryToRegisters.animateData(speed);
        sleepIfNotPipelined(speed);
    }

    public void processRType(){
        int speed = simCpu.getClockSpeed() / 3 / 3;
        cpu.ir.highlight();
        cpu.irToRegister1.animateData(speed);
        cpu.irToRegister2.animateData(speed);
        sleepIfNotPipelined(speed);

        cpu.register.highlight();
        cpu.registerToALU1.animateData(speed);
        cpu.registerToALU2.animateData(speed);
        sleepIfNotPipelined(speed);

        cpu.alu.highlight();
        cpu.aluToRegisters.animateData(speed);
        sleepIfNotPipelined(speed);
    }

    public void processStageEnterMessage(StageEnterMessage m) {
        switch (m.getStage()){
            case Fetch:
                int speed = simCpu.getClockSpeed() / 3 / 6;
                cpu.programCounter.highlight();
                cpu.PCToIM.animateData(speed);
                sleepIfNotPipelined(speed);

                cpu.instructionMemory.highlight();
                cpu.codeMemoryToIR.animateData(speed);
                sleepIfNotPipelined(speed);

                cpu.ir.highlight();
                cpu.irToPlusFour.animateData(speed);
                sleepIfNotPipelined(speed);

                cpu.programCounter.highlight();
                cpu.pcToPlusFour.animateData(speed);
                sleepIfNotPipelined(speed);

                cpu.plusFour.highlight();
                cpu.plusFourToMux.animateData(speed);
                sleepIfNotPipelined(speed);

                cpu.muxAdder.highlight();
                cpu.muxToPC.animateData(speed);
                sleepIfNotPipelined(speed);
                break;
        }
    }

    private void sleepIfNotPipelined(long millis){
        try{
            Thread.sleep(millis);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }


}
