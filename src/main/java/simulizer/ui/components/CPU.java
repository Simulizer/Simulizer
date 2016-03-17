package simulizer.ui.components;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Duration;
import simulizer.ui.components.cpu.ALU;
import simulizer.ui.components.cpu.AnimationProcessor;
import simulizer.ui.components.cpu.ConnectorWire;
import simulizer.ui.components.cpu.CustomLine;
import simulizer.ui.components.cpu.CustomWire;
import simulizer.ui.components.cpu.GeneralComponent;
import simulizer.ui.components.cpu.InstructionsWindow;
import simulizer.ui.windows.CPUVisualisation;

/**
 * Represents a visualised CPU
 */
public class CPU {

	double width;
	double height;
	CPUVisualisation vis;

	// Items needed to set widths
	public GeneralComponent controlUnit;
	public GeneralComponent programCounter;
	public GeneralComponent instructionMemory;
	public GeneralComponent register;
	public GeneralComponent mainMemory;
	public GeneralComponent ir;
	public GeneralComponent plusFour;
	public GeneralComponent signExtender;
	public GeneralComponent muxAdder;
	public GeneralComponent shiftLeft;
	public GeneralComponent shiftLeftIR;
	public ALU alu;
	public ALU adder;

	public CustomWire irToRegister1;
	public CustomWire irToRegister2;
	public CustomWire irToRegister3;
	public CustomWire pcToPlusFour;
	public CustomWire muxToPC;
	public CustomWire irToSignExtender;
	public CustomWire signExtenderToALU;
	public CustomWire dataMemoryToRegisters;
	public CustomWire aluToRegisters;
	public CustomWire signExtenderToShift;
	public CustomWire plusFourToMux;
	public CustomWire aluToMux;
	public CustomWire irToShift;
	public CustomWire shiftToMux;
	public CustomWire plusFourToMuxWithShift;
	public CustomWire registertoMux;

	public ConnectorWire adderToMux;
	public ConnectorWire plusFourToAdder;
	public ConnectorWire shiftToAdder;
	public ConnectorWire PCToIM;
	public ConnectorWire aluToMemory;
	public ConnectorWire registerToALU1;
	public ConnectorWire registerToALU2;
	public ConnectorWire codeMemoryToIR;

	public InstructionsWindow previousInstructions;

	public Group generalWires;
	public Group allItems;
	public Group components;

	public GeneralComponent info;

	public AnimationProcessor animationProcessor;
	private Timer t = new Timer(true);

	/**
	 * Sets up the CPU
	 * 
	 * @param vis
	 *            The visualisation to use
	 * @param width
	 *            The width of the window
	 * @param height
	 *            The height of the window
	 */
	public CPU(CPUVisualisation vis, double width, double height) {
		this.width = width;
		this.height = height;
		this.vis = vis;
		this.animationProcessor = new AnimationProcessor(this);
	}

	public void showText(String text, double time) {
		showText(text, time, true);
	}

	/**
	 * Shows a caption for a specified time
	 * 
	 * @param text
	 *            The text to show
	 * @param time
	 *            How long the caption should be displayed
	 */
	public void showText(String text, double time, boolean fadeOut) {
		if (time < 250)
			return;

		double fadeTime = time / 4;
		double delayTime = time / 2;

		FadeTransition ft = new FadeTransition(Duration.millis(fadeTime), info);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.setCycleCount(1);
		if (fadeOut) {
			ft.setOnFinished(event -> t.schedule(new TimerTask() {
				@Override
				public void run() {
					FadeTransition fo = new FadeTransition(Duration.millis(fadeTime), info);
					fo.setFromValue(1);
					fo.setToValue(0);
					fo.setCycleCount(1);

					fo.play();
					cancel();
				}
			}, (int) delayTime));
		}

		Platform.runLater(() -> {
			info.setLabel(text);
			ft.play();
		});
	}

