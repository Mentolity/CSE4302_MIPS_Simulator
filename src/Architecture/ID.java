package Architecture;

import java.util.ArrayList;

import Architecture.EXE.EXEWrapper;
import Architecture.MEM.MEMWrapper;
import Instruction.I_Instruction;
import Instruction.Instruction;
import Instruction.J_Instruction;
import Instruction.R_Instruction;

//Dependencies are calculated in this stage **
//Branch Instructions are resolved in this stage **
public class ID {
	ArrayList<Instruction> instructions; //needs access to instructions to decode immediate jumps
	Instruction currentInstruction;
	String operation;
	int[] registers;
	int reg1, reg2, reg3;
	int imm;
	int branchAddress = -1; //initialized to -1 to indicate no branch is currently being taken
	
	public ID(int[] reg, ArrayList<Instruction> il){
		registers = reg;
		instructions = il;
	}
	
	public int getBranchAddress(){
		int ba = branchAddress;
		branchAddress = -1; //once we check the branch address reset to -1 to indicate branch was taken
		return ba;
	}
	
	//return number of NOPS to insert
	public int detectHazards(IDWrapper idWrapper, EXEWrapper exeWrapper, String operation, int reg1, int reg2, int reg3){//detectHazards after information is pulled in execute method
		//IDWrapper has info for operation currently in EXE stage
		//EXEWrapper has info for operation currently in MEM stage
		//detect hazards in the EXE stage
		try{
			String exeOperation = idWrapper.operation;
			int exeDependentReg = idWrapper.reg1;
			
			//Detect forwarding to ID hazard for branching
			if(operation.equals("bne") || operation.equals("beq")){ //if currentInstruction is a branch check to see if 
				if(!exeOperation.equals("sw") && !exeOperation.equals("bne") && !exeOperation.equals("beq")){
					if(reg1 == exeDependentReg || reg2 == exeDependentReg){
						if(exeOperation.equals("lw"))
							return 2;
						else
							return 1;
					}
				}
			}
			
			//Detect Load/EXE hazard
			//if instruction in EXE stage is lw add NOP and then forward results from MEM to EXE stage
			if(exeOperation.equals("lw")){
				if(reg2 == exeDependentReg || reg3 == exeDependentReg)
					return 1;
			}
		}catch(NullPointerException e){ //if EXE stage hasn't been reached yet there can't be a hazard
			return 0;
		}
		
		//detect hazards in the MEM stage
		try{
			String memOperation =  exeWrapper.idWrapper.operation;
			int memDependentReg = exeWrapper.idWrapper.reg1;

			//Detect forwarding to ID hazard for branching
			if(operation.equals("bne") || operation.equals("beq")){ //if currentInstruction is a branch check to see if 				
				if(memOperation.equals("lw") && (reg1 == memDependentReg || reg2 == memDependentReg))
					return 1;
			}
		}catch(NullPointerException e){
			return 0;
		}
		
		return 0;
	}
	
