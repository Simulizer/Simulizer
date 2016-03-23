## End to end tests ##

Each of these tests will start with all windows closed.

### Editor ###

**Test ID**: TC-E2E1  

- **Description**: User opens a new file in the code editor, writes some valid code, and then saves their code to file.
- **Input**: `.text; main:; li $v0 10; syscall`
- **Action**:
  1. Open the `Editor` window.
  2. `Ctrl + n`.
  3. Physically enter the input text into the code editor.
  4. `Ctrl + s`.
  5. Choose the `code` folder as the destination and enter the file name as `NewFileSaveTest.s`, then click save.
- **Expected**:
  * The title text at the top of the editor should change to `Untitled` and the contents of the editor should be cleared.
  * The text should be syntax highlighted as the user types.
  * After typing a single character, the editor should change its title to `Editor - Untitled *`.
  * A save dialog should be shown when the user attempts to save.
  * The text should be saved in a file called `NewFileSaveTest.s` in the code folder on the user's computer.
- **Actual**: As expected (see below).

![](segments/end-to-end/editor-new-file-save-test.png){ width=40% }

![](segments/end-to-end/editor-new-file-save-test-dialog.png){ width=40% }

![](segments/end-to-end/editor-new-file-save-vim.png){ width=40% }

**Test ID**: TC-E2E2

- **Description**: Test general usability, e.g. open an existing file, scroll around the code, fold a section, etc.
- **Input**: `bubblesort.s`
- **Action**:
  1. Open the `Editor` window.
  2. `Ctrl + o`.
  3. Select `bubblesort.s`.
  4. Scroll down to line 108.
  5. Click the fold arrow next to the `108` line number.
  6. Enter `potato` on line 104.
  7. Edit the text on line 100 to say `# my new comment`.
  8. Place the cursor on line 95 then press `Ctrl + b`.
  9. `Ctrl + +`
  10. `Ctrl + -`
  11. `Ctrl + g` then `55`.
  12. `Ctrl + f` then `text`.
  13. Select `read_input` on line 14 then `Ctrl + x`. Then move the cursor onto line 15 and press `Ctrl + v`. Select `read_input` again and press `Ctrl + c`, then move the cursor to the end of line 14 and press `Ctrl + v`.
  14. `Edit` $\to$ `Toggle Word wrap`.
- **Expected**:
  * Open dialog shown.
  * Text from `bubblesort.s` is loaded into the editor.
  * After clicking the fold button, lines 108-134 should be collapsed and the line numbers should show 108 then 135.
  * A red box should appear around `potato`.
  * The text size should increase/decrease when pressing `Ctrl + +` or `Ctrl + -`.
  * Editor should show a dialog allowing the user to enter `55`, and the editor should jump to line 55 after the user confirms entry.
  * Editor should show a dialog allowing the user to enter a search term. The editor should jump to the next occurrence of the search term.
  * The editor should wrap long lines appropriately with line wrap mode on.
- **Actual**: As expected (see below).

![](segments/end-to-end/editor-open-dialog.png){ width=40% }

![](segments/end-to-end/editor-fold.png){ width=40% }

![](segments/end-to-end/editor-edit.png){ width=40% }

**Test ID**: TC-E2E3

- **Description**: Line number tracking while simulation is running.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open `count.s` in the editor.
  3. Press `F5`.
  4. Hover over the highlighted line number.
- **Expected**:
  * Editor becomes read-only and the line number of the current line is highlighted.
  * Hovering over the highlighted line number should show the current stage of execution, e.g. `decoding`.
- **Actual**: As expected (see below).

![](segments/end-to-end/editor-line-hover.png){ width=40% }

**Test ID**: TC-E2E4

- **Description**: Open an invalid program, view errors, then fix them.
- **Input**: `count-annotated.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open `count-annotated.s`.
  3. Hover over the line number label on line 18.
  4. Hover over the line number label on line 23.
  5. Add `$a2` to line 18 after `$a1`.
  6. Add `}@` to line 23.
- **Expected**:
  * After opening `count-annotated.s`, the editor should show errors on lines 18 and 23.
  * Hovering over the line number label on line 18 should show an error message about wrong number of operands.
  * Hovering over the line number label on line 23 should show an about an unclosed annotation.
  * After modifying each line, the editor should update to indicate that the line no longer contains an error.
- **Actual**: As expected (see below).

![](segments/end-to-end/editor-line-18-error.png){ width=40% }

![](segments/end-to-end/editor-line-23-error.png){ width=40% }

![](segments/end-to-end/editor-errors-fixed.png){ width=40% }

### Registers window ###

Test ID: TC-E2E5

- **Description**: Check that the information in the `Registers` window updates as the simulation runs.
- **Input**: `.text; main:; li $a0 55`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Registers` window.
  3. Press `Ctrl + n` in the editor.
  4. Type the input text into the editor.
  5. Run the simulation.
