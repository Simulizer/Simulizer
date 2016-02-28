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
    public CustomWire memToRes;
    public CustomWire IrTOPC;
    public CustomWire PCToPlusFour;
    public CustomWire registerToMemory;
    public CustomWire IMToALU;
    public CustomWire IMToIR;
    public CustomWire IMToRegister1;
    public CustomWire IMToRegister2;
    public CustomWire IMToRegister3;
    public ConnectorWire controlUnitToIr;
    public ConnectorWire controlUnitToPC;
    public ConnectorWire controlUnitToPlusFour;
    public ConnectorWire controlUnitToIM1;
    public ConnectorWire controlUnitToIM2;
    public ConnectorWire controlUnitToRegisters;
    public ConnectorWire controlUnitToALU;
    public ConnectorWire controlUnitToDataMemory;
    public ConnectorWire plusFourToIr;
    public ConnectorWire PCToIM;
    public ConnectorWire aluToMemory;
    public ConnectorWire registerToALU1;
    public ConnectorWire registerToALU2;

    public CPU(CPUVisualisation vis, double width, double height){
        this.width = width;
        this.height = height;
        this.vis = vis;
    }

    public void drawCPU(){
        Group components = new Group();
        controlUnit = new GeneralComponent(vis, "Controller");
        programCounter = new GeneralComponent(vis, "PC");
        instructionMemory = new GeneralComponent(vis, "Instruction Memory");
        register = new GeneralComponent(vis, "Registers");
        alu = new ALU(vis, "ALU");
        mainMemory = new GeneralComponent(vis, "Data Memory");
        ir = new GeneralComponent(vis, "");
        unknown = new GeneralComponent(vis, "+4");

        controlUnit.setTooltip("The control unit (CU) is a component of a computer's central processing unit (CPU) that directs operation of the processor. It tells the computer's memory, arithmetic/logic unit and input and output devices how to respond to a program's instructions.");

        programCounter.setTooltip("A program counter is a register in a computer processor that contains the address (location) of the instruction being executed at the current time. As each instruction gets fetched, the program counter increases its stored value by 1.");

        register.setTooltip("In computer architecture, a processor register is a small amount of storage available as part of a digital processor, such as a central processing unit (CPU). Such registers are typically addressed by mechanisms other than main memory and can be accessed faster.");

        alu.setTooltip("An arithmetic logic unit (ALU) is a digital circuit used to perform arithmetic and logic operations. It represents the fundamental building block of the central processing unit (CPU) of a computer. Modern CPUs contain very powerful and complex ALUs. In addition to ALUs, modern CPUs contain a control unit (CU).");

        mainMemory.setTooltip("PC memory is more easily referred to as RAM (Random Access Memory) and performs very different tasks to storage memory which is found on a hard drive or SSD. RAM, in the form of a memory module, is a component in your computer which enables short-term or temporary data access.");

        ir.setTooltip("In computing, an instruction register (IR) is the part of a CPU's control unit that stores the instruction currently being executed or decoded.");

        generalWires = new Group();

        controlUnitToIr = ir.verticalLineTo(controlUnit, true, true, 0);
        controlUnitToPC = programCounter.verticalLineTo(controlUnit, true, true, 0);
        controlUnitToPlusFour = unknown.verticalLineTo(controlUnit, true, true, 0);
        controlUnitToIM1 = instructionMemory.verticalLineTo(controlUnit, true, true, -0.1);
        controlUnitToIM2 = instructionMemory.verticalLineTo(controlUnit, true, false, 0.1);
        controlUnitToRegisters = register.verticalLineTo(controlUnit, true, true, 0);
        controlUnitToALU = alu.verticalLineTo(controlUnit, true, true, 0);
        controlUnitToDataMemory = mainMemory.verticalLineTo(controlUnit, true, true, 0);
        plusFourToIr = unknown.horizontalLineTo(ir, false, false, 0);
        PCToIM = programCounter.horizontalLineTo(instructionMemory, true, false, 0);
        aluToMemory = alu.horizontalLineTo(mainMemory, true, false, 0);
        registerToALU1 = register.horizontalLineTo(alu, true, false, -0.3);
        registerToALU2 = register.horizontalLineTo(alu, true, false, 0.3);

        memToRes = new CustomWire(580, 80);
        IrTOPC = new CustomWire(15, 230);
        PCToPlusFour = new CustomWire(60, 110);
        registerToMemory = new CustomWire(320, 145);
        IMToALU = new CustomWire(190, 150);
        IMToIR = new CustomWire(190, 150);
        IMToRegister1 = new CustomWire(190, 150);
        IMToRegister2 = new CustomWire(190, 150);
        IMToRegister3 = new CustomWire(190, 150);

        generalWires.getChildren().addAll(
                controlUnitToIr,
                controlUnitToPC,
                controlUnitToPlusFour,
                controlUnitToIM1,
                controlUnitToIM2,
                controlUnitToRegisters,
                controlUnitToALU,
                controlUnitToDataMemory,
                plusFourToIr,
                PCToIM,
                aluToMemory,
                registerToALU1,
                registerToALU2
        );

        Group complexWires = new Group();
        complexWires.getChildren().addAll(
                memToRes,
                IrTOPC,
                PCToPlusFour,
                registerToMemory,
                IMToALU,
                IMToIR,
                IMToRegister1,
                IMToRegister2,
                IMToRegister3
        );

        components.getChildren().addAll(register, instructionMemory, alu, mainMemory, programCounter, ir, unknown);
        vis.addAll(controlUnit, components, generalWires, complexWires);

    }

    public void resizeShapes(){
        double width = vis.getWindowWidth();
        double height = vis.getWindowHeight();

        controlUnit.setAttrs(width * 0.03, height - (height * 0.2), width * 0.94, height * 0.1);
        programCounter.setAttrs(width * 0.06, height * 0.15, width * 0.06, height * 0.25);
        instructionMemory.setAttrs(width * 0.15, height * 0.15, width * 0.16, height * 0.25);
        register.setAttrs(width * 0.367, height * 0.15, width * 0.16, height * 0.25);
        alu.setAttrs(width * 0.58, height * 0.15, width * 0.11, height * 0.25);
        mainMemory.setAttrs(width * 0.8, height * 0.15, width * 0.16, height * 0.4);
        ir.setAttrs(width * 0.025, height * 0.55, width * 0.03, height * 0.125);
        unknown.setAttrs(width * 0.1, height * 0.525, width * 0.06, height * 0.1);

        ObservableList<Node> wires = generalWires.getChildren();

        for(Node wire : wires){
            ((ConnectorWire) wire).updateLine();
        }

        memToRes.drawLine(width * 0.96, height * 0.2, new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.125, CustomLine.Direction.UP),
                new CustomLine(width * 0.583, CustomLine.Direction.LEFT),
                new CustomLine(height * 0.075, CustomLine.Direction.DOWN)
        );


        IrTOPC.drawLine(width * 0.025, height * 0.575,
                new CustomLine(width * 0.016, CustomLine.Direction.LEFT),
                new CustomLine(height * 0.25, CustomLine.Direction.UP),
                new CustomLine(width * 0.05, CustomLine.Direction.RIGHT)
        );

        PCToPlusFour.drawLine(width * 0.12, height * 0.275,
                new CustomLine(width * 0.015, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.25, CustomLine.Direction.DOWN)
        );

        registerToMemory.drawLine(width * 0.53, height * 0.3625,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.125, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.25, CustomLine.Direction.RIGHT)
        );

        IMToALU.drawLine(width * 0.310, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.2875, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.2333, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.31, CustomLine.Direction.UP)
        );

        IMToIR.drawLine(width * 0.310, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.2875, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.272, CustomLine.Direction.LEFT)
        );

        IMToRegister1.drawLine(width * 0.310, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.175, CustomLine.Direction.UP),
                new CustomLine(width * 0.04, CustomLine.Direction.RIGHT)
        );

        IMToRegister2.drawLine(width * 0.310, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.1, CustomLine.Direction.UP),
                new CustomLine(width * 0.04, CustomLine.Direction.RIGHT)
        );

        IMToRegister3.drawLine(width * 0.310, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.025, CustomLine.Direction.UP),
                new CustomLine(width * 0.04, CustomLine.Direction.RIGHT)
        );

    }
    
    public void closeAllThreads(){
    	memToRes.closeThread();
    	IrTOPC.closeThread();
    	PCToPlusFour.closeThread();
    	registerToMemory.closeThread();
    	IMToALU.closeThread();
    	IMToIR.closeThread();
    	IMToRegister1.closeThread();
    	IMToRegister2.closeThread();
    	IMToRegister3.closeThread();
    	controlUnitToIr.closeThread();
    	controlUnitToPC.closeThread();
    	controlUnitToPlusFour.closeThread();
    	controlUnitToIM1.closeThread();
    	controlUnitToIM2.closeThread();
    	controlUnitToRegisters.closeThread();
    	controlUnitToALU.closeThread();
    	controlUnitToDataMemory.closeThread();
    	plusFourToIr.closeThread();
    	PCToIM.closeThread();
    	aluToMemory.closeThread();
    	registerToALU1.closeThread();
    	registerToALU2.closeThread();
    }
}
