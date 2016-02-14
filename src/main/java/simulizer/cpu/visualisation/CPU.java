package simulizer.cpu.visualisation;

import javafx.scene.Group;
import javafx.scene.shape.Line;
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

        ProgramCounter programCounter = new ProgramCounter(20, 60, 40, 100, "PC");
        InstructionMemory instructionMemory = new InstructionMemory(90, 60, 100, 100, "Instruction Memory");
        RegisterBlock register = new RegisterBlock(220, 60, 100, 100, "Registers");
        ALU alu = new ALU(350, 60, 100, 100, "ALU");
        MainMemory mainMemory = new MainMemory(480, 60, 100, 100, "Data Memory");

        Group line = programCounter.connect(controlUnit, true, true);
        Group line2 = instructionMemory.connect(controlUnit, true, true, -20);
        Group line3 = instructionMemory.connect(controlUnit, true, false, 20);
        Group line4 = register.connect(controlUnit, true, true);
        Group line5 = alu.connect(controlUnit, true, true);
        Group line6 = mainMemory.connect(controlUnit, true, true);

        Group lines = new Group();
        lines.getChildren().addAll(line, line2, line3, line4, line5, line6);

        components.getChildren().addAll(register, instructionMemory, alu, mainMemory, programCounter);
        vis.addAll(controlUnit, components, lines);
    }
}