- **Expected**:
  * The value of `a0` in the `Registers` window should change from 0 to 55.
- **Actual**: As expected (see below).

![](segments/end-to-end/registers-a0.png){ width=40% }

Test ID: TC-E2E6

- **Description**: Columns in `Registers` window should allow sorting.
- **Input**: `add.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Registers` window.
  3. Open `add.s`.
  4. Run the simulation.
  5. Click the `Register` heading in the `Registers` window, then click it again.
  6. Click the `Unsigned` heading in the `Registers` window, then click it again.
- **Expected**:
  * The data in the table is sorted based on the `Register` column in the `Registers` window, either alphabetically a-z or z-a, then after clicking it again it should sort in the opposite alphabetical order.
  * Same as above but for the `Unsigned` column: it should sort alphabetically/numerically and then sort in the opposite order after the second click.
- **Actual**: The values are sorted correctly, but the values in the value column are sorted in lexicographical order rather than numerical order, see below.

![](segments/end-to-end/registers-sort-left-down.png){ width=40% }

![](segments/end-to-end/registers-sort-left.png){ width=40% }

![](segments/end-to-end/registers-sort-right-down.png){ width=40% }

![](segments/end-to-end/registers-sort-right-up.png){ width=40% }

Test ID: TC-E2E7

- **Description**: Registers window should allow the user to view the values of the registers as unsigned, signed, or in hexadecimal.
- **Input**: `add.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Registers` window.
  3. Open `add.s`.
  4. Run the simulation.
  5. Right click on the right-hand column and click `Signed`.
  6. Right click on the right-hand column and click `Hexadecimal`.
  7. Right click on the right-hand column and click `Unsigned`.
- **Expected**:
  * The right-hand column should show the value of the register in the selected form.
- **Actual**: As expected (see below).

![](segments/end-to-end/registers-signed.png){ width=40% }

![](segments/end-to-end/registers-hex.png){ width=40% }

### Labels window ###

Test ID: TC-E2E8

- **Description**: The `Labels` window should show all labels present in the code, and only those present in the code.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Labels` window.
  3. Open `count.s`.
- **Expected**:
  * The `Labels` window should show the labels:
    - END: 20
    - LOOP: 12
    - main: 8
    - mystr: 5
- **Actual**: As expected (see below).

![](segments/end-to-end/labels-correct.png){ width=40% }

Test ID: TC-E2E9

- **Description**: The `Labels` window should update labels as the user types.
- **Input**: `count.s`.
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Labels` window.
  3. Open `count.s`.
  4. Change the label on line 12 from `LOOP` to `LOOPER`.
  5. Delete line 20.
  6. Undo the deletion, e.g. `Ctrl + z`.
- **Expected**:
  * `LOOP` should change to `LOOPER` in the `Registers` window.
  * The row with `END` should be removed from the table after deleting line 20.
  * The row with `END: 20` should be added after undoing the deletion of line 20.
- **Actual**: As expected.

Test ID: TC-E2E10

- **Description**: The buttons in the `Labels` window should work appropriately.
- **Input**: `binary-search.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Labels` window.
  3. Open `binary-search.s`.
  4. Click on the row with the label `binary_search`.
  5. Click on the `Next` button three times.
  6. Click the `Previous` button three times.
  7. Click the `Select All` button.
- **Expected**:
  * The editor should jump to line 170 after clicking the `binary_search` row.
  * The editor should jump to line 215, then line 221, and then line 137, highlighting the occurrence of the `binary_search` label each time.
  * All occurrences of the label `binary_search` should be highlighted after clicking `Select All`.
- **Actual**: As expected (see below).

![](segments/end-to-end/labels-jump-170.png){ width=40% }

### Layouts ###

Test ID: TC-E2E11

- **Description**: Test each bundled layout.
- **Action**:
  1. Click each layout in the `Layouts` menu option.
- **Expected**:
  * The window should update to show the windows in the corresponding layout.
- **Actual**: As expected.

Test ID: TC-E2E12

