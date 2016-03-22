#Visualisation Design#
$\TODO{write section}$

- General

    - Windows resize automatically

    - Themes/Accessibility

CPU Visualisation
=================
The CPU Visualisation is one of the most visual parts of the system, and allows to user to learn a great deal about how the CPU is working on instructions at a low level. For this reason it was crucial that a great of detail and thought was put into the design to allow ease of use for the user. 

When viewing the visualisation, the separate components can easily be seen against the light background and allow the user to quickly get a grasp on how the CPU works. Various components are different sizes to highlight their relative importance, for example the mux unit is small, as this performs simple operations, whereas the registers are large. This allows the user to quickly get an idea of the importance of various components. Different shapes were also used, for example the ALU and adder components are the typical shape shown in existing resources, which allows the user to quickly understand the function of various components. Wires can also be seen on the diagram and link various components together, these wires are easy to see and are all organised and positioned to provide a clean design. Distinct arrowheads can also be seen at the end of each wire, which signifies the direction of flow for data, these are fairly large and allow them to easily be seen by the user.

Tooltips were used extensively in the CPU block diagram to give more information to the user. These tooltips show on mouseover, allowing them to only show when needed. The tooltips provide detail about the function for each component, and allow the user to learn more about why different components are needed, for example that the program counter is used to store the address of the next instruction. The tooltips have a dark background which allows the user to clearly differentiate them from components and allow the information to be read easily.

Once the user starts running the simulation, they will start to see red circles moving along the wires in the block diagram. These red circles are easy to see against the blue wires and show the movement of data/signals through the CPU. The movement of data is also synchronised with the text editor, for example when an instruction is being fetched, the visualisation will animate to show this, and once the editor moves to decode an instruction, the visualisation will also show a decode. The user will also notice components highlighting when the visualisation is running, this shows when a specific component is performing an operation, for example that the ALU is performing a comparison. This works well when combined with the data movement animations and allows the user to see how data is being directed through the CPU.

To provide more information in the visualisation, a rectangular box appears at the top of the visualisation containing different information about the current instruction or stage of the simulation. This box is a different colour to the other CPU components allowing it to be easily distinguished. The box also fades in and out at different points in the simulation, which draws attention to it when required. The information is used to describe instructions and stages, for example that the current value of the program counter is being passed to memory.

Overall the CPU visualisation has many different visual aspects to enhance the user's knowledge and allow them to easily observe how a CPU functions inside a computer. Each instruction in the editor is visualised to show detailed execution information and allows the user to easily learn more about it's executed along with the functionality of different components using the informative tooltips provided.

Pipeline view
================= 
The pipeline view can be extremely helpful when the user wants to learn about pipelining, which is extremely difficult to learn about using existing resources. The pipeline view is very easy to use, and simply requires that the simulation is being run in pipeline mode, which the user can switch easily.

When the user is observing the pipeline view, they will immediately notice the different colours of various blocks. These colours are used to show different instructions across different cycles in the pipeline. The different colours allow the user to very quickly see how each instruction is passing through the pipeline. The user can also click on a single instruction, this will highlight the instruction across all cycles and also jump to the correct line inside the editor. More information about each instruction can also be seen by hovering over it with the mouse, data is displayed such as the instruction name, type, address and line number. 

Bubbles are shown as red circles in the pipeline view, the colour and shape used allow the user to clearly distinguish them from regular instructions. By hovering over each bubble, the user can see more information about the hazard such as whether it's a control or read after write hazard.

As the program is running, the pipeline view will start to populate and move along in real time as the program continues to run. This can easily be disabled with the use of a check box to suit the user's preference. The user can also the left and right arrows to move back to a previous time in the pipeline, or alternatively the user can enter a cycle number to jump to, allowing fast movement along the pipeline.

Overall the pipeline view provides great detail and visual aspects to allow the user to quickly and easily learn more about pipelining, which is extremely difficult with existing resources. The user can easily see different stages of the pipeline such as fetch, decode and execute as well as see how instructions are moving through the different stages, as well as the pipeline hazards that occur.

- Editor

    - Syntax highlighting

    - Gutter colours for different lines to show the different stages of the CPU cycle

    - Errors have a red box around them, with a cross on the line. More information about the error on hover of cross

- Program IO

    - Notifies user with a red dot when something is posted.

    - Emphasise when the program is expecting an input.

- High Level

    - If the High Level Visualisation window is opened midway through a simulation, it can render the visuals for the current execution.

    - Hanoi
        - Animations between swaps.

        - Discs are different colours/sizes to make it more obvious than disc 1 - n


    - Lists
        - Swaps animations

        - Emphasise list item being analysed

        - Markers on list
