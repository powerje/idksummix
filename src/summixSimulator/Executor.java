package summixSimulator;

import java.util.Scanner;
import summixSimulator.SummiX_Utilities.InstructionCode;

public class Executor {
	public Executor(SummiX_Machine machine, short data, InstructionCode op) {
		short sr1, sr2, sr, dr, pg, imm5, baser, pgoffset6, index6, pgoffset9, addr, valueAtAddr, valueAtBaseR;
		Scanner in = new Scanner(System.in);
		short oldpc = machine.getPC();
		machine.incrementPC();
		switch (op) {
			case ADD:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				sr2 = SummiX_Utilities.getBits(data, 13, 3);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												+ machine.loadRegister(sr2)));
				//System.out.println("ADD1\tdr:" + dr  + " sr1: " + sr1 + "(" + Integer.toHexString(machine.loadRegister(sr1)) + ")+ sr2:" + sr2 + "("+Integer.toHexString(machine.loadRegister(sr2))+") value:" + Integer.toHexString(machine.loadRegister(dr)));
				break;
			case ADD2:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				
				if (SummiX_Utilities.getBits(data, 11, 1)==1) {
					//negative value
					imm5 = (short) (SummiX_Utilities.getBits(data, 11, 5) << 27 >> 27);
				} else {
					imm5 = (short) (SummiX_Utilities.getBits(data, 11, 5));
				}
			
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												+ imm5));
				//System.out.println("ADD2\t dr:" + dr  + " sr1: " + sr1 + "(" + Integer.toHexString(machine.loadRegister(sr1)) + ")+ imm5:" + imm5 + " value:" + Integer.toHexString(machine.loadRegister(dr)));
				break;
			case AND:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				sr2 = SummiX_Utilities.getBits(data, 12, 3);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												& machine.loadRegister(sr2)));
				//System.out.println("AND:\t" + Integer.toHexString(machine.loadRegister(dr)));
				break;
			case AND2:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				imm5 = SummiX_Utilities.getBits(data, 11, 5);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												& imm5));
				//System.out.println("AND2:\t" + Integer.toHexString(machine.loadRegister(dr)));
				break;
			case BRX: 
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				//System.out.println("BRX\tZ: " + machine.getZ());
				if ((SummiX_Utilities.getBits(data, 4, 1) == 1) && machine.getN() ||  
					(SummiX_Utilities.getBits(data, 5, 1) == 1) && machine.getZ() || 
					(SummiX_Utilities.getBits(data, 6, 1) == 1) && machine.getP()) { 
					//if any of the above cases are true set the pc
					machine.setPC((short) (SummiX_Utilities.getAbsoluteBits(oldpc, 0, 7) + pgoffset9));
				}
				break;
			case DBUG: //The DBUG instruction displays the contents of PC, general registers, and ccr to the console
				System.out.println("SummiX system debug: \n");
				machine.outputMachineState();
			case JSR:
				if (SummiX_Utilities.getBits(data, 4, 1) == 1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				//jump to first 7 bits of PC plus 9 given by offset
				machine.setPC((short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(), 0, 7) + pgoffset9));
				break;
			case JSRR:
				if (SummiX_Utilities.getBits(data, 4, 1) == 1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				pgoffset6 = (short) (SummiX_Utilities.getBits(data, 10, 6));
				machine.setPC((short) (pgoffset6 + machine.loadRegister(SummiX_Utilities.getBits(data, 7, 3))));
				break;
			case LD:
				dr = SummiX_Utilities.getBits(data, 4, 3);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				addr = (short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(), 0, 7) + pgoffset9);
				valueAtAddr = machine.loadMemory(SummiX_Utilities.getBits(addr, 0, 7), SummiX_Utilities.getBits(addr, 7, 9));
				machine.setRegister(dr, valueAtAddr);
				//System.out.println("LD\tdr: " + dr + " set to: " + Integer.toHexString(machine.loadRegister(dr)));
				//System.out.println("LD\tdr: " + dr + " set to: " + Integer.toHexString(machine.loadRegister(dr)));
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
				valueAtBaseR = machine.loadRegister(baser);
				addr = (short) (index6 + valueAtBaseR);			
				valueAtAddr = machine.loadMemory(SummiX_Utilities.getBits(addr, 0, 7), SummiX_Utilities.getBits(addr, 7, 9));				
				dr = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, valueAtAddr);
				//System.out.println("LDR\t" + dr + "(" + Integer.toHexString(machine.loadRegister(dr)) + ") baser:"+baser + "(" + Integer.toHexString(machine.loadRegister(baser)) + ")");
				break;
			case LEA:
				//15:9 pc + 8:0 pgoffset9
				dr = SummiX_Utilities.getBits(data, 4, 3);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				machine.setRegister(dr, (short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(),0,7) + pgoffset9));
				//System.out.println("LEA\t" + "dr:"+dr + " ("+Integer.toHexString(machine.loadRegister(dr))+")");
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
				valueAtBaseR = machine.loadRegister(baser);
				sr = SummiX_Utilities.getBits(data, 4, 3);
				addr = (short) (index6 + valueAtBaseR);
				valueAtAddr = machine.loadMemory(SummiX_Utilities.getBits(addr, 0, 7), SummiX_Utilities.getBits(addr, 7, 9));
				machine.setMemory(SummiX_Utilities.getBits(valueAtAddr, 0, 7), SummiX_Utilities.getBits(valueAtAddr, 7, 9), machine.loadRegister(sr));
				break;
			//Trap instructions below
			case OUT:
				System.out.print((char)SummiX_Utilities.getBits(machine.loadRegister(0), 8, 8));
				break;
			case PUTS:
				//from test.txt (lab2 ex) we get an inifite loop here, printing ? symbols... odd
				char tempChar;
				short memorySpaceToLoadFrom = machine.loadRegister(0);
				short page = SummiX_Utilities.getBits(memorySpaceToLoadFrom, 0, 7);
				short offset = SummiX_Utilities.getBits(memorySpaceToLoadFrom, 7, 9);
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
					offset = SummiX_Utilities.getBits(memorySpaceToLoadFrom, 7, 9);
					tempChar = (char) machine.loadMemory(page, offset);
				}
				break;
			case IN:
				System.out.print("Please input character to be stored in R0: ");
				char ascii = in.next().charAt(0);
				System.out.println(ascii);
				//machine.setRegister(0,((short) ((ascii << 8) >>> 8))); 
				machine.setRegister(0,(short)ascii); // may have to use above statement to clear upper 8 bits
				break;
			case HALT:
				//handled by Interpreter
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
				System.out.println("System error.");
				break;
		}
	}
}


