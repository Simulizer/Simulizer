# Low-level Visualisations #

## CPU Visualisation ##
$\TODO{take picture}$
<!-- TODO take a nice picture of the CPU visualisation
![](segments/cpu-vis.png)
-->


## Pipeline Visualisation ##
$\TODO{take picture}$
<!-- TODO take a nice picture of the pipeline window
![](segments/pipeline.png)
-->
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
$\TODO{take picture}$
<!-- TODO take a nice picture of the memory view window
![](segments/memory-view.png)
-->
