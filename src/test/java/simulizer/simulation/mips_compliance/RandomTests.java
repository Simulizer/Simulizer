package simulizer.simulation.mips_compliance;

import category.UnitTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import simulizer.assembler.Assembler;
import simulizer.assembler.extractor.problem.ProblemCountLogger;
import simulizer.assembler.extractor.problem.ProblemLogger;
import simulizer.assembler.representation.Program;
import simulizer.simulation.components.IOTest;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.user_interaction.BufferIO;
import simulizer.simulation.cpu.user_interaction.IOStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by matthew on 07/09/16.
 */
@Category({UnitTests.class})
public class RandomTests {

    private Program createProgram(String myInstructions)
    {
        String program = "" +
                ".data\n" +
                "mystr: .asciiz \"This is my test String\"\n"+
                "mynum: .word -10\n" +
                "mynewnum: .byte 10\n" +
                ".align 2\n" +
                ".text\n" +
                ".globl main\n" +
                "main:\n" +
                myInstructions;

        ProblemCountLogger log = new ProblemCountLogger(null);

        Program p = Assembler.assemble(program, log, false);
        assertEquals(log.problemCount, 0);
        return p;
    }

    @Test
    public void randomTests() {
        {// these should be equivalent to nop
            String myInstructions = "" +
                "srl  $zero, $zero, 0;\n" +
                "sllv $zero, $zero, $zero;\n" +
                "srlv $zero, $zero, $zero;\n" +
                "add  $zero, $zero, $zero;\n" +
                "addu $zero, $zero, $zero;\n" +
                "sub  $zero, $zero, $zero;\n" +
                "subu $zero, $zero, $zero;\n" +
                "addi $zero, $zero, 0;\n" +
                "addiu $zero, $zero, 0;\n" +
                "subi $zero, $zero, 0;\n";

            Program program = createProgram(myInstructions);

            CPU cpu = new CPU(new IOTest());
            cpu.loadProgram(program);
            cpu.runProgram();
        }

        { // polymorphic j instruction
            String myInstructions = "" +
                    "la $s0, END;\n" +
                    "jr $s0;\n" +
                    "# jump over this\n" +
                    "li $a0, 0xFF;\n" +
                    "li $v0, 1;\n" +
                    "syscall\n" +
                "END:\n" +
                    "li $v0, 10\n" +
                    "syscall\n";


            Program program = createProgram(myInstructions);

            BufferIO io = new BufferIO();
            CPU cpu = new CPU(io);
            cpu.loadProgram(program);
            cpu.runProgram();

            String output = io.getOutput(IOStream.STANDARD);
            assertEquals("", output);



            myInstructions = "" +
                    "la $s0, END;\n" +
                    "j $s0;\n" +            // using j $reg instead of jr $reg
                    "# jump over this\n" +
                    "li $a0, 0xFF;\n" +
                    "li $v0, 1;\n" +
                    "syscall\n" +
                "END:\n" +
                    "li $v0, 10\n" +
                    "syscall\n";


            program = createProgram(myInstructions);

            io = new BufferIO();
            cpu = new CPU(io);
            cpu.loadProgram(program);
            cpu.runProgram();

            output = io.getOutput(IOStream.STANDARD);
            assertEquals("", output);
        }
    }
}
