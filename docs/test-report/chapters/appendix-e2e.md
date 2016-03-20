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
- **Expected**:
  * Open dialog shown.
  * Text from `bubblesort.s` is loaded into the editor.
  * After clicking the fold button, lines 108-134 should be collapsed and the line numbers should show 108 then 135.
  * A red box should appear around `potato`.
- **Actual**: As expected (see below).

![](segments/end-to-end/editor-open-dialog.png)

![](segments/end-to-end/editor-fold.png)

![](segments/end-to-end/editor-error.png)

![](segments/end-to-end/editor-edit.png)
