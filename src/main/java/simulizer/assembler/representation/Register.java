package simulizer.assembler.representation;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * general purpose registers of a MIPS32 machine
 * @author mbway
 */
public enum Register {

    // information extracted from SPIM's documentation:
    // SPIM_SOURCE/Documentation/H_P_AppendixA/HP_AppA.pdf

    zero  (0),   // constant 0
    at    (1),   // reserved for assembler
    v0    (2),   // expression evaluation and results of a function
    v1    (3),   // expression evaluation and results of a function
    a0    (4),   // argument 1
    a1    (5),   // argument 2
    a2    (6),   // argument 3
    a3    (7),   // argument 4
    t0    (8),   // temporary (not preserved across call)
    t1    (9),   // temporary (not preserved across call)
    t2    (10),  // temporary (not preserved across call)
    t3    (11),  // temporary (not preserved across call)
    t4    (12),  // temporary (not preserved across call)
    t5    (13),  // temporary (not preserved across call)
    t6    (14),  // temporary (not preserved across call)
    t7    (15),  // temporary (not preserved across call)
    s0    (16),  // saved temporary (preserved across call)
    s1    (17),  // saved temporary (preserved across call)
    s2    (18),  // saved temporary (preserved across call)
    s3    (19),  // saved temporary (preserved across call)
    s4    (20),  // saved temporary (preserved across call)
    s5    (21),  // saved temporary (preserved across call)
    s6    (22),  // saved temporary (preserved across call)
    s7    (23),  // saved temporary (preserved across call)
    t8    (24),  // temporary (not preserved across call)
    t9    (25),  // temporary (not preserved across call)
    k0    (26),  // reserved for OS kernel
    k1    (27),  // reserved for OS kernel
    gp    (28),  // pointer to global area
    sp    (29),  // stack pointer
    fp    (30),  // frame pointer
    ra    (31);  // return address (used by function call)

    private final int id;

    Register(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return this.toString();
    }

    /**
     * get a register from a string, throws NoSuchElementException if no such register
     * @param name the name of the register
     * @return the register with the given name
     */
    public static Register fromString(String name) {
        try {
            return valueOf(name);
        } catch(IllegalArgumentException e) {
            throw new NoSuchElementException();
        }
    }

    /**
     * get a register from an integer id, throws NoSuchElementException if no such register
     * @param id the ID of the register
     * @return the register with the given id
     */
    public static Register fromID(int id) {
        return Arrays.asList(values()).stream().filter(r -> r.getID() == id).findAny().get();
    }

}
