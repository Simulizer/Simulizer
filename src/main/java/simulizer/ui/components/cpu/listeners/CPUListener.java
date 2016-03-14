package simulizer.ui.components.cpu.listeners;

import javafx.concurrent.Task;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.messages.*;
import simulizer.ui.components.cpu.AnimationProcessor;
import simulizer.ui.windows.CPUVisualisation;
import simulizer.utils.UIUtils;

/**
 * Handles communication between the CPU simulation and visualisation
 */
public class CPUListener extends SimulationListener {

    CPUVisualisation vis;
    CPU simCpu;
    simulizer.ui.components.CPU cpu;
    boolean replaying;
    public AnimationProcessor animationProcessor;

    /**
     * Sets the visualisation, cpu simulation and the visualisation
     */
    public CPUListener(simulizer.ui.components.CPU cpu, CPU simCpu, CPUVisualisation vis, AnimationProcessor animationProcessor){
        this.vis = vis;
        this.cpu = cpu;
        this.simCpu = simCpu;
        this.animationProcessor = animationProcessor;
        cpu.previousInstructions.attachListener(this);
    }

    public CPU getSimCpu(){
        return simCpu;
    }

    public void replayInstruction(Message m){
        replaying = true;
        if (m instanceof DataMovementMessage){
            processDataMovementMessage(((DataMovementMessage) m));
        } else if(m instanceof InstructionTypeMessage){
            processInstructionTypeMessage(((InstructionTypeMessage) m));
        } else if(m instanceof StageEnterMessage){
            processStageEnterMessage(((StageEnterMessage) m));
        }
        replaying = false;
    }

    /**
     * get the duration of 1/n th of a single CPU cycle (fetch+decode+execute)
     * @param oneOverFraction eg 3 => 1/3 of a cycle
     * @return the duration in ms
     */
    private double getCycleFraction(int oneOverFraction) {
		double denominator = simCpu.getCycleFreq() * oneOverFraction;
		if(denominator < 0.00001) {
			return 0; // TODO: put a better value here
		} else {
			return (1000 / denominator);
		}
    }

