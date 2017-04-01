package Instruction;

public class J_Instruction extends Instruction{
	String target;
	public J_Instruction(String l, String InsName, String targetLabel) {
		super(l, InsName);
		target = targetLabel;
	}
	
	public String getTarget(){
		return target;
	}

}
