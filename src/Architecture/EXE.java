package Architecture;

import Architecture.ID.IDWrapper;
import Architecture.MEM.MEMWrapper;

public class EXE {	
	int[] registers;
	IDWrapper idWrapper;
	int exeValue;
	public EXE(int[] regs) {
		registers = regs;
	}

	//w is instruction currently being executed
	//exeWrapper contains forwarding from MEM stage
	//memWrapper contains forwarding from Wb stage
	public void execute(IDWrapper w, EXEWrapper exeWrapper, MEMWrapper memWrapper){
		String exeOperation = "";
		int exeDependentReg = -1;
		int exeForwardedValue = 0;
		
		String memOperation = "";
		int memDependentReg = -1;
		int memForwardedValue = 0;
		
		try{
			exeOperation = exeWrapper.idWrapper.operation;
			exeDependentReg = exeWrapper.idWrapper.reg1;
			exeForwardedValue = exeWrapper.exeValue;
		}catch(NullPointerException e){
			//if we havn't reached the EXE stage then do nothing
		}
		
		try{
			memOperation = memWrapper.exeWrapper.idWrapper.operation;
			memDependentReg = memWrapper.exeWrapper.idWrapper.reg1;
			memForwardedValue = memWrapper.memValue;
		}catch(NullPointerException e){
			//if we havn't reached the MEM stage then do nothing
		}
																					//Theres some cludgy copy-pasta that should be re-factored here
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
			boolean forwardedFromMEM = false;
			boolean forwardedFromWB = false;
			exeValue = (registers[w.reg3] - 0x1000)/4 + (w.imm/4); //for lw/sw subtract 0x1000 to get to beginning of memory then divide by 4 to get index into memory
																 //divide the immediate by 4 to get integer index
			
			if(w.reg3 == exeDependentReg){						 //check for forwarded results
				exeValue = (exeForwardedValue - 0x1000)/4 + (w.imm/4);
				forwardedFromMEM = true;
			}
			if(w.reg3 == memDependentReg){
				exeValue = (memForwardedValue - 0x1000)/4 + (w.imm/4);
				forwardedFromWB = true;
				System.out.println("Forwarded " + memForwardedValue + " to reg3 from WB to EXE");
			}
			
			if(forwardedFromMEM && !forwardedFromWB)
				System.out.println("Forwarded " + memForwardedValue + " to reg3 from MEM to EXE");
				
		}																
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("sw")){
			boolean forwardedFromMEM = false;
			boolean forwardedFromWB = false;
			exeValue = (registers[w.reg3] - 0x1000)/4 + (w.imm/4);
			
			if(w.reg3 == exeDependentReg){						 //check for forwarded results
				exeValue = (exeForwardedValue - 0x1000)/4 + (w.imm/4);
				forwardedFromMEM = true;
			}
			if(w.reg3 == memDependentReg){
				exeValue = (memForwardedValue - 0x1000)/4 + (w.imm/4);		
				forwardedFromWB = true;
				System.out.println("Forwarded " + memForwardedValue + " to reg3 from WB to EXE");
			}
			
			if(forwardedFromMEM && !forwardedFromWB)
				System.out.println("Forwarded " + memForwardedValue + " to reg3 from MEM to EXE");
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("add")){
			int reg2 = getRTypeRegister(2, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);
			int reg3 = getRTypeRegister(3, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);	
	
			exeValue = reg2 + reg3;
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("sub")){
			int reg2 = getRTypeRegister(2, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);
			int reg3 = getRTypeRegister(3, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);	
			
			exeValue = reg2 - reg3;
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("addi")){
			exeValue = registers[w.reg2] + w.imm;
			boolean forwardedFromMEM = false;
			boolean forwardedFromWB = false;
			
			if(exeOperation != null)
				if(!exeOperation.equals("sw"))
					if(w.reg2 == exeDependentReg){						 //check for forwarded results
						exeValue = exeForwardedValue + w.imm;
						forwardedFromMEM = true;
					}
			if(memOperation != null)
				if(!memOperation.equals("sw"))
					if(w.reg2 == memDependentReg){
						exeValue = memForwardedValue + w.imm;	
						forwardedFromWB = true;
						System.out.println("Forwarded " + memForwardedValue + " to reg2 from WB to EXE");
					}
			
			if(forwardedFromMEM && !forwardedFromWB)
				System.out.println("Forwarded " + memForwardedValue + " to reg2 from MEM to EXE");
				
			
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
			int reg2 = getRTypeRegister(2, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);
			int reg3 = getRTypeRegister(3, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);	
	
			exeValue = reg2 & reg3;
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("or")){
			int reg2 = getRTypeRegister(2, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);
			int reg3 = getRTypeRegister(3, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);	
	
			exeValue = reg2 | reg3;	
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("nor")){
			int reg2 = getRTypeRegister(2, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);
			int reg3 = getRTypeRegister(3, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);	
	
			exeValue = ~(reg2 | reg3);	
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("xor")){
			int reg2 = getRTypeRegister(2, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);
			int reg3 = getRTypeRegister(3, w, exeOperation, memOperation, exeDependentReg, exeForwardedValue, memDependentReg, memForwardedValue);	
	
			exeValue = reg2 ^ reg3;
		}
		//____________________________________________________________	
	}
	private int getRTypeRegister(int regNumber, IDWrapper w, String exeOperation, String memOperation, int exeDependentReg, int exeForwardedValue, int memDependentReg, int memForwardedValue){
		int reg2 = registers[w.reg2];									 //load register values as defaults
		int reg3 = registers[w.reg3];
		boolean reg2SetFlag = false;
		boolean reg3SetFlag = false;
		//check EXE stage first as it'll have the most recent values to be forwarded
		if(!exeOperation.equals("sw") && !memOperation.equals("beq") && !memOperation.equals("bne")){ //check for forwarded results
			if(w.reg3 == exeDependentReg && !reg3SetFlag){
				reg3 = exeForwardedValue;
				if(regNumber == 3)
					System.out.println("Forwarded " + reg3 + " to reg3 from MEM to EXE");
				reg3SetFlag = true;
			}
			if(w.reg2 == exeDependentReg && !reg2SetFlag){
				reg2 = exeForwardedValue;
				if(regNumber == 2)
					System.out.println("Forwarded " + reg2 + " to reg2 from MEM to EXE");
				reg2SetFlag = true;
			}
		}	
		
		if(!memOperation.equals("sw") && !memOperation.equals("beq") && !memOperation.equals("bne")){				
			if(w.reg3 == memDependentReg && !reg3SetFlag){
				reg3 = memForwardedValue;
				if(regNumber == 3)
					System.out.println("Forwarded " + reg3 + " to reg3 from WB to EXE");
				reg3SetFlag = true;
			}
			if(w.reg2 == memDependentReg && !reg2SetFlag){
				reg2 = memForwardedValue;
				if(regNumber == 2)
					System.out.println("Forwarded " + reg2 + " to reg2 from WB to EXE");
				reg2SetFlag = true;
			}
		}
		
		if(regNumber == 2)
			return reg2;
		if(regNumber == 3)
			return reg3;
		return -1;		
	}
	
	public EXEWrapper getEXEWrapper(){
		return new EXEWrapper(exeValue, idWrapper);
	}
	
	public EXEWrapper getNOPEXEWrapper(){
		ID id = new ID(null, null);
		return new EXEWrapper(exeValue, id.getNOPIDWrapper());
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
