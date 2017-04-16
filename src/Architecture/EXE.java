package Architecture;

import Architecture.ID.IDWrapper;

public class EXE {	
	int[] registers;
	IDWrapper idWrapper;
	int exeValue;
	public EXE(int[] regs) {
		registers = regs;
	}

	public void execute(IDWrapper w) {
		idWrapper = w;
		if(w == null)
			return;
		//____________________________________________________________
		if(w.operation == null)
			return;
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("nop")){
			return; //no operation
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("lw")){
			exeValue = (registers[w.reg3] - 0x1000) + (w.imm/4);		//for lw/sw subtract 0x1000 to get to beginning of memory 
		}																//divide the immediate by 4 to get integer index
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("sw")){
			exeValue = (registers[w.reg3] - 0x1000) + (w.imm/4);
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("add")){
			exeValue = registers[w.reg2] + registers[w.reg3];
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("sub")){
			exeValue = registers[w.reg2] - registers[w.reg3];
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("addi")){
			exeValue = registers[w.reg2] + w.imm;
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("bne")){
			return; //resolved in the ID stage
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("beq")){
			return; //resolved in the ID stage
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("and")){
			exeValue = registers[w.reg2] & registers[w.reg3];
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("or")){
			exeValue = registers[w.reg2] | registers[w.reg3];
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("nor")){
			exeValue = ~(registers[w.reg2] | registers[w.reg3]);
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("xor")){
			exeValue = registers[w.reg2] ^ registers[w.reg3];
		}
		//____________________________________________________________	
	}
	
	public EXEWrapper getEXEWrapper(){
		return new EXEWrapper(exeValue, idWrapper);
	}
	
	public class EXEWrapper{
		public int exeValue;
		public IDWrapper idWrapper;
		public EXEWrapper(int wbVal, IDWrapper w){
			exeValue = wbVal;
			idWrapper = w;
		}
	}
}
