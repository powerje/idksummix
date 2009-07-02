package summixSimulator;

import java.util.BitSet;

import summixSimulator.SummiX_Utilities.InstructionCode;

public class Executor {
	public Executor(SummiX_Machine machine, short data, InstructionCode op) {
		short sr1, sr2, dr, imm5, pc, pgoffset6, pgoffset9;
		BitSet ccr = machine.getCCR();
		
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
				pc = machine.getPC();
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				//ccr is set above to machine.getCCR()
				if ((SummiX_Utilities.getBits(data, 4, 1) == 1) && ccr.get(0) ||  // n bit
					(SummiX_Utilities.getBits(data, 4, 2) == 1) && ccr.get(1) ||  // z bit
					(SummiX_Utilities.getBits(data, 4, 3) == 1) && ccr.get(2)) {  // p bit
					//if any of the above cases are true set the pc
					machine.setPC((short) (pc + pgoffset9));
				}
				//else it is a nop
				break;
			case DBUG: //The DBUG instruction displays the contents of PC, general registers, and ccr to the console
				for (int i=0;i < 8;i++) { //print general registers
					System.out.print("|R" + i + ": " + machine.loadRegister((short)i) + "\t|");
				}
				System.out.print("\n|PC: " + machine.getPC() + "\t|\n");
				//ccr is set above to machine.getCCR()
				System.out.print("CCR: N - " + ccr.get(0) + " Z - " + ccr.get(1) + " P - " + ccr.get(2));
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
				if (SummiX_Utilities.getBits(data, 4, 1)==1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				//jump to first 7 bits of PC plus 9 given by offset
				machine.setPC((short) (SummiX_Utilities.getBits(machine.getPC(), 0, 7) + pgoffset9));
				break;
			case JSRR: //someone read the directions for this and check to make sure i didn't mess it up
				if (SummiX_Utilities.getBits(data, 4, 1)==1) { // link bit is set
					machine.setSubroutineReturn(machine.getPC()); //so set r7 to current pc for return
				}
				pgoffset6 = (short) (SummiX_Utilities.getBits(data, 10, 6) << 9); //zero-extend the offset
				machine.setPC((short) (pgoffset6 + SummiX_Utilities.getBits(data, 7, 3)));
				break;
			case LD:
				break;
			case LDI:
				break;
			case LDR:
				break;
			case LEA:
				break;
			case NOT:
				break;
			case RET:
				break;
			case ST:
				break;
			case STI:
				break;
			case STR:
				break;
			case OUT:
				break;
			case PUTS:
				break;
			case IN:
				break;
			case HALT:
				break;
			case OUTN:
				break;
			case INN:
				break;
			case RND:
				break; 			
		}
	}
}


