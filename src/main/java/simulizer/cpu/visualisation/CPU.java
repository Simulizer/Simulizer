package simulizer.cpu.visualisation;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import simulizer.cpu.visualisation.components.*;
import simulizer.ui.interfaces.WindowEnum;
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
    ConnectorWire controlUnitToIr;
    ConnectorWire controlUnitToPC;
    ConnectorWire controlUnitToPlusFour;
    ConnectorWire controlUnitToIM1;
    ConnectorWire controlUnitToIM2;
    ConnectorWire controlUnitToRegisters;
    ConnectorWire controlUnitToALU;
    ConnectorWire controlUnitToDataMemory;
    ConnectorWire plusFourToIr;
    ConnectorWire PCToIM;
    ConnectorWire aluToMemory;
    ConnectorWire registerToALU1;
    ConnectorWire registerToALU2;

    public CPU(CPUVisualisation vis, double width, double height){
        this.width = width;
        this.height = height;
        this.vis = vis;
    }

    public void drawCPU(){
        Group components = new Group();
        controlUnit = new GeneralComponent(20, height - 60, width - 40, 30, "Controller");
        programCounter = new GeneralComponent(20, 60, (width * 0.06), 100, "PC");
        instructionMemory = new GeneralComponent(90, 60, 100, 100, "Instruction Memory");
        register = new GeneralComponent(220, 60, 100, 100, "Registers");
        alu = new ALU(350, 60, 100, 100, "ALU");
        mainMemory = new GeneralComponent(480, 60, 100, 150, "Data Memory");
        ir = new GeneralComponent(15, 220, 20, 50, "");
        unknown = new GeneralComponent(60, 210, 40, 40, "+4");

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

        vis.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                double xMin = register.getLayoutX();
                double xMax = xMin + register.getShapeWidth();
                double yMin = register.getLayoutY();
                double yMax = yMin + register.getShapeHeight();

                if( x > xMin && x < xMax && y > yMin && y < yMax){
                    // In register box, highlight register window somehow?
                    vis.getMainWindowManager().findInternalWindow(WindowEnum.REGISTERS).emphasise();
                }
            }
        });

        components.getChildren().addAll(register, instructionMemory, alu, mainMemory, programCounter, ir, unknown);
        vis.addAll(controlUnit, components, generalWires, complexWires);

    }

    public void demoVis(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controlUnit.highlight(2);
                        }
                    });

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controlUnitToIr.animateData(2, true);
                        }
                    });

                    Thread.sleep(3000);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ir.highlight(2);
                        }
                    });

                    Thread.sleep(500);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            IrTOPC.animateData(4, false);
                        }
                    });

                    Thread.sleep(4000);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            programCounter.highlight(2);
                        }
                    });

                    Thread.sleep(500);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            PCToIM.animateData(1, false);
                        }
                    });

                    Thread.sleep(1000);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            instructionMemory.highlight(2);
                        }
                    });

                    Thread.sleep(500);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            IMToRegister1.animateData(3, false);
                        }
                    });

                    Thread.sleep(3000);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            register.highlight(2);
                        }
                    });

                    Thread.sleep(500);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            registerToALU1.animateData(1, false);
                        }
                    });

                    Thread.sleep(1000);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            alu.highlight(2);
                        }
                    });

                    Thread.sleep(500);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            aluToMemory.animateData(1, false);
                        }
                    });

                    Thread.sleep(1000);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mainMemory.highlight(2);
                        }
                    });

                } catch(InterruptedException e){
                    System.out.println(e.getMessage());
                }

            }
        }).start();

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
}
