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
	private short[]		reg	= {0,0,0,0,0,0,0,0};		//initialize all registers to 0
	private short		pc	= 0;						//program counter starts at 0
	private boolean[] 	ccr	= {false, true, false};		//N,Z,P = 0,1,0 (all registers are set to 0) initially
	private final int   N	= 0, Z = 1, P = 2;
	
	private void randomizeMemory() {
		/**
		 * Randomize memory which maybe useful for debugging later
		 */
	    for (int i = 0; i < 127; i++) {
	    	for (int j = 0; j < 511; j++) {
	    		mem[i][j] = (short) Math.random ();
	    	}	
	   	} 
	}
	
	public SummiX_Machine() {
		randomizeMemory();
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
		//System.out.println("page: " + page + "\noffset: " + offset + "\ndata: " + data);
	}
	
	public short loadMemory(short page, short offset) {
		/**
		 * Get data from mem[page][offset] and return it
		 * 
		 * @param page the page of memory
		 * @param offset the offset within the page
		 * @return data the value stored at the desired location in memory
		 */
		return this.mem[page][offset];
	}
	
	public void setPC(short addr) {
		/**
		 * Sets the PC to addr
		 * 
		 * @param addr address to be written to PC
		 */
		this.pc = addr;
	}
	
	public void incrementPC() {
		/**
		 * Increments the PC
		 */
		this.pc++;
	}
	
	public short getPC() {
		return this.pc;
	}
	
	public void setRegister(short register, short data) {
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
		if (data < 0) {
			this.ccr[N] = true;
		}
		else {
			this.ccr[N] = false;
		}
		if (data == 0) {
			this.ccr[Z] = true;
		}
		else {
			this.ccr[Z] = false;
		}
		if (data > 0) {
			this.ccr[P] = true;
			}
		else {
			this.ccr[P] = false;
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
