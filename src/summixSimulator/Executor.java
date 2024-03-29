package summixSimulator;

import java.util.Random;
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
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("ADD\tdr: " + dr  + "\tsr1: " + sr1 + "\tsr2: " + sr2);
				}
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												+ machine.loadRegister(sr2)));
				break;
			case ADD2:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				
				if (SummiX_Utilities.getBits(data, 11, 1)==1) {
					//negative values
					imm5 = (short) (SummiX_Utilities.getBits(data, 11, 5) << 27 >> 27);
				} else {
					imm5 = (short) (SummiX_Utilities.getBits(data, 11, 5));
				}
			
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("ADD\tdr: " + dr  + "\tsr1: " + sr1 + "\timm5: " + imm5);
				}
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												+ imm5));
				
				break;
			case AND:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				sr2 = SummiX_Utilities.getBits(data, 12, 3);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("AND" + "\tdr: " + dr + "\tsr1: " + sr1 + "\tsr2: " + sr2);
				}
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												& machine.loadRegister(sr2)));
				break;
			case AND2:
				sr1 = SummiX_Utilities.getBits(data, 7, 3);
				imm5 = SummiX_Utilities.getBits(data, 11, 5);
				dr  = SummiX_Utilities.getBits(data, 4, 3);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("AND" + "\tdr: " + dr + "\tsr1: " + sr1 + "\timm5: " + imm5);
				}				
				machine.setRegister(dr, (short) (machine.loadRegister(sr1) 
												& imm5));
				break;
			case BRX: 
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					String branchCommand = "BR";
					if (SummiX_Utilities.getBits(data, 4, 1)==1) {
						branchCommand = branchCommand + "N";
					}
					if (SummiX_Utilities.getBits(data, 5, 1)==1) {
						branchCommand = branchCommand + "Z";
					}
					if (SummiX_Utilities.getBits(data, 6, 1)==1) {
						branchCommand = branchCommand + "P";
					}
					if ((SummiX_Utilities.getBits(data, 6, 1)==1) && (SummiX_Utilities.getBits(data, 5, 1)==1) && (SummiX_Utilities.getBits(data, 4, 1)==1)) {
						branchCommand = "BRA";
					}
					if ((SummiX_Utilities.getBits(data, 6, 1)==0) && (SummiX_Utilities.getBits(data, 5, 1)==0) && (SummiX_Utilities.getBits(data, 4, 1)==0)) {
						branchCommand = "NOP";
					}
					System.out.println(branchCommand + "\tpgoffset9: " + pgoffset9);
				}		
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
				break;
			case JSR:
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("JSR" + "\tpgoffset9: " + pgoffset9);
				}
				if (SummiX_Utilities.getBits(data, 4, 1) == 1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				//jump to first 7 bits of PC plus 9 given by offset
				machine.setPC((short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(), 0, 7) + pgoffset9));
				break;
			case JSRR:
				pgoffset6 = (short) (SummiX_Utilities.getBits(data, 10, 6));
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("JSRR" + "\tpgoffset6: " + pgoffset6);
				}
				if (SummiX_Utilities.getBits(data, 4, 1) == 1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				machine.setPC((short) (pgoffset6 + machine.loadRegister(SummiX_Utilities.getBits(data, 7, 3))));
				break;
			case LD:
				dr = SummiX_Utilities.getBits(data, 4, 3);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("LD" + "\tdr1: " + dr + "\tpgoffset9: " + pgoffset9);
				}
				addr = (short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(), 0, 7) + pgoffset9);
				valueAtAddr = machine.loadMemory(SummiX_Utilities.getBits(addr, 0, 7), SummiX_Utilities.getBits(addr, 7, 9));
				machine.setRegister(dr, valueAtAddr);
				//System.out.println("LD\tdr: " + dr + " set to: " + Integer.toHexString(machine.loadRegister(dr)));
				//System.out.println("LD\tdr: " + dr + " set to: " + Integer.toHexString(machine.loadRegister(dr)));
				break;
			case LDI:
				dr = SummiX_Utilities.getBits(data, 4, 3);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("LDI" + "\tdr: " + dr + "\tpgoffset9: " + pgoffset9);
				}
				machine.setRegister(dr, (short) (SummiX_Utilities.getAbsoluteBits(machine.loadRegister(dr), 0, 7) + pgoffset9));				
				break;
			case LDR:
				//zero extend  index6 and add it to value in BaseR
				index6 = SummiX_Utilities.getBits(data, 10, 6); //zero extend index6
				baser = SummiX_Utilities.getBits(data, 7, 3);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("LDR" + "\tindex6: " + index6 + "\tbaser: " + baser);
				}
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
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("LEA" + "\tdr: " + dr + "\tpgoffset9: " + pgoffset9);
				}
				machine.setRegister(dr, (short) (SummiX_Utilities.getAbsoluteBits(machine.getPC(),0,7) + pgoffset9));
				//System.out.println("LEA\t" + "dr:"+dr + " ("+Integer.toHexString(machine.loadRegister(dr))+")");
				break;
			case NOT:
				dr = SummiX_Utilities.getBits(data, 4, 3); // Get destination register
				sr = SummiX_Utilities.getBits(data, 7, 3); // Get source register
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("NOT" + "\tdr: " + dr + "\tsr: " + sr);
				}
				sr = (short) ~machine.loadRegister(sr); //Bitwise inversion of the value in source register
				machine.setRegister(dr, sr); //Store data from source register into destination register
				break;
			case RET:
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("RET");
				}
				machine.setPC(machine.loadRegister(7)); // copies the contents of R7 to PC
				break;
			case ST:
				sr = SummiX_Utilities.getBits(data, 4, 3);
				pg = SummiX_Utilities.getBits(machine.getPC(), 0, 7);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("ST" + "\tsr: " + sr + "\tpg: " + pg + "\tpgoffset9: " + pgoffset9);
				}
				machine.setMemory(pg, pgoffset9, machine.loadRegister(sr));
				break;
			case STI:
				sr = SummiX_Utilities.getBits(data, 4, 3);
				pg = SummiX_Utilities.getBits(sr, 0, 7);
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("STI" + "\tsr: " + sr + "\tpg: " + pg + "\tpgoffset9: " + pgoffset9);
				}
				machine.setMemory(pg, pgoffset9, machine.loadRegister(sr));
				break;
			case STR:
				//zero extend  index6 and add it to value in BaseR
				index6 = SummiX_Utilities.getBits(data, 10, 6);
				baser = SummiX_Utilities.getBits(data, 7, 3);
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("STR" + "\tindex6: " + index6 + "\tbaser: " + baser);
				}
				valueAtBaseR = machine.loadRegister(baser);
				sr = SummiX_Utilities.getBits(data, 4, 3);
				addr = (short) (index6 + valueAtBaseR);
				valueAtAddr = machine.loadMemory(SummiX_Utilities.getBits(addr, 0, 7), SummiX_Utilities.getBits(addr, 7, 9));
				machine.setMemory(SummiX_Utilities.getBits(valueAtAddr, 0, 7), SummiX_Utilities.getBits(valueAtAddr, 7, 9), machine.loadRegister(sr));
				break;
			//Trap instructions below
			case OUT:
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("OUT");
				}
				System.out.print((char)SummiX_Utilities.getBits(machine.loadRegister(0), 8, 8));
				break;
			case PUTS:
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("PUTS");
				}
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
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("IN");
				}
				System.out.print("Please input character to be stored in R0: ");
				char ascii = in.next().charAt(0);
				System.out.println(ascii);
				//machine.setRegister(0,((short) ((ascii << 8) >>> 8))); 
				machine.setRegister(0,(short)ascii); // may have to use above statement to clear upper 8 bits
				break;
			case HALT:
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("HALT");
				}
				//handled by Interpreter
				break;
			case OUTN:  //write value of r0 to console as a decimal
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("OUTN");
				}
				System.out.print(machine.loadRegister(0));
				break;
			case INN:
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("INN");
				}
				int input = 0;
				String numericalInput;
				System.out.print("Please enter a number between -32768 and 32767 with no commas: ");
				numericalInput = in.next();
				input = Integer.parseInt(numericalInput);
				machine.setRegister(0, (short)input);
				break;
			case RND:
				if ((machine.getSimState()) == SummiX_Utilities.Simulator_State.STEP || (machine.getSimState() == SummiX_Utilities.Simulator_State.TRACE)) {
					System.out.println("RND");
				}
	    		Random randomNumbers = new Random();
	    		int ran = (short) randomNumbers.nextInt(); //random int within the range of 16 bits
				machine.setRegister(0, (short) ran);
				break; 
			case ERR: //was an error op code
				System.out.println("System error.");
				break;
		}
	}
}