- **Description**: Try saving a custom layout.
- **Action**:
  1. Open the following windows: `Editor`, `CPU Visualisation`, and `Program I/O`.
  2. Resize the windows so that the `Editor` is in the bottom left-quarter, the `CPU Visualisation` is in the top-left quarter, and the `Program I/O` takes up the right half.
  3. `Layouts` $\to$ `Save Current Layout`.
  4. Save the layout as `layout-save-test.json`.
  5. `Layouts` $\to$ `Default`.
  6. `Layouts` $\to$ `layout-save-test`.
- **Expected**:
  * A dialog should be shown to allow the user to save the layout in the `layouts` folder.
  * The layout should change back to the default layout when `Default` is clicked.
  * `layout-save-test` should be available in the `Layouts` menu.
  * The layout should switch back to the layout that was specified after clicking `layout-save-test`.
- **Actual**: As expected.

### General window functionality ###
Test ID: TC-E2E13

- **Description**: Windows should resize correctly and reposition when dragged.
- **Action**:
  1. Open the `Editor` window.
  2. Resize the window vertically from the top of the window.
  3. Resize the window vertically from the bottom of the window.
  4. Resize the window horizontally from the left of the window.
  5. Resize the window horizontally from the right of the window.
  6. Move the window around the screen.
  7. Repeat from step 1 for all windows.
- **Expected**:
  * The window should resize and any appropriate contents should also resize.
  * The content should be repositioned in the main window relative to its container window.
- **Actual**: As expected.

Test ID: TC-E2E14

- **Description**: Internal windows should resize according to the size of the main window.
- **Action**:
  1. Open the default layout.
  2. Resize the main window vertically from the top of the window.
  3. Resize the main window vertically from the bottom of the window.
  4. Resize the main window horizontally from the left of the window.
  5. Resize the main window horizontally from the right of the
  6. Repeat from step 1 for each layout.
- **Expected**:
  * Each internal window should resize according to its original size and the size of the main window.
- **Actual**: As expected (see below).

![](segments/end-to-end/default-layout.png){ width=40% }
![](segments/end-to-end/resize-main-window.png){ width=40% }

### Configuration/Options ###
Test ID: TC-E2E15

- **Description**: Test that items in the options window change the program accordingly.
- **Action**:
  1. `File`$\to$`Options`$\to$`Settings`$\to$`Editor`.
  2. Toggle on `Vim mode`.
  3. Close the window.
  4. Choose to restart the program.
  5. Repeat from step 1 and test each possible option.
- **Expected**:
  * The program should update accordingly, e.g. after restarting the editor should be in Vim mode.
- **Actual**: As expected (see below).

![](segments/end-to-end/options-vim-restart.png){ width=40% }

![](segments/end-to-end/options-vim-mode.png){ width=40% }

### Pipeline View ###

Test ID: TC-E2E16

- **Description**: Pipeline view should only show one instruction at a time when CPU is non-pipelined.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open `count.s`
  3. Open the `Pipeline View` window.
  4. `Simulation -> Toggle CPU Pipelining` (off).
  5. Run the simulation.
- **Expected**:
  * The pipeline view should show an instruction in the fetch stage, then red circles for the decode and fetch stages. Then the same instruction should be shown in the decode stage and then the fetch stage, with the other stages being red circles each time.
- **Actual**: As expected (see below).

Test ID: TC-E2E17

- **Description**: Pipeline view should show instructions in the pipeline, as well as the waiting and completed instructions, and display any hazards.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open `count.s`
  3. Open the `Pipeline View` window.
  4. `Simulation -> Toggle CPU Pipelining` (on).
  5. Run the simulation.
  6. After 30 cycles have elapsed, uncheck the `Follow` option.
  7. A few seconds later, check the `Follow` option again.
  8. After 60 cycles have elapsed, pause the simulation.
  9. Use the left and right keys to scroll around.
  10. Enter 9999 in the `Go to` field.
- **Expected**:
  * The pipeline view shows the instructions before, in, and after the pipeline.
  * After unchecking the `Follow` option, the window should stop snapping to the most recent stage in the pipeline and should be stationary.
  * After re-checking the `Follow` option, it should continually snap to the most recent stage in the pipeline.
  * After pausing the simulation, the pipeline view should be stationary.
  * The correct instructions and pipeline stages should be shown when scrolling left and right.
  * After entering 9999 in the `Go to` field, the view should snap to either cycle 9999 or the most recent cycle, and then the view should continue filling up with pipeline stages from left to right.
