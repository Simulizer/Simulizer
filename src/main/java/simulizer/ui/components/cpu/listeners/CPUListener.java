package simulizer.ui.components.cpu.listeners;

import simulizer.assembler.representation.Instruction;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.messages.DataMovementMessage;
import simulizer.simulation.messages.InstructionTypeMessage;
import simulizer.simulation.messages.SimulationListener;
import simulizer.simulation.messages.StageEnterMessage;
import simulizer.ui.components.cpu.AnimationProcessor;
import simulizer.ui.windows.CPUVisualisation;

/**
 * Handles communication between the CPU simulation and visualisation
 */
public class CPUListener extends SimulationListener {

    CPUVisualisation vis;
    CPU simCpu;
    simulizer.ui.components.CPU cpu;
    Instruction currentInstruction;
    public AnimationProcessor animationProcessor;

    /**
     * Sets the visualisation, cpu simulation and the visualisation
     */
    public CPUListener(simulizer.ui.components.CPU cpu, CPU simCpu, CPUVisualisation vis, AnimationProcessor animationProcessor){
        this.vis = vis;
        this.cpu = cpu;
        this.simCpu = simCpu;
        this.animationProcessor = animationProcessor;
    }

    public CPU getSimCpu(){
        return simCpu;
    }

    /**
     * get the duration of 1/n th of a single CPU cycle (fetch+decode+execute)
     * @param oneOverFraction eg 3 => 1/3 of a cycle
     * @return the duration in ms
     */
    private int getCycleFraction(int oneOverFraction) {
        double oneCycleMs = 1000 / simCpu.getCycleFreq();
		return (int) (oneCycleMs / oneOverFraction);
    }

    /**
     * Get the duration of 1/n th of a single instruction time
     * @param nthOfInstruction eg 3 => 1/3 of an instructions running time
     * @return the duration in ms
     */
    private int getInstructionFraction(int nthOfInstruction){
        return getCycleFraction(3) / nthOfInstruction;
    }

