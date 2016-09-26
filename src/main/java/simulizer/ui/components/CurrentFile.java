package simulizer.ui.components;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import simulizer.GuiMode;
import simulizer.assembler.Assembler;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.representation.Program;
import simulizer.ui.windows.Editor;
import simulizer.utils.FileUtils;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


/**
 * Holds information about the currently loaded file separately from the editor window
 *
 * @author mbway
 */
public class CurrentFile {
    // TODO: option to have default directory = last save location
	private static File defaultDirectory = new File("code/");


	/**
	 * current backing file (null => none)
	 */
	private static File currentFile = null;
	/**
	 * what Simulizer expects the content of currentFile to be.
     * if it differs then the backing file has been edited or removed externally from Simulizer
	 */
    private static int onDiskHash = 0;

	/**
	 * persists after editor closes, brought up to date with changes made in the editor
	 * or externally from Simulizer when attempting to read it through getCurrentText()
	 */
	private static String currentText = "";


	/**
	 * Immutable structure to hold the results of continuous assembly.
	 * Because of this the information will always be consistent (the hash always refers to that program)
	 */
	private static class AssembledProgram {
		/**
		 * the hash of the file content at the time of assembling
		 */
		private int hash;
		/**
		 * The assembled program. null => has errors
		 */
		private Program program;

		AssembledProgram(int hash, Program program) {
			this.hash = hash;
			this.program = program;
		}
		public int getHash() {
			return hash;
		}
		public Program getProgram() {
			return program;
		}
	}

	private static boolean continuousCheckingEnabled = false;
	private static final AtomicBoolean checkInProgress = new AtomicBoolean(false);
	private static final ScheduledExecutorService continuousChecking =
			Executors.newSingleThreadScheduledExecutor(new ThreadUtils.NamedThreadFactory("Continuous-Checking"));
	private static ScheduledFuture<?> checkTask = null;
    private static int checkedProgramHash = 0;
	/**
	 * if a program is assembled then it is cached here so as not to waste time assembling again
	 */
	private static AssembledProgram assembledProgram = null;



	public static Program getCachedAssembledProgram(String text) {
		if(assembledProgram != null && text.hashCode() == assembledProgram.getHash()) {
			return assembledProgram.getProgram();
		} else {
			return null;
		}
	}
	public static void submitAssembledProgramToCache(String programText, Program p) {
		assembledProgram = new AssembledProgram(programText.hashCode(), p);
	}


	private static void tryGetEditor(Consumer<Editor> ifSuccessful) {
		Editor editor = Editor.getEditor();
		if(editor != null) {
			ifSuccessful.accept(editor);
		}
	}


	public static String getBackingFilename() {
		return currentFile == null ? "Untitled" : currentFile.getName();
	}


	private static void updateCurrentTextFromEditor() {
		Editor editor = Editor.getEditor();
		if (editor != null && editor.hasOutstandingChanges()) {
			try {
				ThreadUtils.platformRunAndWait(() -> currentText = editor.getEditorText());
			} catch (Throwable throwable) {
                UIUtils.showExceptionDialog(throwable);
			}
		}
	}
	public static String getCurrentText() {
        updateCurrentTextFromEditor();
		if(isChangedExternally()) {
			// no changes in the editor but external changes
			assert(currentText.hashCode() == onDiskHash); // should not have outstanding changes
			loadFileWithoutPrompt(currentFile);
		}
		return currentText;
	}


	private static boolean isChangedExternally() {
        return currentFile != null && (
        		!currentFile.exists() || FileUtils.getFileContent(currentFile).hashCode() != onDiskHash
		);
	}

	private static ButtonType externalChangeDialog() {
		return UIUtils.confirmYesNoCancel(
				"It appears the file \"" + getBackingFilename() + "\" has changed outside the editor.\n`" +
				"Would you like to overwrite it with the editor's version?\n('No' will load from disk)", "");
	}

	/**
     * Ask the user to select a file to save to. If given a valid file: save to it and return true, otherwise false
	 * @return whether a valid choice was made and the file was saved to
	 */
	static boolean promptSaveAs() {
		File file = UIUtils.saveFileSelector("Save an assembly file", GuiMode.getPrimaryStage(), defaultDirectory, new FileChooser.ExtensionFilter("Assembly files *.s", "*.s"));
		// TODO: does this dialog handle overwriting existing files?
		// TODO: does this dialog handle overwriting a directory (should fail)?
		if (file != null) {

			if (!file.getName().endsWith(".s"))
				file = new File(file.getAbsolutePath() + ".s");

			currentFile = file;
			saveFileWithoutPrompt();
            return true;
		}
		return false;
	}