    /**
     * Processes the message when a certain instruction is being executed
     * @param m The data movement message
     */
    public void processDataMovementMessage(DataMovementMessage m) {
        if(m.getInstruction().isPresent()){
            switch(m.getInstruction().get().getInstruction()) {
                case beq:
                case beqz:
                case bgtz:
                case bgez:
                case bne:
                case blez:
                case bltz:
                    double speed = getCycleFraction(6);

                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("BRANCH INSTRUCTION - Step 1 - Register operands are read and 16-bit immediate is sign extended", speed);
                            cpu.ir.highlight();
                            cpu.irToRegister1.animateData(speed);
                            cpu.irToRegister2.animateData(speed);
                            cpu.irToSignExtender.animateData(speed);
                            return null;
                        }
                    };

                    Task<Void> task2 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("BRANCH INSTRUCTION - Step 2 - Register operands are subtracted and 16-bit immediate is shifted left 2 bits",speed);
                            cpu.register.highlight();
                            cpu.signExtender.highlight();
                            cpu.registerToALU1.animateData(speed);
                            cpu.registerToALU2.animateData(speed);
                            cpu.signExtenderToShift.animateData(speed);
                            return null;
                        }
                    };

                    Task<Void> task3 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("BRANCH INSTRUCTION - Step 3 - 4 is added to PC value, so it's ready to be added to the offset", speed);
                            cpu.pcToPlusFour.animateData(speed);
                            return null;
                        }
                    };

                    Task<Void> task4 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("BRANCH INSTRUCTION - Step 4 - Next instructions address is calculated (assuming should branch)", speed);
                            cpu.shiftToAdder.animateData(speed);
                            cpu.plusFourToAdder.animateData(speed);
                            return null;
                        }
                    };

                    Task<Void> task5 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("BRANCH INSTRUCTION - Step 5 - (PC + 4) and (PC + offset) are fed into the mux with the alu result, this decides the next value of the PC", speed);

                            cpu.alu.highlight();
                            cpu.adder.highlight();
                            cpu.aluToMux.animateData(speed);
                            cpu.adderToMux.animateData(speed);
                            cpu.plusFourToMux.animateData(speed);
                            return null;
                        }
                    };

                    Task<Void> task6 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("BRANCH INSTRUCTION - Step 6 - Next PC value is updated", speed);
                            cpu.muxAdder.highlight();
                            cpu.muxToPC.animateData(speed);
                            return null;
                        }
                    };

                    cpu.animationProcessor.addAnimationTask(task, speed);
                    cpu.animationProcessor.addAnimationTask(task2, speed);
                    cpu.animationProcessor.addAnimationTask(task3, speed);
                    cpu.animationProcessor.addAnimationTask(task4, speed);
                    cpu.animationProcessor.addAnimationTask(task5, speed);
                    cpu.animationProcessor.addAnimationTask(task6, speed);

                    if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);

                    break;
                case lw:
                    processIType();
                    break;
                case sw:

                    speed = getCycleFraction(3);

                    task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("SW INSTRUCTION - Step 1 - ReadReg and WriteReg are selected and 16-bit immediate is sign extended", speed);
                            cpu.ir.highlight();
                            cpu.irToRegister1.animateData(speed);
                            cpu.irToRegister3.animateData(speed);
                            cpu.irToSignExtender.animateData(speed);
                            return null;
                        }
                    };

                    task2 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("SW INSTRUCTION - Step 2 - ALU computes sum of base address and sum extended offset", speed);
                            cpu.register.highlight();
                            cpu.signExtender.highlight();
                            cpu.registerToALU1.animateData(speed);
                            cpu.signExtenderToALU.animateData(speed);
                            return null;
                        }
                    };

                    task3 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("SW INSTRUCTION - Step 3 - Word is written to memory", speed);
                            cpu.alu.highlight();
                            cpu.aluToMemory.animateData(speed);
                            return null;
                        }
                    };


                    cpu.animationProcessor.addAnimationTask(task, speed);
                    cpu.animationProcessor.addAnimationTask(task2, speed);
                    cpu.animationProcessor.addAnimationTask(task3, speed);

                    if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);

                    break;
                case j:
                    speed = getCycleFraction(4);

                    task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JUMP INSTRUCTION - Step 1 - 26 bit immediate is shifted left 2 bits", speed);
                            cpu.ir.highlight();
                            cpu.irToShift.animateData(speed);
                            return null;
                        }
                    };

                    task2 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JUMP INSTRUCTION - Step 2 - PC + 4 is calculated", speed);
                            cpu.programCounter.highlight();
                            cpu.pcToPlusFour.animateData(speed);
                            return null;
                        }
                    };

                    task3 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JUMP INSTRUCTION - Step 3 - PC + 4 is combined with the 28 bit immediate", speed);
                            cpu.plusFour.highlight();
                            cpu.shiftLeftIR.highlight();
                            cpu.plusFourToMuxWithShift.animateData(speed);
                            cpu.shiftToMux.animateData(speed);
                            return null;
                        }
                    };

                    task4 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JUMP INSTRUCTION - Step 4 - PC value is updated to (PC + 4 + immediate)", speed);
                            cpu.muxAdder.highlight();
                            cpu.muxToPC.animateData(speed);
                            return null;
                        }
                    };

                    cpu.animationProcessor.addAnimationTask(task, speed);
                    cpu.animationProcessor.addAnimationTask(task2, speed);
                    cpu.animationProcessor.addAnimationTask(task3, speed);
                    cpu.animationProcessor.addAnimationTask(task4, speed);

                    if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);

                    break;
                case jal:
                    speed = getCycleFraction(5);
                    task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JAL INSTRUCTION - Step 1 - 26 bit immediate is shifted left 2 bits", speed);
                            cpu.ir.highlight();
                            cpu.irToShift.animateData(speed);
                            return null;
                        }
                    };

                    task2 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JAL INSTRUCTION - Step 2 - PC + 4 is calculated", speed);
                            cpu.programCounter.highlight();
                            cpu.pcToPlusFour.animateData(speed);
                            return null;
                        }
                    };


                    task3 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JAL INSTRUCTION - Step 3 - PC + 4 is combined with the 28 bit immediate", speed);
                            cpu.plusFour.highlight();
                            cpu.shiftLeftIR.highlight();
                            cpu.plusFourToMuxWithShift.animateData(speed);
                            cpu.shiftToMux.animateData(speed);
                            return null;
                        }
                    };

                    task4 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JAL INSTRUCTION - Step 4 - PC value is updated to (PC + 4 + immediate)", speed);
                            cpu.muxAdder.highlight();
                            cpu.muxToPC.animateData(speed);
                            return null;
                        }
                    };


                    task5 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JAL INSTRUCTION - Step 5 - $ra register is updated", speed);
                            cpu.ir.highlight();
                            cpu.irToRegister3.animateData(speed);
                            return null;
                        }
                    };

                    cpu.animationProcessor.addAnimationTask(task, speed);
                    cpu.animationProcessor.addAnimationTask(task2, speed);
                    cpu.animationProcessor.addAnimationTask(task3, speed);
                    cpu.animationProcessor.addAnimationTask(task4, speed);
                    cpu.animationProcessor.addAnimationTask(task5, speed);

                    if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);

                    break;
                case jr:
                    speed = getCycleFraction(3);
                    task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JR INSTRUCTION - Step 1 - Register value is read", speed);
                            cpu.ir.highlight();
                            cpu.irToRegister1.animateData(speed);
                            return null;
                        }
                    };

                    task2 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JR INSTRUCTION - Step 2 - Register value is passed to mux", speed);
                            cpu.register.highlight();
                            cpu.registertoMux.animateData(speed);
                            return null;
                        }
                    };

                    task3 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("JR INSTRUCTION - Step 3 - PC value is updated to register address", speed);
                            cpu.muxAdder.highlight();
                            cpu.muxToPC.animateData(speed);
                            return null;
                        }
                    };

                    cpu.animationProcessor.addAnimationTask(task, speed);
                    cpu.animationProcessor.addAnimationTask(task2, speed);
                    cpu.animationProcessor.addAnimationTask(task3, speed);

                    if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);

                    break;
                case li:
                case la:
                    speed = getCycleFraction(1);
                    task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            cpu.showText("LI/LA INSTRUCTION - Step 1 - Register is written to with the new value", speed);
                            cpu.ir.highlight();
                            cpu.irToRegister3.animateData(speed);
                            return null;
                        }
                    };
                    cpu.animationProcessor.addAnimationTask(task, speed);
                    if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);

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
                if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);
                break;
            case ITYPE:
                processIType();
                if (!replaying) vis.getCpu().previousInstructions.addInstruction(m);
                break;
        }
    }

    /**
     * Processes an I-TYPE instruction
     */
    public void processIType(){
        double speed = getCycleFraction(4);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cpu.showText("I-TYPE INSTRUCTION - Step 1 - ReadReg and WriteReg are selected and 16-bit immediate is sign extended", speed);
                cpu.ir.highlight();
                cpu.irToRegister1.animateData(speed);
                cpu.irToRegister3.animateData(speed);
                cpu.irToSignExtender.animateData(speed);
                return null;
            }
        };

        Task<Void> task2 = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cpu.showText("I-TYPE INSTRUCTION - Step 2 - ALU computes sum of base address and sum extended offset", speed);
                cpu.register.highlight();
                cpu.signExtender.highlight();
                cpu.registerToALU1.animateData(speed);
                cpu.signExtenderToALU.animateData(speed);
                return null;
            }
        };

        Task<Void> task3 = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cpu.showText("I-TYPE INSTRUCTION - Step 3 - Result is sent to memory", speed);
                cpu.alu.highlight();
                cpu.aluToMemory.animateData(speed);
                return null;
            }
        };

        Task<Void> task4 = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cpu.showText("I-TYPE INSTRUCTION - Step 4 - Word is read from memory and written to a register", speed);
                cpu.mainMemory.highlight();
                cpu.dataMemoryToRegisters.animateData(speed);
                return null;
            }
        };

        cpu.animationProcessor.addAnimationTask(task, speed);
        cpu.animationProcessor.addAnimationTask(task2, speed);
        cpu.animationProcessor.addAnimationTask(task3, speed);
        cpu.animationProcessor.addAnimationTask(task4, speed);

    }

    /**
     * Processess an R-TYPE instruction
     */
    public void processRType(){
        double speed = getCycleFraction(4);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cpu.showText("R-TYPE INSTRUCTION - Step 1 - Both read registers and one write register are selected", speed);
                cpu.ir.highlight();
                cpu.irToRegister1.animateData(speed);
                cpu.irToRegister2.animateData(speed);
                cpu.irToRegister3.animateData(speed);
                return null;
            }
        };

        Task<Void> task2 = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cpu.showText("R-TYPE INSTRUCTION - Step 2 - Two registers are added together via the ALU", speed);
                cpu.register.highlight();
                cpu.registerToALU1.animateData(speed);
                cpu.registerToALU2.animateData(speed);
                return null;
            }
        };

        Task<Void> task3 = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cpu.showText("R-TYPE INSTRUCTION - Step 3 - Result is written back to the register", speed);
                cpu.alu.highlight();
                cpu.aluToRegisters.animateData(speed);
                return null;
            }
        };

        cpu.animationProcessor.addAnimationTask(task, speed);
        cpu.animationProcessor.addAnimationTask(task2, speed);
        cpu.animationProcessor.addAnimationTask(task3, speed);

    }

    public void startOfCycle(){
        haltSimulation();
        while(animationProcessor.getRemaining() > 0);
        releaseSimulation();
    }

    /**
     * Handles certain stages e.g the fetch, decode or execute stage
     * @param m The stage enter message
     */
    public void processStageEnterMessage(StageEnterMessage m) {
        switch (m.getStage()){
            case Fetch:
                startOfCycle();
                double speed = getCycleFraction(2);

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        cpu.showText("INSTRUCTION FETCH - Step 1 - The PC value (address of next instruction) is sent to main memory to read the next instruction", speed);
                        cpu.programCounter.highlight();
                        cpu.PCToIM.animateData(speed);
                        return null;
                    }
                };

                Task<Void> task2 = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        cpu.showText("INSTRUCTION FETCH - Step 2 - The next instruction is passed from memory to the instruction register", speed);
                        cpu.instructionMemory.highlight();
                        cpu.codeMemoryToIR.animateData(speed);
                        return null;
                    }
                };


                Task<Void> task3 = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        cpu.showText("INSTRUCTION FETCH - Step 3 - PC is updated to the next instruction address", speed * 3);
                        cpu.programCounter.highlight();
                        cpu.pcToPlusFour.animateData(speed);
                        return null;
                    }
                };

                Task<Void> task4 = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        cpu.plusFour.highlight();
                        cpu.plusFourToMux.animateData(speed);
                        return null;
                    }
                };

                Task<Void> task5 = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        cpu.muxAdder.highlight();
                        cpu.muxToPC.animateData(speed);
                        return null;
                    }
                };

                cpu.animationProcessor.addAnimationTask(task, speed);
                cpu.animationProcessor.addAnimationTask(task2, speed);
                cpu.animationProcessor.addAnimationTask(task3, speed);
                cpu.animationProcessor.addAnimationTask(task4, speed);
                cpu.animationProcessor.addAnimationTask(task5, speed);

                break;
        }
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