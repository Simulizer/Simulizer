package simulizer.simulation.cpu.components;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.InstructionException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;
import simulizer.simulation.instructions.AddressMode;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.messages.DataMovementMessage;
import simulizer.simulation.messages.InstructionTypeMessage;
import simulizer.simulation.messages.RegisterChangedMessage;
import simulizer.simulation.messages.StageEnterMessage;
import simulizer.simulation.messages.StageEnterMessage.Stage;
import simulizer.utils.UIUtils;

/**class is used for executing instructions (including syscall)
 * it is separate from the main CPU model due to it's size
 * since the cpu is the main model, it will be wrapped up in the cpu class
 * @author Charlie Street
 *
 */
public class Executor {
	private CPU cpu;//needs access to the cpu to execute
	
	/**initialise cpu
	 * 
	 * @param cpu the cpu object currently being used
	 */
	public Executor(CPU cpu) {
		this.cpu = cpu;
	}
	
	 /**this method will execute the instruction given to it
     * wrapper for method in Executor, gives nice inheritance layout
     * @param instruction instruction set up with all necessary data
     * @param programCounter the current program counter value
     * @throws InstructionException if problem during execution
     * @throws ExecuteException if problem during execution
     * @throws HeapException if problem with heap
     * @throws MemoryException if problem accessing memory
     * @throws StackException 
     */
    public Address execute(InstructionFormat instruction, Address programCounter) throws InstructionException, ExecuteException, MemoryException, HeapException, StackException {
        Address toReturn = programCounter;
    	cpu.sendMessage(new StageEnterMessage(Stage.Execute));//signal start of execution
    	switch(instruction.mode) {//switch based on instruction format
            case RTYPE:
            	cpu.sendMessage(new InstructionTypeMessage(AddressMode.RTYPE));//send message giving idea of datapath selected
                Word result = cpu.getALU().execute(instruction.getInstruction(), instruction.asRType().getSrc1(), instruction.asRType().getSrc2());
                cpu.sendMessage(new DataMovementMessage(instruction.asRType().getSrc1(),Optional.empty()));//moved into alu
                cpu.sendMessage(new DataMovementMessage(instruction.asRType().getSrc2(),Optional.empty()));
                cpu.getRegisters()[instruction.asRType().getDestReg().getID()] = result;//storing result
                cpu.sendMessage(new DataMovementMessage(Optional.of(cpu.getRegisters()[instruction.asRType().getDestReg().getID()]),Optional.empty()));
                cpu.sendMessage(new RegisterChangedMessage(instruction.asRType().getDestReg()));
                break;
            case ITYPE:
            	cpu.sendMessage(new InstructionTypeMessage(AddressMode.ITYPE));
            	cpu.sendMessage(new DataMovementMessage(instruction.asIType().getCmp1(),Optional.empty()));
            	cpu.sendMessage(new DataMovementMessage(instruction.asIType().getCmp2(),Optional.empty()));
                Word branchTest = cpu.getALU().execute(instruction.getInstruction(), instruction.asIType().getCmp1(), instruction.asIType().getCmp2());//carrying out comparison
                if(equalByteArrays(branchTest.getWord(), ALU.branchTrue)) {
                    toReturn = instruction.asIType().getBranchAddress().get();//set the program counter
                    cpu.sendMessage(new DataMovementMessage(Optional.of(encodeU((long)toReturn.getValue())),Optional.empty()));
                }
                break;
            case SPECIAL:
            	cpu.sendMessage(new InstructionTypeMessage(AddressMode.SPECIAL));
                if(instruction.getInstruction().equals(Instruction.syscall)) {//syscall
                    int v0 = (int)DataConverter.decodeAsSigned(cpu.getRegisters()[Register.v0.getID()].getWord());//getting code for syscall
                    syscall(v0);//carry out specified syscall op
                }
                else if(instruction.getInstruction().equals(Instruction.BREAK)) {
					cpu.pause();
                }
                else if(!instruction.getInstruction().equals(Instruction.nop)) {
                    throw new ExecuteException("Error with zero argument instruction", instruction);
                }
                break;
            case JTYPE:
            	cpu.sendMessage(new InstructionTypeMessage(AddressMode.JTYPE));
                if(instruction.getInstruction().equals(Instruction.jal)) {//making sure i put current address in ra
                    cpu.getRegisters()[Register.ra.getID()] = instruction.asJType().getCurrentAddress().get();
                    cpu.sendMessage(new DataMovementMessage(Optional.of(cpu.getRegisters()[Register.ra.getID()]),Optional.empty()));
                    cpu.sendMessage(new RegisterChangedMessage(Register.ra));
                }

                toReturn = instruction.asJType().getJumpAddress().get();//loading new address into the PC
                cpu.sendMessage(new DataMovementMessage(Optional.of(encodeU((long)toReturn.getValue())),Optional.empty()));
                break;
            case LSTYPE:
            	cpu.sendMessage(new InstructionTypeMessage(AddressMode.LSTYPE));
                if(instruction.getInstruction().getOperandFormat().equals(OperandFormat.destImm)) {//li
                	if(instruction.getInstruction().equals(Instruction.li)) {
                		  cpu.getRegisters()[instruction.asLSType().getRegisterName().get().getID()] = instruction.asLSType().getImmediate().get();
                	} else if(instruction.getInstruction().equals(Instruction.lui)) {
                		byte[] immediate = instruction.asLSType().getImmediate().get().getWord();
                		immediate = new byte[]{immediate[2],immediate[3],0x00,0x00};//lower half of immediate as upper half
                		cpu.getRegisters()[instruction.asLSType().getRegisterName().get().getID()] = new Word(immediate);
                	}
                  
                    cpu.sendMessage(new DataMovementMessage(Optional.of(cpu.getRegisters()[instruction.asLSType().getRegisterName().get().getID()]),Optional.empty()));
                    cpu.sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));

                } else if(instruction.getInstruction().getOperandFormat().equals(OperandFormat.destAddr)) {//load
                    int retrieveAddress = instruction.asLSType().getMemAddress().get().getValue();

                    if(instruction.getInstruction().equals(Instruction.la)) {//have to be careful with la
                    	Word address = new Word(DataConverter.encodeAsSigned((long)retrieveAddress));
                    	cpu.getRegisters()[instruction.asLSType().getRegisterName().get().getID()] = address;
                	}
                	else {
	                    int length = 0;//length to read
	                    byte[] read;

	                    if(instruction.getInstruction().equals(Instruction.lw)) {//checking length to read
	                    	length = 4;
	                    } else if(instruction.getInstruction().equals(Instruction.lb)||instruction.getInstruction().equals(Instruction.lbu)) {
	                    	length = 1;
	                    } else if(instruction.getInstruction().equals(Instruction.lh)||instruction.getInstruction().equals(Instruction.lhu)) {
	                    	length = 2;
	                    }
	                    read = cpu.getMainMemory().readFromMem(retrieveAddress, length);//read bytes from memory
	                    cpu.sendMessage(new DataMovementMessage(Optional.of(new Word(read)),Optional.empty()));
	                    
	                    if(instruction.getInstruction().equals(Instruction.lb)||instruction.getInstruction().equals(Instruction.lh)) {//unsigned vs signed
	                    	long val = DataConverter.decodeAsSigned(read);
	                    	read = DataConverter.encodeAsSigned(val);
	                    } else if(instruction.getInstruction().equals(Instruction.lbu)||instruction.getInstruction().equals(Instruction.lhu)) {
	                    	long val = DataConverter.decodeAsUnsigned(read);
	                    	read = DataConverter.encodeAsUnsigned(val);
	                    }
	                    
	                    cpu.getRegisters()[instruction.asLSType().getRegisterName().get().getID()] = new Word(read);
                	}
                    cpu.sendMessage(new DataMovementMessage(Optional.of(cpu.getRegisters()[instruction.asLSType().getRegisterName().get().getID()]),Optional.empty()));
                	cpu.sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                }
                else if(instruction.getInstruction().getOperandFormat().equals(OperandFormat.srcAddr)) {//store
                	byte[] toStore;//where to store the data to be put in memory
                	
                	if(instruction.getInstruction().equals(Instruction.sb)) {
                		toStore = new byte[]{instruction.asLSType().getRegister().get().getWord()[3]};//lowest byte
                	} else if(instruction.getInstruction().equals(Instruction.sh)) {
                		toStore = instruction.asLSType().getRegister().get().getWord();
                		toStore = new byte[]{toStore[2],toStore[3]};
                	} else {//sw
	                    toStore = instruction.asLSType().getRegister().get().getWord();//all 4 bytes
                	}
                	
                	int storeAddress = instruction.asLSType().getMemAddress().get().getValue();
	                cpu.getMainMemory().writeToMem(storeAddress, toStore);
	                cpu.sendMessage(new DataMovementMessage(Optional.of(new Word(toStore)),Optional.empty()));
                }
                else {
                    throw new ExecuteException("Error executing load/store instruction.", instruction);
                }
                break;
            default:
                throw new ExecuteException("Error during Execution", instruction);
        }
    	return toReturn;
    }

    /**will use IO to enable the use of system calls with the user
     * 
     * @param v0 the syscall code retrieved from the v0 register
     * @throws InstructionException if invalid syscall code
     * @throws HeapException if problem using sbrk like a0 not multiple of 4
     * @throws MemoryException if problem reading from memory for read string
     * @throws StackException 
     */
    private void syscall(int v0) throws InstructionException, HeapException, MemoryException, StackException {
    	int a0 = (int)DataConverter.decodeAsSigned(cpu.getRegisters()[Register.a0.getID()].getWord());//getting main argument register
    	switch(v0) {
    		case 1://print int
    			cpu.getIO().printInt(IOStream.STANDARD, a0);//printing to console
    			break;
    		case 4://print string
				byte[] stringData = cpu.getMainMemory().readUntilNull(a0);
				String str = new String(stringData, StandardCharsets.UTF_8);

    			cpu.sendMessage(new DataMovementMessage(Optional.of(new Word(new byte[4])),Optional.empty()));
    			cpu.getIO().printString(IOStream.STANDARD, str);
    			break;
    		case 5://read int
    			int read = cpu.getIO().readInt(IOStream.STANDARD);//reading in from console
    			Word readAsWord = new Word(DataConverter.encodeAsSigned((long)read));
    			cpu.getRegisters()[Register.v0.getID()] = readAsWord;//storing in v0
    			cpu.sendMessage(new DataMovementMessage(Optional.of(cpu.getRegisters()[Register.v0.getID()]),Optional.empty()));
    			cpu.sendMessage(new RegisterChangedMessage(Register.v0));
    			break;
    		case 8://read string
    			String readInString = cpu.getIO().readString(IOStream.STANDARD);//this string will be cut to maxChars -1 i.e last one will be null terminator
    			int a1 = (int)DataConverter.decodeAsSigned(cpu.getRegisters()[Register.a1.getID()].getWord());//max chars stored here
    			int addressIBuf = a0;//start of input buffer
    			if(readInString.length() >= a1) {//truncating string
    				readInString = readInString.substring(0, a1-1);
    			}
    			for(int i = 0; i < readInString.length(); i++) {//writing each character to memory
    				String toWriteStr = "" + readInString.charAt(i);
    				byte[] toWrite = toWriteStr.getBytes();
    				cpu.getMainMemory().writeToMem(addressIBuf, toWrite);//write into memory
    				addressIBuf += 1;//moving to next word in memory
    			}
    			
    			byte[] nullTerminator = new byte[]{0x00,0x00,0x00,0x00};//null terminator for string
    			cpu.getMainMemory().writeToMem(addressIBuf, nullTerminator);//adding terminator signals end of string
    			cpu.sendMessage(new DataMovementMessage(Optional.of(new Word(nullTerminator)),Optional.empty()));
    			break;
    		case 9://sbrk
    			Address newBreak = cpu.getMainMemory().getHeap().sbrk(a0);
    			cpu.getRegisters()[Register.v0.getID()] = new Word(DataConverter.encodeAsSigned(newBreak.getValue()));
    			cpu.sendMessage(new DataMovementMessage(Optional.of(cpu.getRegisters()[Register.v0.getID()]),Optional.empty()));
    			cpu.sendMessage(new RegisterChangedMessage(Register.v0));
    			break;
    		case 10://exit program
    			cpu.stopRunning();
    			break;
    		case 11://print char
    			char toPrintChar = new String(new byte[]{DataConverter.encodeAsUnsigned(a0)[3]}).charAt(0);//int directly to char
    			cpu.getIO().printChar(IOStream.STANDARD, toPrintChar);
    			break;
    		case 12://read char
    			String readChar = cpu.getIO().readChar(IOStream.STANDARD) + "";//from console
    			byte[] asBytes = readChar.getBytes();
    			long asLong = DataConverter.decodeAsSigned(asBytes);
    			Word toWord = new Word(DataConverter.encodeAsSigned(asLong));//format for register storage
    			cpu.getRegisters()[Register.v0.getID()] = toWord;
    			cpu.sendMessage(new DataMovementMessage(Optional.of(cpu.getRegisters()[Register.v0.getID()]),Optional.empty()));
    			cpu.sendMessage(new RegisterChangedMessage(Register.v0));
    			break;
    		case 67697865://AND HIS NAME IS...
				UIUtils.openURL("https://www.youtube.com/watch?v=5LitDGyxFh4");
				UIUtils.showInfoDialog("And His Name Is", "JOHN CENA!!!");
    			break;
    		case 82736775://RICK ASCII :)
				UIUtils.openURL("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
				UIUtils.showInfoDialog("Never gonna give you up ;)", "");
				break;
    		default://if invalid syscall code
    			throw new InstructionException("Invalid syscall operation", Instruction.syscall);
    	}
    }
    
    /**useful auxiliary method to check if 2 byte arrays equal
    *
    * @param arr1 first array
    * @param arr2 second array
    * @return are they equal?
    */
   private boolean equalByteArrays(byte[] arr1, byte[] arr2) {
       for(int i = 0; i < arr1.length; i++) {
           if(arr1[i] != arr2[i]) {
               return false;
           }
       }
       return true;
   }
   
   /**
    * take a value interpreted as being unsigned and encode it as a word
    *
    * @param value the unsigned value to encode
    * @return the encoded value
    */
   private static Word encodeU(long value) {
       return new Word(DataConverter.encodeAsUnsigned(value));
   }
}
