package simulizer.cpu.visualisation;

import javafx.scene.Group;
import simulizer.cpu.visualisation.components.*;
import simulizer.ui.windows.CPUVisualisation;


public class CPU {

    int width;
    int height;
    CPUVisualisation vis;

    public CPU(CPUVisualisation vis, int width, int height){
        this.width = width;
        this.height = height;
        this.vis = vis;
    }

    public void drawCPU(){
        ControlUnit controlUnit = new ControlUnit(20, height - 60, width - 40, 30, "Controller");

        Group components = new Group();

        ProgramCounter programCounter = new ProgramCounter(0, 0, 40, 100, "PC");
        InstructionMemory instructionMemory = new InstructionMemory(70, 0, 100, 100, "Instruction Memory");
        RegisterBlock register = new RegisterBlock(200, 0, 100, 100, "Registers");
        ALU alu = new ALU(330, 0, 100, 100, "ALU");
        MainMemory mainMemory = new MainMemory(460, 0, 100, 100, "Data Memory");

        components.getChildren().addAll(register, instructionMemory, alu, mainMemory, programCounter);
        components.setLayoutX(20);
        components.setLayoutY(60);

        vis.addAll(controlUnit, components);
    }
}