- **Actual**: As expected (see below).

### Program IO window ###
Test ID: TC-E2E18

- **Description**: Test that messages from the current program are sent to the Program IO window, i.e. check the output of the window.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Program IO` window.
  3. Open `count.s`.
  4. Run the simulation.
- **Expected**:
  * The window should start showing the number 1, 2, 3 and so on.
- **Actual**: As expected (see below).

Test ID: TC-E2E19

- **Description**: Test that text entered by the user is passed to the system, i.e. check the input of the window.
- **Input**: `add2.s`, `4`, `5`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Program IO` window.
  3. Open `add2.s`
  4. Run the simulation and follow any instructions.
- **Expected**:
  * The Program IO window should print `Enter A: `.
  * After entering a number, it should then print `Enter B: `.
  * Finally it should print the (correct) sum of A and B.
- **Actual**: As expected (see below).

### CPU visualisation window ###
Test ID: TC-E2E20

- **Description**: The CPU visualisation should update as the program runs.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `CPU Visualisation` window.
  3. Open `count.s`.
  4. Set the clock speed to the minimum value.
  4. Run the simulation.
- **Expected**:
  * Messages should appear in the CPU visualisation explaining what is currently happening.
  * There should be animations along the bus lines to indicate the movement of data.
- **Actual**: As expected (see below).

Test ID: TC-E2E21

- **Description**: The replay buttons should replay the correct instruction.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `CPU Visualisation` window.
  3. Open `count.s`.
  4. Set the clock speed to the minimum value.
  4. Run the simulation.
  5. After several instructions have been executed, stop the simulation.
  6. Click the replay button for one of the recorded instructions.
  7. Click on each of the other replay buttons.
- **Expected**:
  * After pressing the replay button for a certain instruction, it should replay the animation as it was played when the simulation was running.
- **Actual**: As expected (see below).

### High level visualisation window ###

Test ID: TC-E2E22

- **Description**: Test that the Tower of Hanoi visualisation can be controlled from annotations within the code.
- **Input**: `tower-of-hanoi.s`, `4`.
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Program IO` window.
  3. Open `tower-of-hanoi.s`.
  4. Run the simulation.
  5. When prompted to enter the number of discs, enter `4`.
- **Expected**:
  * The high-level visualisation should open when the `h.show()` command is reached, showing 4 discs on the leftmost peg.
  * The discs should move from peg to peg using animations.
  * The animations should faithfully reflect the algorithm, i.e. it follows the restrictions of the game.
  * The puzzle should end in a solved state.
- **Actual**: As expected (see below).

Test ID: TC-E2E23

- **Description**: Test that the list visualisation (sorting) can be controlled from annotations within the code.
- **Input**: `bubblesort.s`, `5, 3, 6, 7, 2`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Program IO` window.
  3. Open `bubblesort.s`.
  3. Run the simulation.
  4. When prompted to enter the list, enter the list: 5, 3, 6, 7, 2.
- **Expected**:
  * The high-level visualisation should open when the `l.show()` command is reached, showing five list elements: 5, 3, 6, 7, 2.
  * The items in the list should swap using animations.
  * The animations should faithfully reflect the bubble sort algorithm.
  * The list should end sorted in ascending order.
- **Actual**: As expected (see below).

- **Description**: Test that the list visualisation (searching) can be controlled from annotations within the code.
- **Input**: `binary-search.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open the `Program IO` window.
  3. Open `binary-search.s`.
  3. Run the simulation.
  4. When prompted to enter the list, enter the list: 1, 3, 5, 7, 9.
- **Expected**:
  * The high-level visualisation should open when the `l.show()` command is reached, showing the five list elements: 1, 3, 5, 7, 9.
  * A label should appear over the leftmost item of the current search section, and similarly for the rightmost item.
  * The current item being inspected should be emphasised, e.g. highlighted in red.
- **Actual**: As expected (see below).

### Error dialogs ###
Test ID: TC-E2E24

- **Description**: Test that an error message is shown when trying to run an invalid program.
- **Input**: `bad-add.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open `bad-add.s`.
  3. Run the simulation.
- **Expected**:
  * An error dialog should appear informing the user that there are two errors in the program.
- **Actual**: As expected (see below).


<!--
Test ID: TC-E2Ex

- **Description**:
- **Input**:
- **Action**:
  1.
- **Expected**:
  *
- **Actual**: As expected (see below).
-->
