package simulizer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;

/**
 * execute spim with a given program and catch the output for analysis
 * @author mbway
 */
public class SpimRunner {

    String tmpFilePath;
    String program;
    String input;
    String output;

    public SpimRunner() {
        tmpFilePath = "";
        program = "";
        input = "";
        output = "";
    }


    public String runSpim(String program, String sendStdin) {
        return runSpim(program, sendStdin, null);
    }

    public String runSpim(String program, String sendStdin, List<String> extraArgs) {
        this.program = program;
        this.input = sendStdin;

        File tmp = null;
        try {
            tmp = File.createTempFile("program", ".s");
            FileWriter w = new FileWriter(tmp);
            w.write(program);
            w.close();
            tmpFilePath = tmp.getAbsolutePath();
        } catch(IOException e) {
            UIUtils.showExceptionDialog(e);
        }

        assert tmp != null;
        try {
            //System.out.println(tmp.getAbsolutePath());

            ProcessBuilder spimBuilder = new ProcessBuilder(
                "spim", "-file", tmp.getAbsolutePath()
            ).redirectErrorStream(true);

            if(extraArgs != null) {
                spimBuilder.command().addAll(1, extraArgs);
            }

            Process spim = spimBuilder.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(spim.getInputStream()));

            if(sendStdin != null && sendStdin.length() != 0) {
                Writer stdin = new BufferedWriter(new OutputStreamWriter(spim.getOutputStream()));

                stdin.write(sendStdin);
                stdin.flush();
                stdin.close();
            }

            Scanner s = new Scanner(stdout);

            // SPIM writes "Loaded: /some/path/exceptions.s"
            // on the first line of the output
            s.skip("Loaded: .*\\.s[\n]");

            StringBuilder sb = new StringBuilder();
            while(s.hasNextLine()) {
                String line = s.nextLine();
                sb.append(line);
                sb.append('\n');
            }

            spim.waitFor();

            if(sb.lastIndexOf("\n") == -1) {
                output = sb.toString();
                return output;
            } else {
                output = sb.deleteCharAt(sb.lastIndexOf("\n")).toString();
                return output;
            }


        } catch(IOException | InterruptedException e) {
            UIUtils.showExceptionDialog(e);
        }

        tmp.getAbsoluteFile().deleteOnExit(); // delete when the JVM exits

        return null;
    }

    public static void runQtSpim(String program) {
		File tmp = null;
		try {
			tmp = File.createTempFile("program", ".s");
			FileWriter w = new FileWriter(tmp);
			w.write(program);
			w.close();
		} catch(IOException e) {
            UIUtils.showExceptionDialog(e);
		}

		assert tmp != null;

        try {
            Runtime.getRuntime().exec(new String[] {"qtspim", "-file", tmp.getAbsolutePath()});
        } catch (IOException e) {
            UIUtils.showExceptionDialog(e);
        }

        tmp.getAbsoluteFile().deleteOnExit(); // delete when the JVM exits
	}
}
