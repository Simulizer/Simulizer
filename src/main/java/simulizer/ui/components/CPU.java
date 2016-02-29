package simulizer.ui.components;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import simulizer.ui.components.cpu.ALU;
import simulizer.ui.components.cpu.ConnectorWire;
import simulizer.ui.components.cpu.CustomLine;
import simulizer.ui.components.cpu.CustomWire;
import simulizer.ui.components.cpu.GeneralComponent;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.CPUVisualisation;

public class CPU {

    double width;
    double height;
    CPUVisualisation vis;

    // Items needed to set widths
    public GeneralComponent controlUnit;
    public GeneralComponent programCounter;
    public GeneralComponent instructionMemory;
    public GeneralComponent register;
    public ALU alu;
    public GeneralComponent mainMemory;
    public GeneralComponent ir;
    public GeneralComponent unknown;
    public Group generalWires;
    public CustomWire irToPlusFour;
    public CustomWire irToRegister1;
    public CustomWire irToRegister2;
    public CustomWire irToRegister3;
    public CustomWire pcToPlusFour;
    public CustomWire plusFourToPc;
    public CustomWire registerToDataMemory;
    public CustomWire dataMemoryToRegisters;
    public CustomWire aluToRegisters;
    public CustomWire aluToControlUnit;
    public ConnectorWire irToControlUnit;
    public ConnectorWire controlUnitToPlusFour;
    public ConnectorWire controlUnitToIM1;
    public ConnectorWire controlUnitToRegisters;
    public ConnectorWire controlUnitToDataMemory;
    public ConnectorWire PCToIM;
    public ConnectorWire aluToMemory;
    public ConnectorWire registerToALU1;
    public ConnectorWire registerToALU2;
    public ConnectorWire codeMemoryToIR;

    public CPU(CPUVisualisation vis, double width, double height){
        this.width = width;
        this.height = height;
        this.vis = vis;
    }

    public void drawCPU(){
        Group components = new Group();
        controlUnit = new GeneralComponent(vis, "Controller");
        programCounter = new GeneralComponent(vis, "PC");
        instructionMemory = new GeneralComponent(vis, "Code Memory");
        register = new GeneralComponent(vis, "Registers");
        alu = new ALU(vis, "ALU");
        mainMemory = new GeneralComponent(vis, "Data Memory");
        ir = new GeneralComponent(vis, "IR");
        unknown = new GeneralComponent(vis, "+");

        controlUnit.setTooltip("The control unit (CU) is a component of a computer's central processing unit (CPU) that directs operation of the processor. It tells the computer's memory, arithmetic/logic unit and input and output devices how to respond to a program's instructions.");

        programCounter.setTooltip("A program counter is a register in a computer processor that contains the address (location) of the instruction being executed at the current time. As each instruction gets fetched, the program counter increases its stored value by 1.");

        register.setTooltip("In computer architecture, a processor register is a small amount of storage available as part of a digital processor, such as a central processing unit (CPU). Such registers are typically addressed by mechanisms other than main memory and can be accessed faster.");

        alu.setTooltip("An arithmetic logic unit (ALU) is a digital circuit used to perform arithmetic and logic operations. It represents the fundamental building block of the central processing unit (CPU) of a computer. Modern CPUs contain very powerful and complex ALUs. In addition to ALUs, modern CPUs contain a control unit (CU).");

        mainMemory.setTooltip("PC memory is more easily referred to as RAM (Random Access Memory) and performs very different tasks to storage memory which is found on a hard drive or SSD. RAM, in the form of a memory module, is a component in your computer which enables short-term or temporary data access.");

        ir.setTooltip("In computing, an instruction register (IR) is the part of a CPU's control unit that stores the instruction currently being executed or decoded.");

        generalWires = new Group();

        irToControlUnit = ir.verticalLineTo(controlUnit, false, false, 0);
        controlUnitToPlusFour = unknown.verticalLineTo(controlUnit, false, true, 0);
        controlUnitToIM1 = instructionMemory.verticalLineTo(controlUnit, false, true, 0);
        controlUnitToRegisters = register.verticalLineTo(controlUnit, false, true, 0);
        controlUnitToDataMemory = mainMemory.verticalLineTo(controlUnit, false, true, 0);
        PCToIM = programCounter.horizontalLineTo(instructionMemory, true, false, 0);
        aluToMemory = alu.horizontalLineTo(mainMemory, true, false, 0.3);
        registerToALU1 = register.horizontalLineTo(alu, true, false, -0.3);
        registerToALU2 = register.horizontalLineTo(alu, true, false, 0.3);
        codeMemoryToIR = instructionMemory.horizontalLineTo(ir, true, false, 0);

        irToPlusFour = new CustomWire(0,0);
        irToRegister1 = new CustomWire(0,0);
        irToRegister2 = new CustomWire(0,0);
        irToRegister3 = new CustomWire(0,0);
        pcToPlusFour = new CustomWire(0,0);
        plusFourToPc = new CustomWire(0,0);
        registerToDataMemory = new CustomWire(0,0);
        dataMemoryToRegisters = new CustomWire(0,0);
        aluToRegisters = new CustomWire(0,0);
        aluToControlUnit = new CustomWire(0,0);

        generalWires.getChildren().addAll(
            irToControlUnit,
            controlUnitToPlusFour,
            controlUnitToIM1,
            controlUnitToRegisters,
            controlUnitToDataMemory,
            PCToIM,
            aluToMemory,
            registerToALU1,
            registerToALU2,
            codeMemoryToIR
        );

        Group complexWires = new Group();
        complexWires.getChildren().addAll(
            irToPlusFour,
            irToRegister1,
            irToRegister2,
            irToRegister3,
            pcToPlusFour,
            plusFourToPc,
            registerToDataMemory,
            dataMemoryToRegisters,
            aluToRegisters,
            aluToControlUnit
        );

        components.getChildren().addAll(register, instructionMemory, alu, mainMemory, programCounter, ir, unknown);
        vis.addAll(controlUnit, components, generalWires, complexWires);

    }

