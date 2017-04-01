package Architecture;
import java.util.ArrayList;

import Instruction.Instruction;

public class IF {
	ArrayList<Instruction> instructions;
	Instruction currentInstruction;
	public IF(ArrayList<Instruction> il){
		instructions = il;
	}
	

	//executing the IF stage sets the currentInstruction according to the PC
	public void execute(int pc){
		currentInstruction = instructions.get(pc);
	}
	
	//get the currentInstruction to be passed to the ID stage
	public Instruction getCurrentInstruction(){
		return currentInstruction;
	}

}