    /**
     * Processes a data movement message and updates the current instruction
     * @param m The data message message
     */
    public void processDataMovementMessage(DataMovementMessage m) {
        if(m.getInstruction().isPresent()){
            currentInstruction = m.getInstruction().get().getInstruction();
        }
    }
    /**
     * Processes the current instruction
     * @param instruction The current instruction
     */
    public void processInstruction(Instruction instruction) {
        String instructionName = instruction.toString();
        switch(instruction) {
            case beq:
            case beqz:
            case bgtz:
            case bgez:
            case bne:
            case blez:
            case bltz:
            {
                int speed = getInstructionFraction(6);

                Runnable t1 = () -> {
                    cpu.showText("BRANCH INSTRUCTION - Step 1 - Register operands are read and 16-bit immediate is sign extended", speed);
                    cpu.ir.highlight();
                    cpu.irToRegister1.animateData(speed);
                    cpu.irToRegister2.animateData(speed);
                    cpu.irToSignExtender.animateData(speed);
                };

                Runnable t2 = () -> {
                        cpu.showText("BRANCH INSTRUCTION - Step 2 - Register operands are subtracted and 16-bit immediate is shifted left 2 bits",speed);
                        cpu.register.highlight();
                        cpu.signExtender.highlight();
                        cpu.registerToALU1.animateData(speed);
                        cpu.registerToALU2.animateData(speed);
                        cpu.signExtenderToShift.animateData(speed);
                };

                Runnable t3 = () -> {
                        cpu.showText("BRANCH INSTRUCTION - Step 3 - 4 is added to PC value, so it's ready to be added to the offset", speed);
                        cpu.pcToPlusFour.animateData(speed);
                };

                Runnable t4 = () -> {
                        cpu.showText("BRANCH INSTRUCTION - Step 4 - Next instructions address is calculated (assuming should branch)", speed);
                        cpu.shiftToAdder.animateData(speed);
                        cpu.plusFourToAdder.animateData(speed);
                };

                Runnable t5 = () -> {
                        cpu.showText("BRANCH INSTRUCTION - Step 5 - (PC + 4) and (PC + offset) are fed into the mux with the alu result, this decides the next value of the PC", speed);

                        cpu.alu.highlight();
                        cpu.adder.highlight();
                        cpu.aluToMux.animateData(speed);
                        cpu.adderToMux.animateData(speed);
                        cpu.plusFourToMux.animateData(speed);
                };

                Runnable t6 = () -> {
                        cpu.showText("BRANCH INSTRUCTION - Step 6 - Next PC value is updated", speed);
                        cpu.muxAdder.highlight();
                        cpu.muxToPC.animateData(speed);
                };

                cpu.animationProcessor.scheduleRegularAnimations(instructionName, speed, t1, t2, t3, t4, t5, t6);
            }   break;
            case sw:
            {
                int speed = getInstructionFraction(3);

                Runnable t1 = () -> {
                        cpu.showText("SW INSTRUCTION - Step 1 - ReadReg and WriteReg are selected and 16-bit immediate is sign extended", speed);
                        cpu.ir.highlight();
                        cpu.irToRegister1.animateData(speed);
                        cpu.irToRegister3.animateData(speed);
                        cpu.irToSignExtender.animateData(speed);
                };

                Runnable t2 = () -> {
                        cpu.showText("SW INSTRUCTION - Step 2 - ALU computes sum of base address and sum extended offset", speed);
                        cpu.register.highlight();
                        cpu.signExtender.highlight();
                        cpu.registerToALU1.animateData(speed);
                        cpu.signExtenderToALU.animateData(speed);
                };

                Runnable t3 = () -> {
                        cpu.showText("SW INSTRUCTION - Step 3 - Word is written to memory", speed);
                        cpu.alu.highlight();
                        cpu.aluToMemory.animateData(speed);
                };

                cpu.animationProcessor.scheduleRegularAnimations(instructionName, speed, t1, t2, t3);
            }   break;
            case j:
            {
                int speed = getInstructionFraction(4);

                Runnable t1 = () -> {
                        cpu.showText("JUMP INSTRUCTION - Step 1 - 26 bit immediate is shifted left 2 bits", speed);
                        cpu.ir.highlight();
                        cpu.irToShift.animateData(speed);
                };

                Runnable t2 = () -> {
                        cpu.showText("JUMP INSTRUCTION - Step 2 - PC + 4 is calculated", speed);
                        cpu.programCounter.highlight();
                        cpu.pcToPlusFour.animateData(speed);
                };

                Runnable t3 = () -> {
                        cpu.showText("JUMP INSTRUCTION - Step 3 - PC + 4 is combined with the 28 bit immediate", speed);
                        cpu.plusFour.highlight();
                        cpu.shiftLeftIR.highlight();
                        cpu.plusFourToMuxWithShift.animateData(speed);
                        cpu.shiftToMux.animateData(speed);
                };

                Runnable t4 = () -> {
                        cpu.showText("JUMP INSTRUCTION - Step 4 - PC value is updated to (PC + 4 + immediate)", speed);
                        cpu.muxAdder.highlight();
                        cpu.muxToPC.animateData(speed);
                };

                cpu.animationProcessor.scheduleRegularAnimations(instructionName, speed, t1, t2, t3, t4);
            }   break;
            case jal:
            {
                int speed = getInstructionFraction(5);

                Runnable t1 = () -> {
                        cpu.showText("JAL INSTRUCTION - Step 1 - 26 bit immediate is shifted left 2 bits", speed);
                        cpu.ir.highlight();
                        cpu.irToShift.animateData(speed);
                };

                Runnable t2 = () -> {
                        cpu.showText("JAL INSTRUCTION - Step 2 - PC + 4 is calculated", speed);
                        cpu.programCounter.highlight();
                        cpu.pcToPlusFour.animateData(speed);
                };

                Runnable t3 = () -> {
                        cpu.showText("JAL INSTRUCTION - Step 3 - PC + 4 is combined with the 28 bit immediate", speed);
                        cpu.plusFour.highlight();
                        cpu.shiftLeftIR.highlight();
                        cpu.plusFourToMuxWithShift.animateData(speed);
                        cpu.shiftToMux.animateData(speed);
                };

                Runnable t4 = () -> {
                        cpu.showText("JAL INSTRUCTION - Step 4 - PC value is updated to (PC + 4 + immediate)", speed);
                        cpu.muxAdder.highlight();
                        cpu.muxToPC.animateData(speed);
                };


                Runnable t5 = () -> {
                        cpu.showText("JAL INSTRUCTION - Step 5 - $ra register is updated", speed);
                        cpu.ir.highlight();
                        cpu.irToRegister3.animateData(speed);
                };

                cpu.animationProcessor.scheduleRegularAnimations(instructionName, speed, t1, t2, t3, t4, t5);
            }   break;
            case jr:
            {
                int speed = getInstructionFraction(3);

                Runnable t1 = () -> {
                        cpu.showText("JR INSTRUCTION - Step 1 - Register value is read", speed);
                        cpu.ir.highlight();
                        cpu.irToRegister1.animateData(speed);
                };

                Runnable t2 = () -> {
                        cpu.showText("JR INSTRUCTION - Step 2 - Register value is passed to mux", speed);
                        cpu.register.highlight();
                        cpu.registertoMux.animateData(speed);
                };

                Runnable t3 = () -> {
                        cpu.showText("JR INSTRUCTION - Step 3 - PC value is updated to register address", speed);
                        cpu.muxAdder.highlight();
                        cpu.muxToPC.animateData(speed);
                };

                cpu.animationProcessor.scheduleRegularAnimations(instructionName, speed, t1, t2, t3);
            }   break;
            case li:
            case la:
            {
                int speed = getInstructionFraction(1);

                Runnable t1 = () -> {
                        cpu.showText("LI/LA INSTRUCTION - Step 1 - Register is written to with the new value", speed);
                        cpu.ir.highlight();
                        cpu.irToRegister3.animateData(speed);
                };

                cpu.animationProcessor.scheduleRegularAnimations(instructionName, speed, t1);
            }   break;
            case add:
            case addu:
            case and:
            case div:
            case divu:
            case mul:
            case nor:
            case xor:
            case or:
            case slt:
            case sltu:
            case sll:
            case srl:
            case sra:
            case sub:
            case subu:
            {
                processRType(instructionName);
                break;
            }
            case addi:
            case addiu:
            case andi:
            case lbu:
            case lhu:
            case lui:
            case ori:
            case sb:
            case sh:
            case slti:
            case sltiu:
            case lw:
            {
                processIType(instructionName);
                break;
            }

        }
    }

