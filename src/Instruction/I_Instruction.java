package Instruction;

public class I_Instruction extends Instruction{
	String rs;
	String rt;
	String Immediate;

	public I_Instruction(String l, String InsName, String s, String t, String IMM) {
		super(l, InsName);
		rs = s;
		rt = t;
		Immediate = IMM;
	}
	
	public String getRS(){
		return rs;
	}
	public String getRT(){
		return rt;
	}
	public String getIMM(){
		return Immediate;
	}

}