    public void resizeShapes(){
        double width = vis.getWindowWidth();
        double height = vis.getWindowHeight();

        controlUnit.setAttrs(width * 0.03, height * 0.1, width * 0.94, height * 0.1);
        programCounter.setAttrs(width * 0.06, height * 0.5, width * 0.05, height * 0.25);
        instructionMemory.setAttrs(width * 0.15, height * 0.5, width * 0.16, height * 0.25);
        ir.setAttrs(width * 0.35, height * 0.5, width * 0.05, height * 0.25);

        register.setAttrs(width * 0.48, height * 0.5, width * 0.16, height * 0.25);
        alu.setAttrs(width * 0.68, height * 0.5, width * 0.065, height * 0.25);
        mainMemory.setAttrs(width * 0.8, height * 0.65, width * 0.16, height * 0.25);

        unknown.setAttrs(width * 0.415, height * 0.35, width * 0.04, height * 0.1);

        ObservableList<Node> wires = generalWires.getChildren();

        for(Node wire : wires){
            ((ConnectorWire) wire).updateLine();
        }

        irToPlusFour.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() / 2,
                new CustomLine(width * 0.035, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.17, CustomLine.Direction.UP)
        );

        irToRegister1.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() / 2,
                new CustomLine(width * 0.035, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.1, CustomLine.Direction.UP),
                new CustomLine(width * 0.044, CustomLine.Direction.RIGHT)
        );

        irToRegister2.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() / 2,
                new CustomLine(width * 0.035, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.06, CustomLine.Direction.UP),
                new CustomLine(width * 0.044, CustomLine.Direction.RIGHT)
        );

        irToRegister3.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() / 2,
                new CustomLine(width * 0.035, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.02, CustomLine.Direction.UP),
                new CustomLine(width * 0.044, CustomLine.Direction.RIGHT)
        );

        pcToPlusFour.drawLine(programCounter.getLayoutX() + programCounter.getShapeWidth(), programCounter.getLayoutY() + programCounter.getShapeHeight() / 2,
                new CustomLine(width * 0.015, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.22, CustomLine.Direction.UP),
                new CustomLine(width * 0.29, CustomLine.Direction.RIGHT)
        );

        plusFourToPc.drawLine(unknown.getLayoutX() + unknown.getShapeWidth(), unknown.getLayoutY() + unknown.getShapeHeight() / 2,
                new CustomLine(width * 0.015, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.12, CustomLine.Direction.UP),
                new CustomLine(width * 0.44, CustomLine.Direction.LEFT),
                new CustomLine(height * 0.345, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.03, CustomLine.Direction.RIGHT)
        );

        aluToControlUnit.drawLine(alu.getLayoutX() + alu.getShapeWidth(), alu.getLayoutY() + alu.getShapeHeight() * 0.3,
                new CustomLine(width * 0.025, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.365, CustomLine.Direction.UP)
        );

        registerToDataMemory.drawLine(register.getLayoutX() + register.getShapeWidth(), register.getLayoutY() + register.getShapeHeight() * 0.8,
                new CustomLine(width * 0.015, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.16, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.145, CustomLine.Direction.RIGHT)
        );

        dataMemoryToRegisters.drawLine(mainMemory.getLayoutX() + mainMemory.getShapeWidth(), mainMemory.getLayoutY() + mainMemory.getShapeHeight() * 0.8,
                new CustomLine(width * 0.015, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.1, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.54, CustomLine.Direction.LEFT),
                new CustomLine(height * 0.23, CustomLine.Direction.UP),
                new CustomLine(width * 0.045, CustomLine.Direction.RIGHT)
        );

        aluToRegisters.drawLine(alu.getLayoutX() + alu.getShapeWidth(), alu.getLayoutY() + alu.getShapeHeight() * 0.8,
                new CustomLine(width * 0.02, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.265, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.33, CustomLine.Direction.LEFT),
                new CustomLine(height * 0.23, CustomLine.Direction.UP),
                new CustomLine(width * 0.045, CustomLine.Direction.RIGHT)
        );


    }
    
    public void closeAllThreads(){
        irToControlUnit.closeThread();
        controlUnitToPlusFour.closeThread();
        controlUnitToIM1.closeThread();
        controlUnitToRegisters.closeThread();
        controlUnitToDataMemory.closeThread();
        PCToIM.closeThread();
        aluToMemory.closeThread();
        codeMemoryToIR.closeThread();
    	registerToALU1.closeThread();
    	registerToALU2.closeThread();

        irToPlusFour.closeThread();
        irToRegister1.closeThread();
        irToRegister2.closeThread();
        irToRegister3.closeThread();
        pcToPlusFour.closeThread();
        plusFourToPc.closeThread();
        registerToDataMemory.closeThread();
        dataMemoryToRegisters.closeThread();
        aluToRegisters.closeThread();
        aluToControlUnit.closeThread();
    }
}
