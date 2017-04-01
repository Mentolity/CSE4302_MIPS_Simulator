package Instruction;

public class R_Instruction extends Instruction{
	String rs;
	String rt;
	String rd;
	
	public R_Instruction(String l, String InsName, String s, String t, String d) {
		super(l, InsName);
		rs = s;
		rt = t;
		rd = d;
	}
	
	public String getRS(){
		return rs;
	}
	public String getRT(){
		return rt;
	}
	public String getRD(){
		return rd;
	}

}
