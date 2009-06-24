package summixSimulator;

public class SummiX_Machine {
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
	
	private short[][]	mem	= new short[127][511];		//array to represent memory (0-127 pages, 0-511 words per page)
	private short[]		reg	= {0,0,0,0,0,0,0,0};		//init all registers to 0
	private short		pc	= 0;						//pc starts at 0
	private boolean[] 	ccr	= {false, true, false};		//N,Z,P = 0,1,0 (all registers are set to 0) initially
	private final int   N	= 0, Z = 1, P = 2;


	
	public SummiX_Machine() {
		// perhaps in the constructor we should set all memory to garbage?
	}

	public void setMemory(short page, short offset, short data) {
		this.mem[page][offset] = data;
	}
	
	public short loadMemory(short page, short offset) {
		return this.mem[page][offset];
	}
	
	public void setPC(short data) {
		this.pc = data;
	}
	
	public void incrementPC() {
		this.pc++;
	}
	
	public void setRegister(short register, short data) {
		/**
		 * 
		 */
		//store data into register
		this.reg[register] = data;
		//always update CCR
		if (data < 0) {
			ccr[N] = true;
		}
		else {
			ccr[N] = false;
		}
		if (data == 0) {
			ccr[Z] = true;
		}
		else {
			ccr[Z] = false;
		}
		if (data > 0) {
			ccr[P] = true;
			}
		else {
			ccr[P] = false;
		}
	}
	
	public short loadRegister(short register) {
		/**
		 * Loads data from a register
		 * 
		 * @param register the register to load data from
		 * @return data the value stored in the specified register
		 */
		return this.reg[register];
	}
	
	public void setSubroutineReturn(short addr) {
		/**
		 * For the special case of setting register 7 without changing the CCR
		 * as for JSR/JSRR with the link bit set to 1.
		 * 
		 * @param addr the return address to store in register 7
		 */
		this.reg[7] = addr;
	}
}
