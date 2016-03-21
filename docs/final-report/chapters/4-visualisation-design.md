#Visualisation Design#
$\TODO{write section}$

- General

    - Windows resize automatically

    - Themes/Accessibility

- CPU Visualisation
    - Red dot contrasting. Moves along the wires as signals.

    - Components highlight when they are performing operations.

    - Information at the top to describe the animations

    - Tooltips to explain functionality

    - Block diagram abstract view of circuit board

        - Lines at right angles

        - Components are different shapes based of their task

    - Animations are in sync with editor, and work when resizing

- Pipeline view

    - Each instruction has it's own colour, so that it is easy to track between time sections.
        - Easy to see when an instruction is repeated
        - Click to see every occurrence
        - Hover to see more information

    - Bubbles are circularly shaped to distinguish against instructions. Coloured red to indicate a hazard/anomaly

    - Follows the pipeline so everything stays in sync. Can use the arrows to see previous instructions

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
