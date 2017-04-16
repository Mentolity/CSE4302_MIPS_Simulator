package Architecture;

import java.util.ArrayList;

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
	
	public ID(int[] reg, ArrayList<Instruction> il){
		registers = reg;
		instructions = il;
	}
	
	public int execute(Instruction currentInstruction){
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
			reg3 = parseLWSWRegister(((I_Instruction)currentInstruction).getIMM());//register of lw/sw instruction
			
			//___________________________________________
			//Resolving branches
			if(operation.equals("bne")){
				if(registers[reg1] != registers[reg2])
					return imm; //if branch is taken return the new pc
			}
			if(operation.equals("beq")){
				if(registers[reg1] == registers[reg2])
					return imm;
			}
			//___________________________________________
		}
		
		if(currentInstruction instanceof J_Instruction){//J-Types include nop and labels
			operation = currentInstruction.getInstructionName();
			imm = parseImmediate(((J_Instruction)currentInstruction).getTarget());	
		}
		
		return -1;//if the no branch is taken then return -1 to indicate the the pc isn't changed
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
				if(instructionLabel.equals(s))
					return i;
			}
		}
		//return -1 if nothing worked
		return -1;
	}
	
	public IDWrapper getIDWrapper(){
		return new IDWrapper(currentInstruction, operation, imm, reg1, reg2, reg3);
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
