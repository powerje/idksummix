package summixSimulator;

import java.util.BitSet;

import summixSimulator.SummiX_Utilities.InstructionCode;

public class Executor {
	public Executor(SummiX_Machine machine, short data, InstructionCode op) {
		short sr1, sr2, dr, imm5, pc, pgoffset9;
		
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
				BitSet ccr = machine.getCCR();
				pc = machine.getPC();
				pgoffset9 = SummiX_Utilities.getBits(data, 7, 9);
				
				if ((SummiX_Utilities.getBits(data, 4, 1) == 1) && ccr.get(0) ||  // n bit
					(SummiX_Utilities.getBits(data, 4, 2) == 1) && ccr.get(1) ||  // z bit
					(SummiX_Utilities.getBits(data, 4, 3) == 1) && ccr.get(2)) {  // p bit
					//if any of the above cases are true set the pc
					machine.setPC((short) (pc + pgoffset9));
				}
				//else it is a nop
				break;
			case DBUG:
				break;
			case JSR:
				break;
			case JSRR:
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