    /**
     * Processes an I-TYPE instruction
     */
    public void processIType(String instruction){
        int speed = getInstructionFraction(4);

        Runnable t1 = () -> {
			cpu.showText(instruction + " I-TYPE INSTRUCTION - Step 1 - ReadReg and WriteReg are selected and 16-bit immediate is sign extended", speed);
			cpu.ir.highlight();
			cpu.irToRegister1.animateData(speed);
			cpu.irToRegister3.animateData(speed);
			cpu.irToSignExtender.animateData(speed);
        };

        Runnable t2 = () -> {
			cpu.showText(instruction + " I-TYPE INSTRUCTION - Step 2 - ALU computes sum of base address and sum extended offset", speed);
			cpu.register.highlight();
			cpu.signExtender.highlight();
			cpu.registerToALU1.animateData(speed);
			cpu.signExtenderToALU.animateData(speed);
        };

        Runnable t3 = () -> {
			cpu.showText(instruction +  "I-TYPE INSTRUCTION - Step 3 - Result is sent to memory", speed);
			cpu.alu.highlight();
			cpu.aluToMemory.animateData(speed);
        };

        Runnable t4 = () -> {
			cpu.showText(instruction + " I-TYPE INSTRUCTION - Step 4 - Word is read from memory and written to a register", speed);
			cpu.mainMemory.highlight();
			cpu.dataMemoryToRegisters.animateData(speed);
        };

        cpu.animationProcessor.scheduleRegularAnimations(instruction, speed, t1, t2, t3, t4);
    }

    /**
     * Processess an R-TYPE instruction
     */
    public void processRType(String instruction){
        int speed = getInstructionFraction(3);

        Runnable t1 = () -> {
			cpu.showText(instruction + " R-TYPE INSTRUCTION - Step 1 - Both read registers and one write register are selected", speed);
			cpu.ir.highlight();
			cpu.irToRegister1.animateData(speed);
			cpu.irToRegister2.animateData(speed);
			cpu.irToRegister3.animateData(speed);
        };

        Runnable t2 = () -> {
			cpu.showText(instruction + " R-TYPE INSTRUCTION - Step 2 - Two registers are added together via the ALU", speed);
			cpu.register.highlight();
			cpu.registerToALU1.animateData(speed);
			cpu.registerToALU2.animateData(speed);
        };

        Runnable t3 = () -> {
            cpu.showText(instruction + " R-TYPE INSTRUCTION - Step 3 - Result is written back to the register", speed);
            cpu.alu.highlight();
            cpu.aluToRegisters.animateData(speed);
        };

        cpu.animationProcessor.scheduleRegularAnimations(instruction, speed, t1, t2, t3);

    }

    public void startOfCycle(){
        cpu.animationProcessor.newCycle();
    }

    /**
     * Handles certain stages e.g the fetch, decode or execute stage
     * @param m The stage enter message
     */
    public void processStageEnterMessage(StageEnterMessage m) {
        switch (m.getStage()){
            case Fetch:
            {
                startOfCycle();
                int speed = getInstructionFraction(5);

                Runnable t1 = () -> {
                    cpu.showText("INSTRUCTION FETCH - Step 1 - The PC value (address of next instruction) is sent to main memory to read the next instruction", speed);
                    cpu.programCounter.highlight();
                    cpu.PCToIM.animateData(speed);
                };

                Runnable t2 = () -> {
                    cpu.showText("INSTRUCTION FETCH - Step 2 - The next instruction is passed from memory to the instruction register", speed);
                    cpu.instructionMemory.highlight();
                    cpu.codeMemoryToIR.animateData(speed);
                };

                Runnable t3 = () -> {
                    cpu.showText("INSTRUCTION FETCH - Step 3 - PC is updated to the next instruction address", speed * 3);
                    cpu.programCounter.highlight();
                    cpu.pcToPlusFour.animateData(speed);
                };

                Runnable t4 = () -> {
                    cpu.plusFour.highlight();
                    cpu.plusFourToMux.animateData(speed);
                };

                Runnable t5 = () -> {
                    cpu.muxAdder.highlight();
                    cpu.muxToPC.animateData(speed);
                };

                cpu.animationProcessor.scheduleRegularAnimations(speed, t1, t2, t3, t4, t5);

                break;
            }
            case Decode:
            {
                int speed = getInstructionFraction(1);
                Runnable t1 = () -> {
                    cpu.showText("INSTRUCTION DECODE - The encoded instruction present in the IR (instruction register) is interpreted by the decoder", speed);
                    cpu.ir.highlight();
                };

                cpu.animationProcessor.scheduleRegularAnimations(speed, t1);
                break;
            }
            case Execute:
            {
                processInstruction(currentInstruction);
                break;
            }
        }
    }

}