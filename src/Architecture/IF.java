package Architecture;
import java.util.ArrayList;

import Instruction.Instruction;
import Instruction.J_Instruction;

public class IF {
	ArrayList<Instruction> instructions;
	Instruction currentInstruction;
	public IF(ArrayList<Instruction> il){
		instructions = il;
	}
	

	//executing the IF stage sets the currentInstruction according to the PC
	public int execute(int pc){
		while(pc < instructions.size()-1 && instructions.get(pc).getInstructionName() == null) //if the instruction parsed was a blank line skip it
			pc++;
		
		if(pc > instructions.size()-1){ //if we've fetched all instruction feed nop into the pipeline
			currentInstruction = new J_Instruction(null, "nop", null);
			return pc+1;
		}	
		
		currentInstruction = instructions.get(pc); // fetch the current instruction
		return pc+1;
	}
	
	//get the currentInstruction to be passed to the ID stage
	public Instruction getCurrentInstruction(){
		return currentInstruction;
	}
	
	public Instruction getNOPInstruction(){
		return new J_Instruction(null, "nop", null);
	}

}
