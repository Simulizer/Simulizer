package simulizer.cpu.visualisation;

import javafx.scene.Group;
import simulizer.cpu.visualisation.components.*;
import simulizer.ui.windows.CPUVisualisation;


public class CPU {

    int width;
    int height;
    CPUVisualisation vis;
    static int ARROW_SIZE;

    public CPU(CPUVisualisation vis, int width, int height){
        this.width = width;
        this.height = height;
        this.vis = vis;
    }

    public void drawCPU(){
        GeneralComponent controlUnit = new GeneralComponent(20, height - 60, width - 40, 30, "Controller");

        Group components = new Group();

        GeneralComponent programCounter = new GeneralComponent(20, 60, 40, 100, "PC");
        GeneralComponent instructionMemory = new GeneralComponent(90, 60, 100, 100, "Instruction Memory");
        GeneralComponent register = new GeneralComponent(220, 60, 100, 100, "Registers");
        ALU alu = new ALU(350, 60, 100, 100, "ALU");
        GeneralComponent mainMemory = new GeneralComponent(480, 60, 100, 150, "Data Memory");

        GeneralComponent ir = new GeneralComponent(15, 220, 20, 50, "");
        GeneralComponent unknown = new GeneralComponent(60, 210, 40, 40, "+4");

        Wire controlUnitToIr = ir.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToPC = programCounter.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToPlusFour = unknown.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToIM1 = instructionMemory.vericalLineTo(controlUnit, true, true, -10);
        Wire controlUnitToIM2 = instructionMemory.vericalLineTo(controlUnit, true, false, 10);
        Wire controlUnitToRegisters = register.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToALU = alu.vericalLineTo(controlUnit, true, true, 0);
        Wire controlUnitToDataMemory = mainMemory.vericalLineTo(controlUnit, true, true, 0);

        controlUnitToIM1.animateData(4, true);

        Wire plusFourToIr = unknown.horizontalLineTo(ir, false, false, 0);
        Wire PCToIM = programCounter.horizontalLineTo(instructionMemory, true, false, 0);
        Wire aluToMemory = alu.horizontalLineTo(mainMemory, true, false, 0);

        Wire reisterToALU1 = register.horizontalLineTo(alu, true, false, -35);
        Wire reisterToALU2 = register.horizontalLineTo(alu, true, false, 35);

        Wire memToRes = new CustomWire(580, 80,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(50, CustomLine.Direction.UP),
                new CustomLine(350, CustomLine.Direction.LEFT),
                new CustomLine(30, CustomLine.Direction.DOWN)
        );

        Wire IrTOPC = new CustomWire(15, 230,
                new CustomLine(10, CustomLine.Direction.LEFT),
                new CustomLine(100, CustomLine.Direction.UP),
                new CustomLine(15, CustomLine.Direction.RIGHT)
        );

        Wire PCToPlusFour = new CustomWire(60, 110,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(100, CustomLine.Direction.DOWN)
        );

        Wire registerToMemory = new CustomWire(320, 145,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(50, CustomLine.Direction.DOWN),
                new CustomLine(150, CustomLine.Direction.RIGHT)
        );

        Wire IMToALU = new CustomWire(190, 150,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(115, CustomLine.Direction.DOWN),
                new CustomLine(140, CustomLine.Direction.RIGHT),
                new CustomLine(115, CustomLine.Direction.UP)
        );

        Wire IMToIR = new CustomWire(190, 150,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(115, CustomLine.Direction.DOWN),
                new CustomLine(165, CustomLine.Direction.LEFT)
        );

        Wire IMToRegister1 = new CustomWire(190, 150,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(70, CustomLine.Direction.UP),
                new CustomLine(20, CustomLine.Direction.RIGHT)
        );

        Wire IMToRegister2 = new CustomWire(190, 150,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(40, CustomLine.Direction.UP),
                new CustomLine(20, CustomLine.Direction.RIGHT)
        );

        Wire IMToRegister3 = new CustomWire(190, 150,
                new CustomLine(10, CustomLine.Direction.RIGHT),
                new CustomLine(10, CustomLine.Direction.UP),
                new CustomLine(20, CustomLine.Direction.RIGHT)
        );

        Group lines = new Group();
        lines.getChildren().addAll(
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
                reisterToALU2,
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
        vis.addAll(controlUnit, components, lines);
    }
}
