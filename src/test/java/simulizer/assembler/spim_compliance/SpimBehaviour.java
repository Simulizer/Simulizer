package simulizer.assembler.spim_compliance;


import category.SpimTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * test the behaviour of spim
 * @author mbway
 */
@Category({SpimTests.class})
public class SpimBehaviour {

    private void expectSyntaxError(String spimOutput, int line) {
        assertTrue(spimOutput.contains("syntax error on line " + line));
    }
    private void expectCorrectParse(String expectedOutput, String spimOutput) {
        assertEquals(expectedOutput, spimOutput);
        assertFalse(spimOutput.contains("syntax error"));
    }



    private String printAnInteger(String dataType, String loadInstruction, String value) {
        String p = ""+
            ".data\n" +
            "  myint: ." + dataType + " " + value + "\n" +
            "  .space 4\n" +
            ".text\n" +
            "main:\n" +
            "  li $v0, 1; " + loadInstruction + " $a0, myint; syscall\n" + // print int
            "  li $v0, 10; syscall\n"; // exit
        SpimRunner spim = new SpimRunner();
        return spim.runSpim(p, "");
    }

    /**
     * parse the string as an integer and take the n least significant bytes
     */
    private static int truncate(String bigInteger, int bytes) {
        byte arr[] = new BigInteger(bigInteger).toByteArray();
        assert arr.length >= bytes;
        byte out[] = new byte[bytes];
        System.arraycopy(arr, arr.length - bytes, out, 0, bytes);
        return new BigInteger(out).intValue();
    }

    @Test
    public void testIntegerLiterals() {
        String output;

        output = printAnInteger("byte", "lb", "10");
        assertEquals(10, Integer.parseInt(output));

        // literal too large: take lowest n bytes
        output = printAnInteger("byte", "lb", "9817068107");
        assertEquals(truncate("9817068107", 1), Integer.parseInt(output));


        output = printAnInteger("half", "lh", "9817068107");
        assertEquals(truncate("9817068107", 2), Integer.parseInt(output));

        output = printAnInteger("word", "lw", "9817068108");
        assertEquals(truncate("9817068108", 4), Integer.parseInt(output));

        output = printAnInteger("word", "lw", "-9817068105");
        assertEquals(truncate("-9817068105", 4), Integer.parseInt(output));


        // hex not allowed to be denoted with a capital
        output = printAnInteger("word", "lw", "0Xabc");
        assertTrue(output.contains("The following symbols are undefined:\nXabc"));
        assertTrue(output.contains("0")); // prints 0
    }

    @Test
    public void testRegisterValues() {

        // initial stack pointer
        {
            String p = "" +
                ".text\n" +
                "main: li $v0, 1\n" +
                "move $a0, $sp\n" +
                "syscall\n" +
                "li $v0, 10; syscall";
            SpimRunner spim = new SpimRunner();
            String output = spim.runSpim(p, "");
            assertEquals(0x7ffff3c8, Integer.parseInt(output));
        }
    }

    @Test
    public void testStringLiterals() {
        // octal codes correct for small numbers
        // octal escape sequences
        {
            // three digits required (complains)
            {
                String p = "" +
                    ".data; mystring: .asciiz \"\\12\"\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 4; la $a0, mystring; syscall\n" + // print string
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                assertTrue(spim.runSpim(p, "").contains("Bad character in \\ooo construct"));
            }
            // sometimes it fails silently (literally prints the characters: '\' '4' '3')
            {
                String p = "" +
                    ".data; mystring: .asciiz \"\\43\"\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 4; la $a0, mystring; syscall\n" + // print string
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                expectCorrectParse("\\43", spim.runSpim(p, ""));
            }
            // small numbers handled correctly
            {
                String p = "" +
                    ".data; mystring: .asciiz \"\\043\"\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 4; la $a0, mystring; syscall\n" + // print string
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                expectCorrectParse("\43", spim.runSpim(p, ""));
            }
            // larger numbers not handled correctly (interprets \101 as \011)
            {
                String p = "" +
                    ".data; mystring: .asciiz \"\\101\"\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 4; la $a0, mystring; syscall\n" + // print string
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                expectCorrectParse("\011", spim.runSpim(p, ""));
            }
            // a bug: parses `"abc\"` as `abc"` (backticks not included)
            {
                String p = "" +
                    ".data; mystring: .asciiz \"abc\\\"\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 4; la $a0, mystring; syscall\n" + // print string
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                expectCorrectParse("abc\"", spim.runSpim(p, ""));
            }
        }
    }

    @Test
    public void testSyntax() {
        // spaces between '.' and the directive are not allowed
        {
            String p = "" +
                ".data\n" +
                "  myint: . word 15\n" + // syntax error
                ".text\n" +
                "main:\n" +
                "  li $v0, 1; lw $a0, myint; syscall\n" + // print int
                "  li $v0, 10; syscall\n"; // exit
            SpimRunner spim = new SpimRunner();
            expectSyntaxError(spim.runSpim(p, ""), 2);
        }
        // spaces between labels and ':' are allowed
        {
            String p = "" +
                ".data\n" +
                "  myint: .word 15\n" + // syntax error
                ".text\n" +
                "main   :\n" +
                "  li $v0, 1; lw $a0, myint; syscall\n" + // print int
                "  li $v0, 10; syscall\n"; // exit
            SpimRunner spim = new SpimRunner();
            expectCorrectParse("15", spim.runSpim(p, ""));
        }

        // semicolon or newline required after .data or .text directives
        {
            { // .data; allowed
                String p = "" +
                    ".data; myint: .word 15\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 1; lw $a0, myint; syscall\n" + // print int
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                expectCorrectParse("15", spim.runSpim(p, ""));
            }
            { // .data\n allowed

                String p = "" +
                    ".data\n" +
                    "  myint: .word 15\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 1; lw $a0, myint; syscall\n" + // print int
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                expectCorrectParse("15", spim.runSpim(p, ""));
            }
            { // neither: not allowed
                String p = "" +
                    ".data myint: .word 15\n" +
                    ".text\n" +
                    "main:\n" +
                    "  li $v0, 1; lw $a0, myint; syscall\n" + // print int
                    "  li $v0, 10; syscall\n"; // exit
                SpimRunner spim = new SpimRunner();
                expectSyntaxError(spim.runSpim(p, ""), 1);
            }
        }

        // semicolons are allowed all over the place
        {
            String p = "" +
                ".data;;;;; myint: .word 15\n" +
                ".text;;;\n" +
                ";;;main:;;;\n" +
                "  ;;li $v0, 1; lw $a0, myint; syscall;;;\n" + // print int
                "  ;;li $v0, 10; syscall;;;\n"; // exit
            SpimRunner spim = new SpimRunner();
            expectCorrectParse("15", spim.runSpim(p, ""));
        }

        // trailing commas and more than one comma in between parameters
        {
            String p = "" +
                ".data; myint: .word 15\n" +
                ".text\n" +
                "main:\n" +
                "  li $v0, 1; lw $a0, myint,; syscall\n" + // print int
                "  li $v0,, 10,; syscall\n"; // exit
            SpimRunner spim = new SpimRunner();
            expectCorrectParse("15", spim.runSpim(p, ""));
        }


    }
}