	public int execute(Instruction currentInstruction, IDWrapper idWrapper, EXEWrapper exeWrapper, MEMWrapper memWrapper){//return number of NOPs to insert
		//If there is a dependency instructions will move down pipeline such that 
		//EXEWrapper has info for EXE dependencies
		//MEMWrapper has info for MEM dependencies
		
		reg1 = -1;
		reg2 = -1;
		reg3 = -1;
		imm = -1;
		this.currentInstruction = currentInstruction;
		if(currentInstruction instanceof R_Instruction){
			operation = currentInstruction.getInstructionName();
			reg1 = getRegisterNumber(((R_Instruction)currentInstruction).getRS());
			reg2 = getRegisterNumber(((R_Instruction)currentInstruction).getRT());
			reg3 = getRegisterNumber(((R_Instruction)currentInstruction).getRD());
		}
		
		if(currentInstruction instanceof I_Instruction){
			operation = currentInstruction.getInstructionName();
			
			reg1 = getRegisterNumber(((I_Instruction)currentInstruction).getRS());
			reg2 = getRegisterNumber(((I_Instruction)currentInstruction).getRT());
			
			//Calculate Immediate values in the execution state
			//this entails moving the parseImmediate function to that stage
			imm = parseImmediate(((I_Instruction)currentInstruction).getIMM());//offset
			reg3 = parseLWSWRegister(((I_Instruction)currentInstruction).getIMM());//register of LW/SW instruction
			
			//___________________________________________
			//Resolving branches
			if(currentInstruction.getInstructionName().equals("bne") || currentInstruction.getInstructionName().equals("beq")){//if not a branch instruction don't bother looking for branch dependencies
				branchAddress = -1;//if no branch is taken then set branchAddress to -1 to indicate the the PC isn't changed
				boolean reg1DependencyFound = false;
				boolean reg2DependencyFound = false;
				int reg1Temp = registers[reg1];
				int reg2Temp = registers[reg2];
				
				//check for dependency in the EXE Stage
				if(!reg1DependencyFound){
					try{
						String exeOperation = exeWrapper.idWrapper.operation;
						int exeDependentReg = exeWrapper.idWrapper.reg1;
						if(!exeOperation.equals("sw") && !exeOperation.equals("bne") && !exeOperation.equals("beq")){
							if(reg1 == exeDependentReg){
								reg1Temp = exeWrapper.exeValue;
								reg1DependencyFound = true;
								System.out.println("Forwarded " + reg1Temp + " to reg1 from MEM to ID");
							}
						}	
					}catch(NullPointerException e){
						//if we havn't reached the EXE stage then do nothing
					}
				}
				
				//check for dependencies in the MEM stage if no dependency were found in the EXE stage
				//We check EXE stage first as most relevant forwarding address will be there
				if(!reg1DependencyFound){
					try{
						String memOperation =  memWrapper.exeWrapper.idWrapper.operation;
						int memDependentReg = memWrapper.exeWrapper.idWrapper.reg1;
						if(memOperation.equals("lw")){	//if dependency is LW operation
							if(reg1 == memDependentReg){
								reg1Temp = memWrapper.memValue;
								reg1DependencyFound = true;
								System.out.println("Forwarded " + reg1Temp + " to reg1 from WB to ID");
							}
						}else if(!memOperation.equals("sw") && !memOperation.equals("bne") && !memOperation.equals("beq")){ //otherwise if its not a LW operation but still a dependent operation
							if(reg1 == memDependentReg){
								reg1Temp = memWrapper.exeWrapper.exeValue;
								reg1DependencyFound = true;
								System.out.println("Forwarded " + reg1Temp + " to reg1 from WB to ID");
							}
						}
					}catch(NullPointerException e){
						//if we havn't reached the MEM stage then do nothing
					}
				}
				//_________________________________________________________________________________ lazy copy-pasta, should re-factor, reg1/reg2 can have dependency in same stage so check separately
				if(!reg2DependencyFound){
					try{
						String exeOperation = exeWrapper.idWrapper.operation;
						int exeDependentReg = exeWrapper.idWrapper.reg1;
						if(!exeOperation.equals("sw") && !exeOperation.equals("bne") && !exeOperation.equals("beq")){
							if(reg2 == exeDependentReg){
								reg2Temp = exeWrapper.exeValue;
								reg2DependencyFound = true;
								System.out.println("Forwarded " + reg2Temp + " to reg2 from MEM to ID");
							}
						}	
					}catch(NullPointerException e){
						//if we havn't reached the EXE stage then do nothing
					}
				}
				
				//check for dependencies in the MEM stage if no dependency were found in the EXE stage
				//We check EXE stage first as most relevant forwarding address will be there
				if(!reg2DependencyFound){
					try{
						String memOperation =  memWrapper.exeWrapper.idWrapper.operation;
						int memDependentReg = memWrapper.exeWrapper.idWrapper.reg1;
						if(memOperation.equals("lw")){	//if dependency is LW operation
							if(reg2 == memDependentReg){
								reg2Temp = memWrapper.memValue;
								reg2DependencyFound = true;
								System.out.println("Forwarded " + reg2Temp + " to reg2 from WB to ID");
							}
						}else if(!memOperation.equals("sw") && !memOperation.equals("bne") && !memOperation.equals("beq")){ //otherwise if its not a LW operation but still a dependent operation
							if(reg2 == memDependentReg){
								reg2Temp = memWrapper.exeWrapper.exeValue;
								reg2DependencyFound = true;
								System.out.println("Forwarded " + reg2Temp + " to reg2 from WB to ID");
							}
						}
					}catch(NullPointerException e){
						//if we havn't reached the MEM stage then do nothing
					}
				}
				
				//set branch based on register values if no dependencies were found
				if(!reg1DependencyFound && !reg2DependencyFound)
					setBranchAddress(registers[reg1], registers[reg2]);
				else if(reg1DependencyFound && !reg2DependencyFound)
					setBranchAddress(reg1Temp, registers[reg2]);
				else if(!reg1DependencyFound && reg2DependencyFound)
					setBranchAddress(registers[reg1], reg2Temp);
				else
					setBranchAddress(reg1Temp, reg2Temp);
					
				//___________________________________________
			}
		}
		
		if(currentInstruction instanceof J_Instruction){//J-Types include nop and labels
			operation = currentInstruction.getInstructionName();
			imm = parseImmediate(((J_Instruction)currentInstruction).getTarget());	
		}
		
		return detectHazards(idWrapper, exeWrapper, operation, reg1, reg2, reg3);
	}
	
