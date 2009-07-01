package summixSimulator;

import summixSimulator.SummiX_Utilities.InstructionCode;

public class Executor {
	public Executor(SummiX_Machine machine, short data, InstructionCode op) {
		short sr1, sr2, dr, imm5, pc, pgoffset9, nzp;
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
				nzp = SummiX_Utilities.getBits(data, 4, 3);
				
				if (nzp == 1){  // n bit
					
				}
				else if (nzp == 2){  // z bit
					
				}
				else if (nzp == 4){  // p bit
					
				}
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


