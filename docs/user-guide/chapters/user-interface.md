# User Interface #
The user interface is designed to be as configurable as possible, so that the application can fulfil your needs. Don't need to visualise the internals of the CPU? Just close the CPU visualiser. Need to make the editor a bit bigger? Then resize the editor. It's very simple.

## Menu Bar ##
The menu bar contains a collection of useful controls organised for easy of use. Below describes the tree structure of the menu so that is clear what each menu item is for.

- **File**: Contains the standard controls found in most applications.

    - **New** (`CTRL+N`): Creates a new blank program and opens the Editor Internal Window.

    - **Open** (`CTRL+O`): Opens an existing program and puts it in the Editor.

    - **Save** (`CTRL+S`): Saves the current program to the file loaded in the Editor.

    - **Save as**: Saves the current program to a new file.

    - **Options**: Opens the Options Internal Window.

    - **Exit**: Exits Simulizer.

- **Edit**: Contains standard controls found in most text editor.

    - **Cut** (`CTRL+X`): Cuts text from the editor.

    - **Copy** (`CTRL+C`): Copies text from the editor.

    - **Paste** (`CTRL+V`): Pastes text in the editor.

    - **Find** (`CTRL+F`): Finds text in the editor.

    - **Go To Line** (`CTRL+G`): Goes to a specified line in the editor.

    - **Insert Breakpoint** (`CTRL+B`): Inserts a Breakpoint for the simulation on the currently selected line in the editor.

    - **Increase Font Size** (`CTRL++`): Increases the font size of the editor.

    - **Decrease Font Size** (`CTRL+-`): Decreases the font size of the editor.

    - **Toggle Word Wrap**: Switches between line wrapping and not.

- **Simulation**: Controls for the simulation of the MIPS processor.

    - **Assemble and Run** (`F5`): Assembles the SIMP Program and (if it is a valid program) executes it. On an invalid program, hints to what went wrong will be displayed in the Editor.

    - **Pause/Resume Simulation** (`F6`): Pauses or Resumes the currently running/paused SIMP program.

    - **Single Step** (`F7`): On a paused SIMP program, this option completes one cycle of the simulated CPU.

    - **End Simulation** (`F8`): Completely ends the simulation and resets the CPU to it's initial state.

    - **Toggle CPU Pipelining**: Switches between the pipelined and non-pipelined CPU.

    - **Set clock speed**: Opens a dialog box so that you can change at what speed the simulated CPU is running at. Note: this is measured in Hertz, and setting this value too high may have performance issue.

- **Windows**: This contains a sub-menu with all the Internal Windows. This allows you to open and close each Internal Windows more easily.

    - **Close All**: Closes all open Internal Windows.

- **Layouts**: Contains a list of all layouts saved in the layouts folder. This allows you to easily switch between different common workspace layouts.

    - **Save Layout**: Saves the current workspace layout to a new file

    - **Refresh Layouts**: Refreshes the list of layouts.

- **Help**: Useful help materials.

    - **Guide**: Opens this user guide.

    - **Syscall Reference**: Opens an Internal Window describing what each syscall is for.

    - **Instruction Reference**: Opens an Internal Window describing what each instruction is for.

    - **Register Reference**: Opens an Internal Window describing the common use for each register.

    - **Editor Shortcuts**: Opens the web browser to a page describing keyboard shortcuts for the Editor.

## Internal Windows ##
Each pane inside the application is called an Internal Window. This section will give a brief description of what all the different Internal Windows are for, and why you might want to use them.

