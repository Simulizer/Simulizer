package simulizer.ui.components.cpu.listeners;

import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.listeners.DataMovementMessage;
import simulizer.simulation.listeners.InstructionTypeMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.simulation.listeners.StageEnterMessage;
import simulizer.ui.windows.CPUVisualisation;
import simulizer.utils.UIUtils;

/**
 * Handles communication between the CPU simulation and visualisation
 */
public class CPUListener extends SimulationListener {

    CPUVisualisation vis;
    CPU simCpu;
    simulizer.ui.components.CPU cpu;

    /**
     * Sets the visualisation, cpu simulation and the visualisation
     * @param cpu
     * @param simCpu
     * @param vis
     */
    public CPUListener(simulizer.ui.components.CPU cpu, CPU simCpu, CPUVisualisation vis){
        this.vis = vis;
        this.cpu = cpu;
        this.simCpu = simCpu;
    }

    /**
     * Processes the message when a certain instruction is being executed
     * @param m The data movement message
     */
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
                    int speed = simCpu.getClockSpeed() / 6;

                    cpu.showText("BRANCH INSTRUCTION - Step 1 - Register operands are read and 16-bit immediate is sign extended", speed);

                    cpu.ir.highlight();
                    cpu.irToRegister1.animateData(speed);
                    cpu.irToRegister2.animateData(speed);
                    cpu.irToSignExtender.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("BRANCH INSTRUCTION - Step 2 - Register operands are subtracted and 16-bit immediate is shifted left 2 bits", speed);

                    cpu.register.highlight();
                    cpu.signExtender.highlight();
                    cpu.registerToALU1.animateData(speed);
                    cpu.registerToALU2.animateData(speed);
                    cpu.signExtenderToShift.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("BRANCH INSTRUCTION - Step 3 - 4 is added to PC value, so it's ready to be added to the offset", speed);

                    cpu.pcToPlusFour.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("BRANCH INSTRUCTION - Step 4 - Next instructions address is calculated (assuming should branch)", speed);

                    cpu.shiftToAdder.animateData(speed);
                    cpu.plusFourToAdder.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("BRANCH INSTRUCTION - Step 5 - (PC + 4) and (PC + offset) are fed into the mux with the alu result, this decides the next value of the PC", speed);

                    cpu.alu.highlight();
                    cpu.adder.highlight();
                    cpu.aluToMux.animateData(speed);
                    cpu.adderToMux.animateData(speed);
                    cpu.plusFourToMux.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("BRANCH INSTRUCTION - Step 6 - Next PC value is updated", speed);

                    cpu.muxAdder.highlight();
                    cpu.muxToPC.animateData(speed);
                    sleepFor(speed);
                    break;
                case lw:
                    processIType();
                    break;
                case sw:
                    speed = simCpu.getClockSpeed() / 6;

                    cpu.showText("SW INSTRUCTION - Step 1 - ReadReg and WriteReg are selected and 16-bit immediate is sign extended", speed);

                    cpu.ir.highlight();
                    cpu.irToRegister1.animateData(speed);
                    cpu.irToRegister3.animateData(speed);
                    cpu.irToSignExtender.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("SW INSTRUCTION - Step 2 - ALU computes sum of base address and sum extended offset", speed);

                    cpu.register.highlight();
                    cpu.signExtender.highlight();
                    cpu.registerToALU1.animateData(speed);
                    cpu.signExtenderToALU.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("SW INSTRUCTION - Step 3 - Word is written to memory", speed);

                    cpu.alu.highlight();
                    cpu.aluToMemory.animateData(speed);
                    sleepFor(speed);

                    incrementPC(speed);

                    break;
                case j:
                    speed = simCpu.getClockSpeed() / 4;

                    cpu.showText("JUMP INSTRUCTION - Step 1 - 26 bit immediate is shifted left 2 bits", speed);

                    cpu.ir.highlight();
                    cpu.irToShift.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JUMP INSTRUCTION - Step 2 - PC + 4 is calculated", speed);

                    cpu.programCounter.highlight();
                    cpu.pcToPlusFour.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JUMP INSTRUCTION - Step 3 - PC + 4 is combined with the 28 bit immediate", speed);

                    cpu.plusFour.highlight();
                    cpu.shiftLeftIR.highlight();
                    cpu.plusFourToMuxWithShift.animateData(speed);
                    cpu.shiftToMux.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JUMP INSTRUCTION - Step 4 - PC value is updated to (PC + 4 + immediate)", speed);

                    cpu.muxAdder.highlight();
                    cpu.muxToPC.animateData(speed);
                    sleepFor(speed);
                    break;
                case jal:
                    speed = simCpu.getClockSpeed() / 5;

                    cpu.showText("JAL INSTRUCTION - Step 1 - 26 bit immediate is shifted left 2 bits", speed);

                    cpu.ir.highlight();
                    cpu.irToShift.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JAL INSTRUCTION - Step 2 - PC + 4 is calculated", speed);

                    cpu.programCounter.highlight();
                    cpu.pcToPlusFour.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JAL INSTRUCTION - Step 3 - PC + 4 is combined with the 28 bit immediate", speed);

                    cpu.plusFour.highlight();
                    cpu.shiftLeftIR.highlight();
                    cpu.plusFourToMuxWithShift.animateData(speed);
                    cpu.shiftToMux.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JAL INSTRUCTION - Step 4 - PC value is updated to (PC + 4 + immediate)", speed);