	/**
	 * Draws the cpu, sets up each component along with each wire and adds them to the visualisation
	 * Also sets up the tool tips for each component
	 */
	public void drawCPU() {
		components = new Group();

		info = new GeneralComponent(vis, "Label");
		info.setOpacity(0);
		info.getComponentShape().getStyleClass().add("cpu-info-label-outer");
		info.getComponentLabel().getStyleClass().add("cpu-info-label-text");

		controlUnit = new GeneralComponent(vis, "Controller");
		programCounter = new GeneralComponent(vis, "PC");
		instructionMemory = new GeneralComponent(vis, "Code Memory");
		register = new GeneralComponent(vis, "Registers");
		alu = new ALU(vis, "ALU");
		mainMemory = new GeneralComponent(vis, "Data Memory");
		ir = new GeneralComponent(vis, "IR");
		plusFour = new GeneralComponent(vis, "+4");
		signExtender = new GeneralComponent(vis, "+");
		muxAdder = new GeneralComponent(vis, "mux");
		shiftLeft = new GeneralComponent(vis, "<");
		adder = new ALU(vis, "+");
		shiftLeftIR = new GeneralComponent(vis, "<");

		previousInstructions = new InstructionsWindow(animationProcessor);

		controlUnit.setTooltip("The control unit (CU) is a component of a computer's central processing unit (CPU) that directs operation of the processor. It tells the computer's memory, arithmetic/logic unit and input and output devices how to respond to a program's instructions.");

		programCounter.setTooltip("A program counter is a register in a computer processor that contains the address (location) of the next instruction that will be executed. As each instruction gets fetched, the program counter increases its stored value by 1.");

		register.setTooltip("In computer architecture, a processor register is a small amount of storage available as part of a digital processor, such as a central processing unit (CPU). Such registers are typically addressed by mechanisms other than main memory and can be accessed faster.");

		alu.setTooltip("An arithmetic logic unit (ALU) is a digital circuit used to perform arithmetic and logic operations. It represents the fundamental building block of the central processing unit (CPU) of a computer. Modern CPUs contain very powerful and complex ALUs. In addition to ALUs, modern CPUs contain a control unit (CU).");

		mainMemory.setTooltip("PC memory is more easily referred to as RAM (Random Access Memory) and performs very different tasks to storage memory which is found on a hard drive or SSD. RAM, in the form of a memory module, is a component in your computer which enables short-term or temporary data access.");

		ir.setTooltip("In computing, an instruction register (IR) is the part of a CPU's control unit that stores the instruction currently being executed or decoded.");

		instructionMemory.setTooltip("Instruction memory, this is where the instructions of the program are stored.");

		signExtender.setTooltip("Sign extension is the operation, in computer arithmetic, of increasing the number of bits of a binary number while preserving the number's sign (positive/negative) and value.");

		adder.setTooltip("Computes the sum of 2 values together and outputs the result.");

		muxAdder.setTooltip("A multiplexer (or mux) is a device that selects one of several analog or digital input signals and forwards the selected input into a single line.");

		shiftLeftIR.setTooltip("A shift left logical of two positions moves each bit to the left by two.");
		shiftLeft.setTooltip("A shift left logical of two positions moves each bit to the left by two.");

		plusFour.setTooltip("Used to Change the value of the program counter by +4.");

		generalWires = new Group();

		PCToIM = programCounter.horizontalLineTo(instructionMemory, true, false, 0);
		aluToMemory = alu.horizontalLineTo(mainMemory, true, false, 0.3);
		registerToALU1 = register.horizontalLineTo(alu, true, false, -0.3);
		registerToALU2 = register.horizontalLineTo(alu, true, false, 0.3);
		codeMemoryToIR = instructionMemory.horizontalLineTo(ir, true, false, 0);

		plusFourToAdder = plusFour.horizontalLineTo(adder, true, false, 0);
		shiftToAdder = shiftLeft.horizontalLineTo(adder, true, false, 0);
		adderToMux = adder.horizontalLineTo(muxAdder, true, false, 0);

		irToRegister1 = new CustomWire(0, 0);
		irToRegister2 = new CustomWire(0, 0);
		irToRegister3 = new CustomWire(0, 0);
		pcToPlusFour = new CustomWire(0, 0);
		muxToPC = new CustomWire(0, 0);
		irToSignExtender = new CustomWire(0, 0);
		signExtenderToALU = new CustomWire(0, 0);
		dataMemoryToRegisters = new CustomWire(0, 0);
		aluToRegisters = new CustomWire(0, 0);
		aluToMux = new CustomWire(0, 0);
		shiftToMux = new CustomWire(0, 0);
		irToShift = new CustomWire(0, 0);
		signExtenderToShift = new CustomWire(0, 0);
		muxToPC = new CustomWire(0, 0);
		plusFourToMux = new CustomWire(0, 0);
		plusFourToMuxWithShift = new CustomWire(0, 0);
		registertoMux = new CustomWire(0, 0);

		generalWires.getChildren().addAll(PCToIM, aluToMemory, registerToALU1, registerToALU2, codeMemoryToIR, plusFourToAdder, shiftToAdder, adderToMux);

		Group complexWires = new Group();

		complexWires.getChildren().addAll(irToRegister1, irToRegister2, irToRegister3, pcToPlusFour, muxToPC, irToSignExtender, signExtenderToALU, dataMemoryToRegisters, aluToRegisters, signExtenderToShift, plusFourToMux, aluToMux, irToShift, shiftToMux, plusFourToMuxWithShift, registertoMux);

		components.getChildren().addAll(register, instructionMemory, alu, mainMemory, programCounter, ir, plusFour, signExtender, shiftLeft, adder, muxAdder, shiftLeftIR);

		allItems = new Group();
		allItems.getChildren().addAll(components, generalWires, complexWires, info, previousInstructions);
		vis.add(allItems);
	}

