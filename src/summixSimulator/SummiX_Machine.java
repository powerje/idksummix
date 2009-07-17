package summixSimulator;

import java.util.BitSet;
import java.util.Random;
import summixSimulator.SummiX_Utilities.Simulator_State;

/**
 * SummiX_Machine consists of:
 * Memory (mem): Memory is word addressable.  There are
 * 2^16 words of memory, addresses 0-65,535.  Memory is
 * organized into pages of 512 words.  There are 128 pages.
 * A memory address is given by a 16-bit quantity where the
 * upper 7 bits denote the page and the lower 9 bits denote
 * the offset within that page.
 *
 * Registers (reg and pc):
 * There are 8 general purposes registers (R0-R7) and
 * one program counter (PC).
 * 
 * Condition Code Registers (ccr):
 * The CCR contains 3 bits, N, Z, and P.  They are all
 * updated (set or cleared) every time a value is written
 * to a general purpose register except for JSR/JSRR
 * instructions.  The N bit is set to 1 iff the last
 * value written to the register was negative, Z is set
 * to 1 if the last value was 0, and P if it was positive.
 */

public class SummiX_Machine {

	
	private short[][]		mem	= new short[128][512];		//array to represent memory (0-127 pages, 0-511 words per page)
	private short[]			reg	= {0,0,0,0,0,0,0,0};		//initialize all registers to 0
	private short			pc	= 0;						//program counter starts at 0
	private BitSet			ccr	= new BitSet(3);			//N,Z,P = 0,1,0 (all registers are set to 0) initially
	private final int   	N	= 0, Z = 1, P = 2;
	private Simulator_State	simState;
	
	private void randomizeMemory() {
		/**
		 * Randomize memory which maybe useful for debugging later
		 */
	    for (int i = 0; i < 128; i++) {
	    	for (int j = 0; j < 512; j++) {
	    		Random randomNumbers = new Random();
	    		mem[i][j] = (short) randomNumbers.nextInt(); //random int within the range of 16 bits
	    	}	
	   	} 
	}
	
	public SummiX_Machine() {
		randomizeMemory();
		this.ccr.set(Z);
	}
	
	public Simulator_State getSimState() {
		return simState;
	}
	public void setSimState(Simulator_State simState) {
		this.simState = simState;
	}
	
	public void setMemory(short page, short offset, short data) {
		/**
		 * Sets memory at mem[page][offset]
		 * 
		 * @param page the page of memory
		 * @param offset the offset within the page
		 * @param data the data to store
		 */
		this.mem[page][offset] = data;
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			System.out.println("M[" + page + "][" + offset + "] = " + SummiX_Utilities.shortToHexString(data));
		}
	}
	
	public short loadMemory(short page, short offset) {
		/**
		 * Get data from mem[page][offset] and return it
		 * 
		 * @param page the page of memory
		 * @param offset the offset within the page
		 * @return data the value stored at the desired location in memory
		 */
		short data = 0;
		try {
			data = this.mem[page][offset];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: memory out of bounds exception - page: " + page + ",offset: " + offset);
			System.exit(-1);
		}
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			System.out.println("M[" + page + "][" + offset + "] = " + SummiX_Utilities.shortToHexString(data));
		}
		return data;
	}
	
	public boolean getN() {
		return this.ccr.get(N);
	}
	
	public boolean getZ() {
		return this.ccr.get(Z);
	}
	
	public boolean getP() {
		return this.ccr.get(P);
	}
	public void setPC(short addr) {
		/**
		 * Sets the PC to addr
		 * 
		 * @param addr address to be written to PC
		 */
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			System.out.println("PC = " + SummiX_Utilities.shortToHexString(addr));
		}
		this.pc = addr;
	}
	
	public void incrementPC() {
		/**
		 * Increments the PC
		 */
		short page = SummiX_Utilities.getBits(this.pc, 0, 7);
		short offset = SummiX_Utilities.getBits(this.pc, 8, 9);
		
		if (offset==511) {
			page = (short) (((short) (page >>> 9) + 1) << 9);
			this.pc = page;
		} else {
			this.pc++;
		}		
	}
	
	public short getPC() {
		return this.pc;
	}
	
	public int getPage() {
		return SummiX_Utilities.getBits(this.pc, 0, 7);
	}
	
	public int getOffset() {
		return SummiX_Utilities.getBits(this.pc, 7, 9);
	}
	
	public void setRegister(int register, short data) {
		/**
		 * Sets the given register with given data and updates the
		 * CCR accordingly
		 * 
		 * @param register the register to be set
		 * @param data the data to store in the register
		 */
		//store data into register
		this.reg[register] = data;
		//always update CCR
		
		this.ccr.clear();
		
		if (data < 0) {
			this.ccr.set(N);
		} else if (data == 0) {
			this.ccr.set(Z);
		} else {
			this.ccr.set(P);
		}
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			System.out.println("R" + register + " = " + SummiX_Utilities.shortToHexString(data));

			System.out.print("CCR: ");
			if (this.ccr.get(N)) {
				System.out.print("1");
			}
			else {
				System.out.print("0");
			}
			if (this.ccr.get(Z)) {
				System.out.print("1");
			}
			else {
				System.out.print("0");
			}
			if (this.ccr.get(P)) {
				System.out.print("1");
			}
			else {
				System.out.print("0");
			}
			System.out.println();	
		}
		
	}
	
	public void setSubroutineReturn(short addr) {
		/**
		 * For the special case of setting register 7 without changing the CCR
		 * as for JSR/JSRR with the link bit set to 1.
		 * 
		 * @param addr the return address to store in register 7
		 */
		this.reg[7] = addr;
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			System.out.println("R7 = " + SummiX_Utilities.shortToHexString(addr));
		}
	}
	
	public short loadRegister(int i) {
		/**
		 * Loads data from a register
		 * 
		 * @param register the register to load data from
		 * @return data the value stored in the specified register
		 */
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			System.out.println("R" + i + " = " + SummiX_Utilities.shortToHexString(this.reg[i]));
		}
		return this.reg[i];
	}
	
	public void outputMemoryPage(int page) {
		System.out.println("Memory Page: " + page);
	   	for (int i = 0; i < 512; i++) {
	   		short data = mem[page][i];
	   		String output = SummiX_Utilities.shortToHexString(data);
	   		System.out.print("|" + i + ": " + output + "\t");
	   		if ((i % 9 == 0) && (i > 0)) {
	   			System.out.println();
	   		}
	   	}
	   	System.out.println();
	}
	
	public void outputMachineState() {
		System.out.println();
		for (int i=0;i < 8;i++) { //print general registers
			System.out.print("| R" + i + ": " + SummiX_Utilities.shortToHexString(this.reg[i]) + "\t");
		}
		//output PC and current instruction
		short instrAtPC = this.mem[SummiX_Utilities.getBits(this.pc, 0, 7)][SummiX_Utilities.getBits(this.pc, 7, 9)];
		System.out.print("|\n| PC: 0x" + Integer.toHexString((int)this.pc) + "\t| Instr: " + SummiX_Utilities.shortToHexString(instrAtPC)+ "\t|");
		//output CCR
		System.out.print(" CCR: ");
		if (this.ccr.get(N)) {
			System.out.print("1");
		}
		else {
			System.out.print("0");
		}
		if (this.ccr.get(Z)) {
			System.out.print("1");
		}
		else {
			System.out.print("0");
		}
		if (this.ccr.get(P)) {
			System.out.print("1");
		}
		else {
			System.out.print("0");
		}
		System.out.print("\t|\t");
	}
}
