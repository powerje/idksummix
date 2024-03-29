/**
 * 
 */
package summixSimulator;

import summixSimulator.SummiX_Utilities.InstructionCode;

/**
 * The SummiX interpreter simulates the operation of the
 * machine as it executes each instruction.
 * 
 * @author Mike/Mike/Dan/Jim
 *
 */
public class Interpreter {
	

	public static boolean getInstruction(SummiX_Machine machine, short data)
	{
		int instruction;
		boolean halt = false;
		instruction = SummiX_Utilities.getBits(data, 0, 4);
		InstructionCode op=null;
		int bit;
		
		switch(instruction) {
		case 1: //ADD
			bit = SummiX_Utilities.getBits(data, 10, 1);  //get ADD bit			
			if (bit == 0)
				op = InstructionCode.ADD;
			else
				op = InstructionCode.ADD2;
			break;
		case 5:  //AND
			bit = SummiX_Utilities.getBits(data, 10, 1);  //get AND bit
			if (bit == 0)
				op = InstructionCode.AND;
			else
				op = InstructionCode.AND2;
			break;
		case 0:  //BRx
				op = InstructionCode.BRX;
		break;
		case 8:  //DBUG
			op = InstructionCode.DBUG;
			break;
		case 4:  //JSR
			op = InstructionCode.JSR;
			break;
		case 12:  //JSRR
			op = InstructionCode.JSRR;
			break;
		case 2:  //LD
			op = InstructionCode.LD;
			break;
		case 10:  //LDI
			op = InstructionCode.LDI;
			break;
		case 6:  //LDR
			op = InstructionCode.LDR;
			break;
		case 14:  //LEA
			op = InstructionCode.LEA;
			break;
		case 9:  //NOT
			op = InstructionCode.NOT;
			break;
		case 13:  //RET
			op = InstructionCode.RET;
			break;
		case 3:  //ST
			op = InstructionCode.ST;
			break;
		case 11:  //STI
			op = InstructionCode.STI;	
			break;
		case 7:  //STR
			op = InstructionCode.STR;
			break;
		case 15:  //TRAP
			//use case selects for multiple traps
			short trap = SummiX_Utilities.getBits(data, 8, 8); // get trapvect8
			switch(trap){
			case 0x21:  //OUT
				op = InstructionCode.OUT;
				break;
			case 0x22:  //PUTS
				op = InstructionCode.PUTS;
				break;
			case 0x23:  //IN
				op = InstructionCode.IN;
				break;
			case 0x25:  //HALT
				op = InstructionCode.HALT;
				halt = true;
				break;
			case 0x31:  //OUTN
				op = InstructionCode.OUTN;
				break;
			case 0x33:  //INN
				op = InstructionCode.INN;
				break;
			case 0x43:  //RND
				op = InstructionCode.RND;
				break;
			default:
				System.out.println("Error: Invalid TRAP code");
				op = InstructionCode.ERR;
				halt = true;
				break;
			}
			break;
		default:
			System.out.println("Error: Invalid OPCODE");
			op = InstructionCode.ERR;
			halt = true;
			break;
		}
		try {
		new Executor(machine, data, op); //PC gets incremented in here
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: memory out of bounds exception - page: " + SummiX_Utilities.getBits(data, 0, 7) + ", offset: " + SummiX_Utilities.getBits(data, 7, 9));
		}
		if (instruction == 15) { //traps are special
			//all trap instructions set r7 to the address following the trap instruction
			machine.setSubroutineReturn(machine.getPC());
                        if (op == InstructionCode.HALT) {
                            //this has to go here or it would look funny for step/trace to print register information after the machine has "halted"
                            System.out.println("\nSystem exited normally.");
                        }
		}
		return halt;
	}
}
