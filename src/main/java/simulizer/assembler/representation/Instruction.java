package simulizer.assembler.representation;

import simulizer.assembler.representation.operand.OperandFormat;

import java.util.NoSuchElementException;

/**
 * supported instructions
 * @author mbway
 */
public enum Instruction {

    // information extracted from SPIM's documentation:
    // SPIM_SOURCE/Documentation/H_P_AppendixA/HP_AppA.pdf


// Arithmetic and Logic Instructions
    abs    (OperandFormat.destSrc,     "dest <- abs(src)"),

    and    (OperandFormat.destSrcSrc,  "logical AND"),
    //andi   (OperandFormat.destSrcImm,  "logical and immediate (zero extended)"),

    add    (OperandFormat.destSrcSrc,  "addition (with overflow)"),
    addu   (OperandFormat.destSrcSrc,  "addition (without overflow)"),
    addi   (OperandFormat.destSrcImm,  "add immediate (with overflow)"),
    addiu  (OperandFormat.destSrcImmU, "add immediate (without overflow)"),

    sub    (OperandFormat.destSrcSrc,  "subtraction (with overflow)"),
    subu   (OperandFormat.destSrcSrc,  "subtraction (without overflow)"),
    subi   (OperandFormat.destSrcImm,  "subtract immediate (with overflow)"),
    subiu  (OperandFormat.destSrcImmU, "subtract immediate (without overflow)"),

    mul    (OperandFormat.destSrcSrc,  "multiplication (without overflow) store low 32 bits in destination"),
    mulo   (OperandFormat.destSrcSrc,  "multiplication (with overflow) store low 32 bits in destination"),
    mulou  (OperandFormat.destSrcSrc,  "unsigned multiplication (with overflow) store low 32 bits in destination"),

    div    (OperandFormat.destSrcSrc,  "division (with overflow)"),
    divu   (OperandFormat.destSrcSrc,  "division (without overflow)"),

    neg    (OperandFormat.destSrc,     "dest <- -(src) (with overflow)"),
    negu   (OperandFormat.destSrc,     "dest <- -(src) (without overflow)"),

    nor    (OperandFormat.destSrcSrc,  "logical NOR"),
    not    (OperandFormat.destSrc,     "logical NOT"),
    or     (OperandFormat.destSrcSrc,  "logical OR"),
    ori    (OperandFormat.destSrcImm,  "logical OR immediate"),
    xor    (OperandFormat.destSrcSrc,  "XOR"),
    xori   (OperandFormat.destSrcImm,  "XOR immediate"),

// Constant-Manipulating Instructions
    li     (OperandFormat.destImm,     "load immediate"),


// Comparison Instructions: ignored

// Branch Instructions (limited range jump)
    // ignoring many "and link" instructions
    // ignoring many 2 register comparisons in favour of the compare to zero variety
    b      (OperandFormat.label,       "unconditional branch to label"),
    beq    (OperandFormat.cmpCmpLabel, "branch on equal"),
    bne    (OperandFormat.cmpCmpLabel, "branch on not equal"),
    bgez   (OperandFormat.cmpLabel,    "branch on >= zero"),
    bgtz   (OperandFormat.cmpLabel,    "branch on > zero"),
    blez   (OperandFormat.cmpLabel,    "branch on <= zero"),
    bltz   (OperandFormat.cmpLabel,    "branch on < zero"),
    beqz   (OperandFormat.cmpLabel,    "branch on = zero"),

// Jump Instructions
    j      (OperandFormat.label,       "unconditional jump to label"),
    jal    (OperandFormat.label,       "unconditional jump and link"),
    jr     (OperandFormat.register,    "unconditional jump using register value"),

// Trap Instructions: ignored

// Load Instructions
    la     (OperandFormat.destAddr,    "store the computed address into the destination"),
    lb     (OperandFormat.destAddr,    "load byte (sign extended) from the address into the destination"),
    lbu    (OperandFormat.destAddr,    "load byte unsigned (not sign extended) from the address into the destination"),
    lh     (OperandFormat.destAddr,    "load half (sign extended) from the address into the destination"),
    lhu    (OperandFormat.destAddr,    "load half unsigned (not sign extended) from the address into the destination"),
    lw     (OperandFormat.destAddr,    "load word from the address into the destination"),

// Store Instructions
    sb     (OperandFormat.srcAddr,    "store byte to the address"),
    sh     (OperandFormat.srcAddr,    "store half to the address"),
    sw     (OperandFormat.srcAddr,    "store word to the address"),

// Movement Instructions
    // ignoring conditional moves
    move   (OperandFormat.destSrc,    "dest <- src"),


// Misc instructions
    syscall (OperandFormat.noArguments, "make a system call"),
    nop     (OperandFormat.noArguments, "no-op (do nothing)"),
    BREAK   (new OperandFormat().allowed1(OperandFormat.OperandType.UNSIGNED_IMMEDIATE), "break into the debugger");


    private final OperandFormat f;

    Instruction(OperandFormat f, String purpose) {
        this.f = f;
    }

    public OperandFormat getOperandFormat() {
        return this.f;
    }


    /**
     * get an instruction from a string, throws NoSuchElementException if no such instruction
     * @param name the name of the register
     * @return the register with the given name
     */
    public static Instruction fromString(String name) {
        try {
            if(name.equals("break")) {
                return BREAK; // needs to be handled specially because 'break' is a Java keyword
            } else {
                return valueOf(name);
            }
        } catch(IllegalArgumentException e) {
            throw new NoSuchElementException();
        }
    }
}