	public static void promptSave() {
		if(currentFile == null) {
			promptSaveAs();
		} else if(isChangedExternally()) {
			ButtonType response = externalChangeDialog();

			if (response == ButtonType.YES) {
				saveFileWithoutPrompt();
			} else if(response == ButtonType.NO) {
				loadFileWithoutPrompt(currentFile);
			}
			// cancel or [X]
		} else {
			saveFileWithoutPrompt();
		}
	}
	/**
     * called when doing something destructive (like loading another file). Or manually when saving.
     * guides the user through making sure their data is saved
	 * @return whether the user chose to cancel the action that triggered the prompt
	 */
	public static boolean promptToSaveIfNecessary() {
		if(currentFile == null && getCurrentText().length() != 0) {
			// new file not yet saved
            for(;;) {
				ButtonType response = UIUtils.confirmYesNoCancel("Save changes to \"" + getBackingFilename() + "\"", "");

				if (response == ButtonType.YES) {
					boolean saved = promptSaveAs();
					if(saved) // otherwise ask again
						break;
                } else if (response == ButtonType.NO) {
                    loadFileWithoutPrompt(null); // discard changes to the new file without a backing file
					break; // chose not to save
				} else {
                    // cancel or [X]
                    return true;
                }
            }

		} else if(isChangedExternally()) {
			// changed externally
            ButtonType response = externalChangeDialog();

			if (response == ButtonType.YES) {
				saveFileWithoutPrompt();
			} else if(response == ButtonType.NO) {
				loadFileWithoutPrompt(currentFile);
			} else {
				// cancel or [X]
				return true; // cancelled
			}

		} else if(getCurrentText().hashCode() != onDiskHash) {
			// changed internally
			ButtonType response = UIUtils.confirmYesNoCancel("Save changes to \"" + getBackingFilename() + "\"", "");

			if (response == ButtonType.YES) {
				saveFileWithoutPrompt();
            } else if(response == ButtonType.NO) {
				loadFileWithoutPrompt(currentFile);
			} else {
                // cancel or [X]
                return true; // cancelled
            }
		}

		return false; // not cancelled
	}

	public static void loadInitialFile() {
		String initialFilename;
		if(!GuiMode.args.files.isEmpty())
			initialFilename = GuiMode.args.files.get(0);
		else
			initialFilename = (String) GuiMode.settings.get("editor.initial-file");


		if (initialFilename != null && !initialFilename.isEmpty()) {
			File f = new File(initialFilename);
			if (f.exists()) {
				currentFile = f;
				currentText = FileUtils.getFileContent(f);
				onDiskHash  = currentText.hashCode();
			} else {
                // allow the primary stage to show, and place the dialog box above it
				ThreadUtils.runLater(() -> {
                    ThreadUtils.sleepQuiet(3000);
					UIUtils.showErrorDialog("Could Not Load", "Could not load file: \"" + initialFilename + "\"\nBecause it does not exist.");
				});
				currentFile = null;
				currentText = "";
				onDiskHash  = 0;
			}
		} else {
			currentFile = null;
			currentText = "";
			onDiskHash  = 0;
		}

		startContinuousChecking();
	}

	private static void loadFileWithoutPrompt(File file) {
		if(file == null) {
			currentFile = null;
			currentText = "";
			onDiskHash  = 0;
		} else {
			currentFile = file;
			currentText = FileUtils.getFileContent(file);
			onDiskHash = currentText.hashCode();
		}
		tryGetEditor(Editor::loadCurrentFile);
		checkedProgramHash = 0; // force re-check so any problems are again displayed in the editor
	}
	/**
	 * just saves, no user interaction
	 */
	private static void saveFileWithoutPrompt() {
		if (currentFile == null) {
			UIUtils.showErrorDialog("Save Error", "cannot save because no file to save to");
			return;
		}
        updateCurrentTextFromEditor();
		// ok for file to not exist yet
		FileUtils.writeToFile(currentFile, currentText);
		onDiskHash = currentText.hashCode();
        // re-loading in case the on disk version was loaded and changes in the editor were discarded
		tryGetEditor(Editor::loadCurrentFile);
	}

	static void loadFile(File file) {
		boolean cancelled = promptToSaveIfNecessary();
		if(!cancelled)
            loadFileWithoutPrompt(file);
	}
	static void reloadFile() {
		boolean cancelled = promptToSaveIfNecessary();
		if(!cancelled)
		loadFileWithoutPrompt(currentFile);
	}
	static void newFile() {
		boolean cancelled = promptToSaveIfNecessary();
		if(!cancelled)
		loadFileWithoutPrompt(null);
	}


	public static boolean checkIsInProgress() {
		return checkInProgress.get();
	}

	private static void startContinuousChecking() {
		if(checkTask != null) {
			checkTask.cancel(false); // wait to finish
		}

        continuousCheckingEnabled           = (boolean) GuiMode.settings.get("editor.continuous-assembly");
		int continuousCheckingRefreshPeriod = (int) GuiMode.settings.get("editor.continuous-assembly-refresh-period");


		if(!continuousChecking.isShutdown()) { // executor still running
			checkTask = continuousChecking.scheduleAtFixedRate(() -> {

				if(!continuousCheckingEnabled)
					return;

				try {
					String program = getCurrentText();
					if(program == null || program.length() == 0) return;
					int thisProgramHash = program.hashCode();

					if (checkedProgramHash == 0 || checkedProgramHash != thisProgramHash) {
						if(checkInProgress.getAndSet(true))
							return; // assembly already in progress on some other thread

                        try {
							tryGetEditor((editor) -> Platform.runLater(editor::refreshTitle));

							//DebugUtils.Timer t = new DebugUtils.Timer("Continuous Assembly");
							final List<Problem> problems = Assembler.checkForProblems(program);
							tryGetEditor((editor) -> Platform.runLater(() -> editor.setProblems(problems)));
                            checkedProgramHash = thisProgramHash;
							//t.stopAndPrint();
						} finally {
							checkInProgress.set(false);
						}
					}
				} catch (Exception e) {
					UIUtils.showExceptionDialog(e);
				} finally {
					tryGetEditor((editor) -> Platform.runLater(editor::refreshTitle));
				}
			}, 0, continuousCheckingRefreshPeriod, TimeUnit.MILLISECONDS);
		}
	}





}
