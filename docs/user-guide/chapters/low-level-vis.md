# Low-level Visualisations #

## CPU Visualisation ##
The CPU visualisation window allows you to view the processes involved when fetching, decoding and executing different types of MIPS instructions as the program is being executed. The window shows a block diagram containing different components of the CPU, for example the ALU along with others such as the main memory and program counter. When instructions are being executed, text will show at the top of the window, containing useful information about different stages of execution, for example that the values of two registers are being compared using the ALU.

To use the CPU visualisation, make sure you set a low clock speed. Animations will only be shown at a clock speed of less than 2Hz. Although to effectively make use of the information, it's recommended to set a very low clock speed, such as 0.05Hz or lower. Alternatively, the single step feature is very useful when combined with the CPU visualisation and can be used to see how each instruction is executed one by one.

When viewing the CPU visualisation, you will notice several things:

- Components highlighting: This shows when different components are performing operations or are about to send data/signals to other components.
- Data moving: These small circles show when data/signals are moving between components of the CPU, for example sending data to the ALU for comparison.
- Replay window: This window contains the previous 10 instructions that have been executed, the "Replay" button can be used to replay the instruction if you missed anything.

You can also view more information about each component by hovering over it with your mouse, a tooltip will show with a detailed description about the role of the component. This can be used to further your knowledge about how each component of the CPU works.


![CPU Visualisation](segments/cpu-visualisation.png)


## Pipeline Visualisation ##
![Pipeline View](segments/pipeline.png)

The pipeline visualisation window allows you to view the contents of the pipeline, as well as the waiting and completed instructions, as the CPU is running. The middle third of the window shows the fetch, decode, and execute portions of the pipeline at each state, including hazards where appropriate (represented as red circles).

Firstly, to use the pipeline view, the CPU must be running and in pipelined mode (to turn on pipelining, go to the `Simulation` menu and make sure `Toggle CPU Pipelining` is selected). Once running, the window will start to fill up from left to right with instructions being processed.

Let's look at the components in the control bar at the bottom of the window:

- `Follow` checkbox: allows you to toggle whether or not you want to snap to the most recent stage of the pipeline.
- Left/Right buttons: allows you to move backwards/forwards cycles in the pipeline (this can also be achieved by using the left and right keys on your keyboard).
- `Goto: ` field: allows you to jump to a specified cycle (indicated at the bottom of each stage of the pipeline) by entering the cycle number, e.g. 67, and then pressing enter. If you enter a number greater than the number of cycles, then it will jump to the last cycle.

You can view information about instructions in the window by hovering over them with your mouse. Summary information about the instruction will appear at the bottom of a screen. If you hover a hazard, the window will tell you what type of hazard was encountered at this time.

You can highlight all occurrences of a particular instruction by clicking on it. This can be useful to track the state of the instruction through the pipeline and to spot past/future occurrences of the instruction.

A maximum of 10,000 pipeline stages can currently be displayed (this is to mitigate problems with infinite loops etc.)

## Memory View ##
![Memory View](segments/memView.png)
This component is still under development and so does not bear any functionality at this time.

