package summixSimulator;

import java.io.*;
import java.util.Scanner;


import summixSimulator.SummiX_Utilities.InstructionCode;

public class Executor {
	public Executor(SummiX_Machine machine, short data, InstructionCode op) {
		short sr1, sr2, sr, dr, pg, imm5, baser, pgoffset6, index6, pgoffset9;
		Scanner in = new Scanner(System.in);
		
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
				dr = SummiX_Utilities.getBits(data, 4, 3);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				machine.setRegister(dr, (short) (SummiX_Utilities.getAbsoluteBits(machine.loadRegister(dr), 0, 7) + pgoffset9));				
				break;
			case LDR:
				//zero extend  index6 and add it to value in BaseR
				index6 = SummiX_Utilities.getBits(data, 10, 6); //zero extend index6
				baser = SummiX_Utilities.getBits(data, 7, 3);	
				dr = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (index6 + machine.loadRegister((int)baser)));
				break;
			case LEA:
				//15:9 pc + 8:0 pgoffset9
				dr = SummiX_Utilities.getBits(data, 7, 3);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				machine.setRegister(dr, (short) (SummiX_Utilities.getBits(machine.getPC(),0,7) + pgoffset9));
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
				sr = SummiX_Utilities.getBits(data, 4, 3);
				pg = SummiX_Utilities.getBits(sr, 0, 7);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				machine.setMemory(pg, pgoffset9, machine.loadRegister(sr));
				break;
			case STR:
				//zero extend  index6 and add it to value in BaseR
				index6 = SummiX_Utilities.getBits(data, 10, 6);
				baser = SummiX_Utilities.getBits(data, 7, 3);	
				sr = SummiX_Utilities.getBits(data, 4, 3);
				machine.setMemory(machine.loadRegister(baser), index6, machine.loadRegister(sr));
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
				char ascii = in.next().charAt(0);
				System.out.println(ascii);
				// machine.setRegister(0,((short) ((ascii << 8) >>> 8))); 
				machine.setRegister(0,(short)ascii); // may have to use above statement to clear upper 8 bits
				break;
			case HALT:
				System.out.println("System exited normally.");
				break;
			case OUTN:  //write value of r0 to console as a decimal
				System.out.print(machine.loadRegister(0));
				break;
			case INN:
				short input;
				System.out.print("Please enter a number between -32768 and 32767 with no commas: ");
				input = (short) Integer.getInteger(in.next()).intValue();
				machine.setRegister(0, input);
				break;
			case RND:
				machine.setRegister(0, (short)Math.random());
				break; 
			case ERR: //was an error op code
				System.out.println("System error: ");
				break;
		}
	}
}


