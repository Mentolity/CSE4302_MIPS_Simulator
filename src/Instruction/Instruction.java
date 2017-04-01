package Instruction;

public class Instruction {
	String label;
	String instructionName;
	
	public Instruction(String l, String InsName){
		label = l;
		instructionName = InsName;
	}
	
	public String getLabel(){
		return label;
	}
	
	public String getInstructionName(){
		return instructionName;
	}
}
