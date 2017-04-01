package Architecture;

import java.util.ArrayList;

import Instruction.I_Instruction;
import Instruction.Instruction;
import Instruction.J_Instruction;
import Instruction.R_Instruction;


//refactor such that ID stage gets register indexes but doens't pull any values until EXE phase when forwarding is available.
public class ID {
	ArrayList<Instruction> instructions; //needs access to instructions to decode immediate jumps
	Instruction currentInstruction;
	String operation;
	int[] registers;
	int reg1, reg2, reg3;
	int rs, rt, rd, imm;
	
	public ID(int[] reg, ArrayList<Instruction> il){
		registers = reg;
	}
	
	public void execute(Instruction currentInstruction){
		this.currentInstruction = currentInstruction;
		if(currentInstruction instanceof R_Instruction){
			operation = currentInstruction.getInstructionName();
			reg1 = getRegisterNumber(((R_Instruction)currentInstruction).getRS());
			reg2 = getRegisterNumber(((R_Instruction)currentInstruction).getRT());
			reg3 = getRegisterNumber(((R_Instruction)currentInstruction).getRD());
			
			//rs is the register being written to in an R_Instruction therefore the value is irrelevant
			//rs = registers[getRegisterNumber(((R_Instruction)currentInstruction).getRS())];
			
			rt = registers[reg2];
			rd = registers[reg3];

		}
		
		if(currentInstruction instanceof I_Instruction){
			operation = currentInstruction.getInstructionName();
			reg1 = getRegisterNumber(((I_Instruction)currentInstruction).getRS());
			reg2 = getRegisterNumber(((I_Instruction)currentInstruction).getRT());
			reg3 = -1;//reg3 not used in I_Instructions so set to -1
			
			rs = registers[reg1];
			rt = registers[reg2];
			imm = parseImmediate(((I_Instruction)currentInstruction).getIMM());	
		}
		
		if(currentInstruction instanceof J_Instruction){//J-Types include nop and labels
			operation = currentInstruction.getInstructionName();
			imm = parseImmediate(((J_Instruction)currentInstruction).getTarget());	
		}
	}
	
	private int getRegisterNumber(String s){
		try{
			return Integer.valueOf(s.replace("$", ""));
		}catch(Exception e){
			return 0;
		}
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
				int i = Integer.parseInt(str[0]);//offset
				int j = registers[getRegisterNumber(str[1].replace(")", ""))]; //data-dependencies
				return i+j;
				
			}else{//else return if it is a decimal value
				return Integer.parseInt(s);
			}
		}catch(NumberFormatException e){
			//if neither catch exception and find the index of the Immediate
			for(int i=0; i<instructions.size(); i++){
				if(instructions.get(i).equals(s))
					return i;
			}
		}
		//return -1 if nothing worked
		return -1;
	}
	
	public IDWrapper getIDWrapper(){
		return new IDWrapper(currentInstruction, operation, rs, rt, rd, imm, reg1, reg2, reg3);
	}
	
	public class IDWrapper{
		public Instruction instruction;
		public String operation;
		public int rs, rt, rd, imm;
		public int reg1, reg2, reg3;
		
		public IDWrapper(Instruction currentInstruction, String op, int s, int t, int d, int i, int r1, int r2, int r3){
			instruction = currentInstruction;
			operation = op;
			rs = s;
			rt = t;
			rd = d;
			reg1 = r1;
			reg2 = r2;
			reg3 = r3;
			imm = i;
		}
		
		public void printIDWrapper(){
			System.out.println("Op: " + operation);
			System.out.println("rs: " + rs);
			System.out.println("rt: " + rt);
			System.out.println("rd: " + rd);
			System.out.println("imm: " + imm);
			System.out.println("________________");
		}
	}
}