	/**
	 * Redraws every component and wire, used when the window is resized
	 */
	public void resizeShapes() {
		double width = vis.getWindowWidth();
		double height = vis.getWindowHeight();

		programCounter.setAttrs(width * 0.06, height * 0.5, width * 0.05, height * 0.25);
		instructionMemory.setAttrs(width * 0.15, height * 0.5, width * 0.16, height * 0.25);
		ir.setAttrs(width * 0.35, height * 0.5, width * 0.05, height * 0.25);
		signExtender.setAttrs(width * 0.5, height * 0.8, width * 0.05, height * 0.05);
		register.setAttrs(width * 0.48, height * 0.5, width * 0.16, height * 0.25);
		alu.setAttrs(width * 0.7, height * 0.5, width * 0.065, height * 0.25);
		adder.setAttrs(width * 0.74, height * 0.33, width * 0.03, height * 0.15);
		shiftLeft.setAttrs(width * 0.645, height * 0.43, width * 0.02, height * 0.07);
		mainMemory.setAttrs(width * 0.8, height * 0.65, width * 0.16, height * 0.25);
		muxAdder.setAttrs(width * 0.82, height * 0.28, width * 0.03, height * 0.15);
		plusFour.setAttrs(width * 0.415, height * 0.32, width * 0.04, height * 0.1);
		shiftLeftIR.setAttrs(width * 0.48, height * 0.40, width * 0.02, height * 0.07);
		info.setAttrs(width * 0.06, height * 0.09, width * 0.88, height * 0.1);
		previousInstructions.setAttrs(0 - (width * 0.02), height * 0.78, width * 0.33, height * 0.24);

		ObservableList<Node> wires = generalWires.getChildren();

		for (Node wire : wires) {
			((ConnectorWire) wire).updateLine();
		}

		registertoMux.drawLine(register.getLayoutX() + register.getShapeWidth(), register.getLayoutY() + register.getShapeHeight() * 0.2, new CustomLine(width * 0.04, CustomLine.Direction.RIGHT), new CustomLine(height * 0.049, CustomLine.Direction.UP), new CustomLine(width * 0.11, CustomLine.Direction.RIGHT), new CustomLine(height * 0.102, CustomLine.Direction.UP), new CustomLine(width * 0.03, CustomLine.Direction.RIGHT));

		aluToMux.drawLine(alu.getLayoutX() + alu.getShapeWidth(), alu.getLayoutY() + alu.getShapeHeight() * 0.4, new CustomLine(width * 0.07, CustomLine.Direction.RIGHT), new CustomLine(height * 0.16, CustomLine.Direction.UP));

		plusFourToMuxWithShift.drawLine(plusFour.getLayoutX() + plusFour.getShapeWidth(), plusFour.getLayoutY() + plusFour.getShapeHeight() / 2, new CustomLine(width * 0.2, CustomLine.Direction.RIGHT), new CustomLine(height * 0.06, CustomLine.Direction.UP), new CustomLine(width * 0.165, CustomLine.Direction.RIGHT));

		plusFourToMux.drawLine(plusFour.getLayoutX() + plusFour.getShapeWidth(), plusFour.getLayoutY() + plusFour.getShapeHeight() / 2, new CustomLine(width * 0.2, CustomLine.Direction.RIGHT), new CustomLine(height * 0.06, CustomLine.Direction.UP), new CustomLine(width * 0.165, CustomLine.Direction.RIGHT));

		irToRegister1.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() / 2, new CustomLine(width * 0.035, CustomLine.Direction.RIGHT), new CustomLine(height * 0.1, CustomLine.Direction.UP), new CustomLine(width * 0.044, CustomLine.Direction.RIGHT));

