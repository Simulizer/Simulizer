## End to end tests ##

Each of these tests will start with all windows closed.

### Editor ###

**Test ID**: TC-E2Ex  

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

![](segments/end-to-end/editor-new-file-save-test.png)

![](segments/end-to-end/editor-new-file-save-test-dialog.png)

![](segments/end-to-end/editor-new-file-save-vim.png)

**Test ID**: TC-E2Ex  

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
  14. `Edit -> Toggle Word rap`.
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

![](segments/end-to-end/editor-open-dialog.png)

![](segments/end-to-end/editor-fold.png

![](segments/end-to-end/editor-edit.png)

**Test ID**: TC-E2Ex

- **Description**: Line number tracking while simulation is running.
- **Input**: `count.s`
- **Action**:
  1. Open the `Editor` window.
  2. Open `count.s` in the editor.
  3. Press `F5`.
  4. Hover over the highlighted line number.
- **Expected**:
  * A dialog appears informing the user that their program is being assembled.
  * Editor becomes read-only and the line number of the current line is highlighted.
  * Hovering over the highlighted line number should show the current stage of execution, e.g. `decoding`.
- **Actual**: As expected (see below).

**Test ID**: TC-E2Ex

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

## Registers window ##

Test ID: TC-E2Ex

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

Test ID: TC-E2Ex

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
- **Actual**: $\TODO{Get a correct result}. As expected (see below).

Test ID: TC-E2Ex

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
