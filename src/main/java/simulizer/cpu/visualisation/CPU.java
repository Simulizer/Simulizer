package simulizer.cpu.visualisation;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import simulizer.cpu.visualisation.components.*;
import simulizer.ui.windows.CPUVisualisation;

public class CPU {

    double width;
    double height;
    CPUVisualisation vis;

    // Items needed to set widths
    GeneralComponent controlUnit;
    GeneralComponent programCounter;
    GeneralComponent instructionMemory;
    GeneralComponent register;
    ALU alu;
    GeneralComponent mainMemory;
    GeneralComponent ir;
    GeneralComponent unknown;
    Group generalWires;
    CustomWire memToRes;
    CustomWire IrTOPC;
    CustomWire PCToPlusFour;
    CustomWire registerToMemory;
    CustomWire IMToALU;
    CustomWire IMToIR;
    CustomWire IMToRegister1;
    CustomWire IMToRegister2;
    CustomWire IMToRegister3;

    public CPU(CPUVisualisation vis, double width, double height){
        this.width = width;
        this.height = height;
        this.vis = vis;
    }

    public void drawCPU(){
        double width = vis.getWindowWidth();
        double height = vis.getWindowHeight();

        controlUnit = new GeneralComponent(20, height - 60, width - 40, 30, "Controller");

        Group components = new Group();
        generalWires = new Group();

        programCounter = new GeneralComponent(20, 60, (width * 0.06), 100, "PC");
        instructionMemory = new GeneralComponent(90, 60, 100, 100, "Instruction Memory");
        register = new GeneralComponent(220, 60, 100, 100, "Registers");
        alu = new ALU(350, 60, 100, 100, "ALU");
        mainMemory = new GeneralComponent(480, 60, 100, 150, "Data Memory");
        ir = new GeneralComponent(15, 220, 20, 50, "");
        unknown = new GeneralComponent(60, 210, 40, 40, "+4");

        Wire controlUnitToIr = ir.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToPC = programCounter.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToPlusFour = unknown.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToIM1 = instructionMemory.vericalLineTo(controlUnit, true, true, -0.1);
        Wire controlUnitToIM2 = instructionMemory.vericalLineTo(controlUnit, true, false, 0.1);
        Wire controlUnitToRegisters = register.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToALU = alu.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToDataMemory = mainMemory.vericalLineTo(controlUnit, true, true, 0);

        controlUnitToIM1.animateData(4, true);

        Wire plusFourToIr = unknown.horizontalLineTo(ir, false, false, 0);
        Wire PCToIM = programCounter.horizontalLineTo(instructionMemory, true, false, 0);
        Wire aluToMemory = alu.horizontalLineTo(mainMemory, true, false, 0);

        Wire reisterToALU1 = register.horizontalLineTo(alu, true, false, -0.3);
        Wire reisterToALU2 = register.horizontalLineTo(alu, true, false, 0.3);

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
                reisterToALU1,
                reisterToALU2
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
        alu.setAttrs(width * 0.58, height * 0.15, width * 0.16, height * 0.25);
        mainMemory.setAttrs(width * 0.8, height * 0.15, width * 0.16, height * 0.4);
        ir.setAttrs(width * 0.025, height * 0.55, width * 0.03, height * 0.125);
        unknown.setAttrs(width * 0.1, height * 0.525, width * 0.06, height * 0.1);

        ObservableList<Node> wires = generalWires.getChildren();

        for(Node wire : wires){
            ((Wire) wire).updateLine();
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

        PCToPlusFour.drawLine(width * 0.1, height * 0.275,
                new CustomLine(width * 0.035, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.25, CustomLine.Direction.DOWN)
        );

        registerToMemory.drawLine(width * 0.53, height * 0.3625,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.125, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.25, CustomLine.Direction.RIGHT)
        );

        IMToALU.drawLine(width * 0.316, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.2875, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.2333, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.2875, CustomLine.Direction.UP)
        );

        IMToIR.drawLine(width * 0.316, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.2875, CustomLine.Direction.DOWN),
                new CustomLine(width * 0.275, CustomLine.Direction.LEFT)
        );

        IMToRegister1.drawLine(width * 0.316, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.175, CustomLine.Direction.UP),
                new CustomLine(width * 0.033, CustomLine.Direction.RIGHT)
        );

        IMToRegister2.drawLine(width * 0.316, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.1, CustomLine.Direction.UP),
                new CustomLine(width * 0.033, CustomLine.Direction.RIGHT)
        );

        IMToRegister3.drawLine(width * 0.316, height * 0.375,
                new CustomLine(width * 0.016, CustomLine.Direction.RIGHT),
                new CustomLine(height * 0.025, CustomLine.Direction.UP),
                new CustomLine(width * 0.033, CustomLine.Direction.RIGHT)
        );


    }
}