                    cpu.muxAdder.highlight();
                    cpu.muxToPC.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JAL INSTRUCTION - Step 5 - $ra register is updated", speed);
                    cpu.ir.highlight();
                    cpu.irToRegister3.animateData(speed);
                    sleepFor(speed);
                    break;
                case jr:
                    speed = simCpu.getClockSpeed() / 3;

                    cpu.showText("JR INSTRUCTION - Step 1 - Register value is read", speed);

                    cpu.ir.highlight();
                    cpu.irToRegister1.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JR INSTRUCTION - Step 2 - Register value is passed to mux", speed);

                    cpu.register.highlight();
                    cpu.registertoMux.animateData(speed);
                    sleepFor(speed);

                    cpu.showText("JR INSTRUCTION - Step 3 - PC value is updated to register address", speed);

                    cpu.muxAdder.highlight();
                    cpu.muxToPC.animateData(speed);
                    sleepFor(speed);
                    break;
                case li:
                case la:

                    speed = simCpu.getClockSpeed() / 4;

                    cpu.showText("LI/LA INSTRUCTION - Step 1 - Register is written to with the new value", speed);
                    cpu.ir.highlight();
                    cpu.irToRegister3.animateData(speed);
                    sleepFor(speed);

                    incrementPC(speed);
                    break;
            }
        }
    }

    /**
     * Handles different types of instructions, e.g R-TYPE and I-TYPE
     * @param m The instruction type message
     */
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

    /**
     * Processes an I-TYPE instruction
     */
    public void processIType(){
        int speed = simCpu.getClockSpeed() / 7;

        cpu.showText("I-TYPE INSTRUCTION - Step 1 - ReadReg and WriteReg are selected and 16-bit immediate is sign extended", speed);

        cpu.ir.highlight();
        cpu.irToRegister1.animateData(speed);
        cpu.irToRegister3.animateData(speed);
        cpu.irToSignExtender.animateData(speed);
        sleepFor(speed);

        cpu.showText("I-TYPE INSTRUCTION - Step 2 - ALU computes sum of base address and sum extended offset", speed);

        cpu.register.highlight();
        cpu.signExtender.highlight();
        cpu.registerToALU1.animateData(speed);
        cpu.signExtenderToALU.animateData(speed);
        sleepFor(speed);

        cpu.showText("I-TYPE INSTRUCTION - Step 3 - Result is sent to memory", speed);

        cpu.alu.highlight();
        cpu.aluToMemory.animateData(speed);
        sleepFor(speed);

        cpu.showText("I-TYPE INSTRUCTION - Step 4 - Word is read from memory and written to a register", speed);

        cpu.mainMemory.highlight();
        cpu.dataMemoryToRegisters.animateData(speed);
        sleepFor(speed);

        incrementPC(speed);
    }

    /**
     * Processess an R-TYPE instruction
     */
    public void processRType(){
        int speed = simCpu.getClockSpeed() / 6;

        cpu.showText("R-TYPE INSTRUCTION - Step 1 - Both read registers and one write register are selected", speed);

        cpu.ir.highlight();
        cpu.irToRegister1.animateData(speed);
        cpu.irToRegister2.animateData(speed);
        cpu.irToRegister3.animateData(speed);
        sleepFor(speed);

        cpu.showText("R-TYPE INSTRUCTION - Step 2 - Two registers are added together via the ALU", speed);

        cpu.register.highlight();
        cpu.registerToALU1.animateData(speed);
        cpu.registerToALU2.animateData(speed);
        sleepFor(speed);

        cpu.showText("R-TYPE INSTRUCTION - Step 3 - Result is written back to the register", speed);

        cpu.alu.highlight();
        cpu.aluToRegisters.animateData(speed);
        sleepFor(speed);

        incrementPC(speed);
    }

    /**
     * Handles certain stages e.g the fetch, decode or execute stage
     * @param m The stage enter message
     */
    public void processStageEnterMessage(StageEnterMessage m) {
        switch (m.getStage()){
            case Fetch:
                int speed = simCpu.getClockSpeed() / 2;

                cpu.showText("INSTRUCTION FETCH - Step 1 - The next instruction address is sent to memory", speed);
                cpu.programCounter.highlight();
                cpu.PCToIM.animateData(speed);
                sleepFor(speed);

                cpu.showText("INSTRUCTION FETCH - Step 2 - The next instruction is passed from memory to the instruction register", speed);

                cpu.instructionMemory.highlight();
                cpu.codeMemoryToIR.animateData(speed);
                sleepFor(speed);

                break;
        }
    }

    /**
     * Increments the program counter, used on most instructions at the end
     * @param speed The speed of the entire animation
     */
    private void incrementPC(int speed){
        cpu.showText("END OF EXECUTION - PC is updated to the next instruction address", speed * 3);

        cpu.programCounter.highlight();
        cpu.pcToPlusFour.animateData(speed);
        sleepFor(speed);

        cpu.plusFour.highlight();
        cpu.plusFourToMux.animateData(speed);
        sleepFor(speed);

        cpu.muxAdder.highlight();
        cpu.muxToPC.animateData(speed);
        sleepFor(speed);
    }

    /**
     * Sleep for a certain time
     * @param millis The time in milliseconds to sleep for
     */
    private void sleepFor(long millis){
        try{
            Thread.sleep(millis);
        } catch (InterruptedException e){
            UIUtils.showExceptionDialog(e);
        }
    }

}