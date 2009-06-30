package summixSimulator;

import summixSimulator.SummiX_Utilities.InstructionCode;

public class Executor {
	public Executor(SummiX_Machine machine, short data, InstructionCode op) {
		switch (op) {
			case ADD:
				short sr1 = SummiX_Utilities.getBits(data, 7, 3);
				short sr2 = SummiX_Utilities.getBits(data, 12, 3);
				short dr  = SummiX_Utilities.getBits(data, 4, 3);
				machine.setRegister(dr, (short) (sr1 + sr2));
				break;
			case ADD2:
				break;
			case AND:
				break;
			case AND2:
				break;
			case BRN:
				break;
			case BRZ:
				break;
			case BRP:
				break;
			case BRA:
				break;
			case BRGE:
				break;
			case BRLE:
				break;
			case NOP:
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