	private void setBranchAddress(int register1, int register2){
		if(operation.equals("bne")){
			if(register1 != register2)
				branchAddress = imm; //if branch is taken set branchAddress to the new PC
		}
		if(operation.equals("beq")){
			if(register1 == register2)
				branchAddress = imm;
		}
	}
	
	private int getRegisterNumber(String s){
		try{
			return Integer.valueOf(s.replace("$", ""));
		}catch(Exception e){
			return 0;
		}
	}
	
	private int parseLWSWRegister(String s){
		int i = 0;
		if(s.contains("($")){//memory offset
			String[] str = s.split("\\(");
			i = getRegisterNumber(str[1].replace(")", ""));
		}
		return i;
	}
	
	private int parseImmediate(String s){
		//if s is null return
		if(s == null)
			return -1;
		try{
			//if the immediate is a hex value return its value as decimal
			if(s.startsWith("0x")){
				return Integer.parseInt(s.replace("0x", ""), 16);
			}else if(s.contains("($")){//memory offset
				String[] str = s.split("\\(");
				if(str[0].equals(""))//if the immediate is formated ($#) there is an implied 0
					return 0;
				
				int i = Integer.parseInt(str[0]);//offset
				return i;
			}else{//else return if it is a decimal value
				return Integer.parseInt(s);
			}
		}catch(NumberFormatException e){
			//if none of the above catch exception and find the index of the Immediate
			for(int i=0; i<instructions.size(); i++){	//iterate over instructions until you find index with matching label
				String instructionLabel = instructions.get(i).getLabel();
				if(instructionLabel == null)
					continue;
				if(instructionLabel.equals(s) && instructions.get(i).getInstructionName() == null) //if the label has no instruction associated with it return pc+1
					return i+1;
				if(instructionLabel.equals(s) && instructions.get(i).getInstructionName() != null) //if the label has an instruction associated with it return pc
					return i;
			}
		}
		//return -1 if nothing worked
		return -1;
	}
	
	public IDWrapper getIDWrapper(){
		return new IDWrapper(currentInstruction, operation, imm, reg1, reg2, reg3);
	}
	
	public IDWrapper getNOPIDWrapper(){
		return new IDWrapper(new Instruction(null, "nop"), "nop", -1, -1, -1, -1);
	}
	
	public class IDWrapper{
		public Instruction instruction;
		public String operation;
		public int rs, rt, rd, imm, offset;
		public int reg1, reg2, reg3;
		
		public IDWrapper(Instruction currentInstruction, String op, int i, int r1, int r2, int r3){
			instruction = currentInstruction;
			operation = op;
			reg1 = r1;
			reg2 = r2;
			reg3 = r3;
			imm = i;
		}
		
		public void printIDWrapper(){
			System.out.println("Op: " + operation);
			
			System.out.println("reg1: " + reg1);
			System.out.println("reg2: " + reg2);
			System.out.println("reg3: " + reg3);
			
			System.out.println("imm: " + imm);
			System.out.println("________________");
		}
	}
}