		irToRegister2.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() / 2, new CustomLine(width * 0.035, CustomLine.Direction.RIGHT), new CustomLine(height * 0.06, CustomLine.Direction.UP), new CustomLine(width * 0.044, CustomLine.Direction.RIGHT));

		irToRegister3.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() / 2, new CustomLine(width * 0.035, CustomLine.Direction.RIGHT), new CustomLine(height * 0.02, CustomLine.Direction.UP), new CustomLine(width * 0.044, CustomLine.Direction.RIGHT));

		irToSignExtender.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() * 0.5, new CustomLine(width * 0.035, CustomLine.Direction.RIGHT), new CustomLine(height * 0.2, CustomLine.Direction.DOWN), new CustomLine(width * 0.065, CustomLine.Direction.RIGHT));

		irToShift.drawLine(ir.getLayoutX() + ir.getShapeWidth(), ir.getLayoutY() + ir.getShapeHeight() * 0.5, new CustomLine(width * 0.035, CustomLine.Direction.RIGHT), new CustomLine(height * 0.185, CustomLine.Direction.UP), new CustomLine(width * 0.045, CustomLine.Direction.RIGHT));

		shiftToMux.drawLine(shiftLeftIR.getLayoutX() + shiftLeftIR.getShapeWidth(), shiftLeftIR.getLayoutY() + shiftLeftIR.getShapeHeight() * 0.5, new CustomLine(width * 0.035, CustomLine.Direction.RIGHT), new CustomLine(height * 0.125, CustomLine.Direction.UP), new CustomLine(width * 0.285, CustomLine.Direction.RIGHT));

		signExtenderToALU.drawLine(signExtender.getLayoutX() + signExtender.getShapeWidth(), signExtender.getLayoutY() + signExtender.getShapeHeight() * 0.5, new CustomLine(width * 0.105, CustomLine.Direction.RIGHT), new CustomLine(height * 0.126, CustomLine.Direction.UP), new CustomLine(width * 0.046, CustomLine.Direction.RIGHT));

		signExtenderToShift.drawLine(signExtender.getLayoutX() + signExtender.getShapeWidth(), signExtender.getLayoutY() + signExtender.getShapeHeight() * 0.5, new CustomLine(width * 0.105, CustomLine.Direction.RIGHT), new CustomLine(height * 0.32, CustomLine.Direction.UP));

		pcToPlusFour.drawLine(programCounter.getLayoutX() + programCounter.getShapeWidth(), programCounter.getLayoutY() + programCounter.getShapeHeight() / 2, new CustomLine(width * 0.015, CustomLine.Direction.RIGHT), new CustomLine(height * 0.22, CustomLine.Direction.UP), new CustomLine(width * 0.29, CustomLine.Direction.RIGHT));

		muxToPC.drawLine(muxAdder.getLayoutX() + muxAdder.getShapeWidth(), muxAdder.getLayoutY() + muxAdder.getShapeHeight() / 2, new CustomLine(width * 0.015, CustomLine.Direction.RIGHT), new CustomLine(height * 0.12, CustomLine.Direction.UP), new CustomLine(width * 0.835, CustomLine.Direction.LEFT), new CustomLine(height * 0.39, CustomLine.Direction.DOWN), new CustomLine(width * 0.03, CustomLine.Direction.RIGHT));

		dataMemoryToRegisters.drawLine(mainMemory.getLayoutX() + mainMemory.getShapeWidth(), mainMemory.getLayoutY() + mainMemory.getShapeHeight() * 0.8, new CustomLine(width * 0.015, CustomLine.Direction.RIGHT), new CustomLine(height * 0.1, CustomLine.Direction.DOWN), new CustomLine(width * 0.52, CustomLine.Direction.LEFT), new CustomLine(height * 0.23, CustomLine.Direction.UP), new CustomLine(width * 0.025, CustomLine.Direction.RIGHT));

		aluToRegisters.drawLine(alu.getLayoutX() + alu.getShapeWidth(), alu.getLayoutY() + alu.getShapeHeight() * 0.8, new CustomLine(width * 0.015, CustomLine.Direction.RIGHT), new CustomLine(height * 0.265, CustomLine.Direction.DOWN), new CustomLine(width * 0.325, CustomLine.Direction.LEFT), new CustomLine(height * 0.23, CustomLine.Direction.UP), new CustomLine(width * 0.025, CustomLine.Direction.RIGHT));

	}

	/**
	 * Shutsdown the executor service and task
	 */
	public void closeAllThreads() {
		animationProcessor.shutdown();
		t.cancel();
	}
}