### CPU Visualisation ###
CPU visualisation is for demonstrating how the MIPS processor fetches, decodes and executes assembly instructions. To use this view, you must set the clock speed to below 2Hz [(see clock speed)](#clockspeed).

![CPU Visualisation executing an I-Type instruction](segments/cpu-visualisation.png){width=60%}

For more information about the CPU Visualiser other low level visualisation see [Low Level Visualisations](#low-level)

### Editor ###
The editor is the place to write assembly code. The program that is contained in the Editor is the one to be run on the MIPS processor. You will most likely want to keep this window open (as without it, you can't run any assembly code).

![Editor running Bubble Sort](segments/editor.png){ width=60% }

### High Level Visualisation ###
The High Level Visualisation window is where visualisations from the [annotations](#annotations) are displayed. There purpose is to demonstrate what your SIMP program is actually doing from a more human understandable view.

![High Level Visualiser with Towers of Hanoi open](segments/high-level.png){ width=60% }

For more information about the different data structures Simulizer can visualise, see [High Level Visualisation](#high-level)

### Labels ###
![Labels window](segments/labels-window.png){ width=40% }

The labels window allows you to view a quick outline of the labels in your program. By clicking on a row in the table, it will jump to the line where the label is defined. You can move to the next/previous occurrence of the label by clicking on the `Next`/`Previous` buttons. The `Select All` button will select each occurrence of the selected label, allowing you to easily see its usage.

### Program I/O ###
The program I/O window provides a command line interface to communicate with your SIMP programs.

![Program I/O](segments/program-io.png){ width=45% }

This window has a three different tabs designed to keep the different I/O streams separate. The first tab is Standard which is where SIMP programs interact. The second tab outputs runtime errors in the SIMP program. The third tab is where the annotations communicate through, and so any log annotations will write to.

### Memory View ###
![Memory View](segments/memView.png){ width=60% }
This section is currently still in development.

### Options ###
The options window allows you to configure different aspects of Simulizer. These settings are laid out in a tree like fashion to make finding each setting easier.

![Options](segments/options.png){ width=60% }

The full settings tree is printed out below.

- **Settings**

    - **Debug Menu**: Show debug menu in the Menu Bar

    - **Window**

      	- **Width**: Default window width

      	- **Height**:Default window height

    - **Workspace**

      	- **Default Theme**: The default theme to load

      	- **Default Layout**: The default layout to load

        - **Scale User Interface**

            - **Allow autosizing of Internal Windows**: Resize all Internal Windows when the main window resizes

            - **Delay before resize**: How long to wait until the Internal Windows resize

      	- **Grid Settings**: Configure when Internal Windows should snap to a grid

          	- **Allow grid snapping**: Enables/Disable snapping Internal Windows to a grid

          	- **Horizontal Lines**: Number of horizontal gridlines to snap to

          	- **Vertical Lines**: Number of vertical gridlines to snap to

          	- **Sensitivity**: How close the window needs to be to the gridline before it snaps

          	- **Delay before snap**: How long to wait until the window snaps

        - **Lock to main window**: Stops InternalWindows from exiting the Main Window

    - **CPU Simulation**

        - **Default CPU cycle frequency**: Default number of cycles (runs of fetch+decode+execute) per second (Hz)

        - **Use Pipelined CPU**: Sets whether to use the pipelined CPU or not

    - **Editor**

      	- **Font family**: Font family (optional). Supports all installed monospace fonts, use single quotes for names with spaces. Separate multiple choices with commas

      	- **Font size**: Font size in px

      	- **Initial file**: Path to a file to load at startup (optional)

      	- **Scroll speed**: Scroll speed

      	- **Soft tabs**: Soft tabs

      	- **Color theme**: Name of the color scheme to load. Supported: (prefix: /ace/theme/) default, high-viz, monokai, ambiance, chaos, tomorrow_night_eighties, predawn, flatland

      	- **User control during execution**: Whether the user is allowed to scroll freely during execution of a program

      	- **Vim mode**: Vim keybindings for the editor

      	- **Wrap long lines**: Wrap long lines

      	- **Continuous Assembly**: Repeatedly assemble the program behind the scenes as you type, and highlight problems in the editor

      	- **Continuous Assembly Period**: The time between refreshing the highlighted problems by assembling the program (milliseconds)

    - **Splash Screen**

      	- **Show splash screen**: Toggles whether the splash screen is shown on launch

      	- **Display Time (in ms)**: Minimum time the splash screen should be shown for

      	- **Splash Screen Width**: Width of the splash screen

      	- **Splash Screen Height**: Height of the splash screen

    - **Logger**

      	- **Emphasise Logger**: Toggles whether to emphasise logger when requesting input

      	- **Font Size**: Font size for the Program I/O

    - **High Level Visualiser**

      	- **Automatically Open High Level Visualiser**: Automatically Open High Level Visualiser when a new visualisation is shown

### Pipeline View ###
![Pipeline View](segments/pipeline.png){ width=60% }

Here you can see the contents of the pipeline during each CPU cycle. To view the contents of this window, the CPU must be *running* and in *pipelined* mode. Once the simulation is running, you will see the screen start to fill up (from left to right) with instructions. The numbers at the bottom indicate which CPU cycle is shown in that column.

The two horizontal lines in the center separate the pipeline instructions from the waiting and completed instructions, as indicated by the labels at the left of the window. The red circles indicate hazards.

The control bar at the bottom of the window has the following features:

1. `Follow` checkbox: when this is selected, the window will snap to the most recent cycle, otherwise the window will keep showing what it currently shows.
2. Left/right arrows: clicking the left and right arrows will move backwards/forwards cycles. You can also move backwards and forwards cycles by pressing the left and right arrow keys.
3. `Go to` field: you can enter a cycle number here, e.g. `56` and the window will snap to that cycle, showing it as the leftmost column.
4. Information label: when hovering over an instruction or a hazard, information about that instruction/hazard will be displayed in this label.  

Clicking on an instruction will highlight all of its occurrences. For more information about the pipeline visualisation, see [Pipeline Visualisation](#pipeline-view)

### Registers ###
![Register View](segments/registers.png){ width=60% }

The Registers window provides a realtime view of the current value stored in each register. This value can be interpreted in three different ways (unsigned integer, signed integer and hexadecimal). To switch between these interpretations, right click on the column heading and select the interpretation you want.

## Layouts ##
Layouts determine the configuration that all the Internal Windows are in. They allow you to quickly switch between different arrangements to optimise your workflow.

### Loading a Layout ###
![Loading a Layout](segments/layout.png){ width=60% }

Simulizer includes many layouts for you to try. To change to one of these layout, go to the Menu Bar and click Layouts. This will bring up a list of all the layouts that you can choose. Just click one and the internal windows will rearrange themselves.

### Saving a Layout ###
If none of the included layouts are up to your standards then why not make your own. Add/Remove and rearrangement the Internal Windows until it is in a configuration that you are happy with. You can then save the layout by clicking Layouts $\to$ Save Layout. Enter a name for this new layout and click the save button. That new layout should show up on the Layouts drop down menu.

## Themes ##
Themes provide a way to change the visual appearance of the software.  This feature is currently in beta (and therefore requires the debug menu). A theme is just a folder in the `themes` folder. It contains some CSS files to define the theme, as well as a `theme.json` file which contains some meta data about the theme (like theme name, author, version, etc.). To switch between themes, navigate to Debug $\to$ Themes and click on your preferred theme.
