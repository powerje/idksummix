package summixSimulator;

import java.io.*;
import java.util.Scanner;


import summixSimulator.SummiX_Utilities.InstructionCode;

public class Executor {
	public Executor(SummiX_Machine machine, short data, InstructionCode op) {
		short sr1, sr2, sr, dr, pg, imm5, pgoffset6, pgoffset9;
		
		switch (op) {
			case ADD:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				sr2 = SummiX_Utilities.getBits(data, 12, 3);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												+ machine.loadRegister(sr2)));
				break;
			case ADD2:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				imm5 = SummiX_Utilities.getBits(data, 11, 5);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												+ imm5));
				break;
			case AND:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				sr2 = SummiX_Utilities.getBits(data, 12, 3);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												& machine.loadRegister(sr2)));
				break;
			case AND2:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				imm5 = SummiX_Utilities.getBits(data, 11, 5);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												& imm5));
				break;
			case BRX: 
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				if ((SummiX_Utilities.getBits(data, 4, 1) == 1) && machine.getN() ||  
					(SummiX_Utilities.getBits(data, 5, 1) == 1) && machine.getZ() || 
					(SummiX_Utilities.getBits(data, 6, 1) == 1) && machine.getP()) { 
					//if any of the above cases are true set the pc
					machine.setPC((short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(), 0, 7) + pgoffset9));
				}
				break;
			case DBUG: //The DBUG instruction displays the contents of PC, general registers, and ccr to the console
				for (int i=0;i < 8;i++) { //print general registers
					System.out.print("|R" + i + ": " + machine.loadRegister(i) + "\t|");
				}
				System.out.print("\n|PC: " + machine.getPC() + "\t|\n");
				System.out.print("CCR: N - " + machine.getN() + " Z - " + machine.getZ() + " P - " + machine.getP());
				break;
/*
 * The JSR and JSRR instructions allow jumps to subroutines. The PC is modied according to
the operand of JSR/JSRR. The destination address for JSR is computed as with ST, while the
destination address for JSRR is computed as with STR. If the link bit (L) is set, the value of
PC is saved to R7 before branching. (If the L bit is not set, these instructions are written JMP
and JMPR respectively.) Note that these instructions do not modify the condition code registers,
despite updating a general purpose register (R7).
 */
			case JSR:
				if (SummiX_Utilities.getBits(data, 4, 1) == 1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				//jump to first 7 bits of PC plus 9 given by offset
				machine.setPC((short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(), 0, 7) + pgoffset9));
				break;
			case JSRR: //someone read the directions for this and check to make sure i didn't mess it up
				if (SummiX_Utilities.getBits(data, 4, 1) == 1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				pgoffset6 = (short) (SummiX_Utilities.getBits(data, 10, 6));
				machine.setPC((short) (pgoffset6 + machine.loadRegister(SummiX_Utilities.getBits(data, 7, 3))));
				break;
			case LD:
				dr = SummiX_Utilities.getBits(data, 4, 3);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				machine.setRegister(dr, (short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(), 0, 7) + pgoffset9));
				break;
			case LDI:
				break;
			case LDR:
				break;
			case LEA:
				break;
			case NOT:
				dr = SummiX_Utilities.getBits(data, 4, 3); // Get destination register
				sr = SummiX_Utilities.getBits(data, 7, 3); // Get source register
				sr = (short) ~sr; //Bitwise inversion of the value in source register
				machine.setRegister(dr, sr); //Store data from source register into destination register
				break;
			case RET:
				machine.setPC(machine.loadRegister(7)); // copies the contents of R7 to PC
				break;
			case ST:
				sr = SummiX_Utilities.getBits(data, 4, 3);
				pg = SummiX_Utilities.getBits(machine.getPC(), 0, 7);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				machine.setMemory(pg, pgoffset9, machine.loadRegister(sr));
				break;
			case STI:
				break;
			case STR:
				break;
			case OUT:
				System.out.print((char)SummiX_Utilities.getBits(machine.loadRegister(0), 0, 8));
				break;
			case PUTS:
				char tempChar;
				short memorySpaceToLoadFrom = machine.loadRegister(0);
				short page = SummiX_Utilities.getBits(memorySpaceToLoadFrom, 0, 7);
				short offset = SummiX_Utilities.getBits(memorySpaceToLoadFrom, 8, 9);
				tempChar = (char) machine.loadMemory(page, offset);
				
				while (tempChar != '\0')
				{
					System.out.print(tempChar);
					
					if (offset==511) {
						page = (short) (((short) (page >>> 9) + 1) << 9);
						memorySpaceToLoadFrom = page;
					} else {
						memorySpaceToLoadFrom++;
					}
					
					page = SummiX_Utilities.getBits(memorySpaceToLoadFrom, 0, 7);
					offset = SummiX_Utilities.getBits(memorySpaceToLoadFrom, 8, 9);
					tempChar = (char) machine.loadMemory(page, offset);
				}
				break;
			case IN:
				System.out.print("Please input character to be stored in R0: ");
				Scanner in = new Scanner(System.in);
				char ascii = in.next().charAt(0);
				System.out.println(ascii);
				// machine.setRegister(0,((short) ((ascii << 8) >>> 8))); 
				machine.setRegister(0,(short)ascii); // may have to use above statement to clear upper 8 bits
				break;
			case HALT:
				System.out.println("System exited normally.");
				break;
			case OUTN:
				break;
			case INN:
				break;
			case RND:
				machine.setRegister(0, (short)Math.random());
				break; 			
		}
	}
}


