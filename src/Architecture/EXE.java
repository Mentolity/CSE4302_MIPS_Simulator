package Architecture;

import Architecture.ID.IDWrapper;

public class EXE {
	int dependentRegister;
	int dependentRegisterValue;
	
	public void execute(IDWrapper w) {
		if(w == null)
			return;
		//____________________________________________________________
		if(w.operation == null)
			return;
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("nop")){
			return;
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("lw")){
			
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("sw")){
			
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("add")){
			if(w.reg2 == dependentRegister){
				w.rs = dependentRegisterValue + w.rd;
			}else if(w.reg3 == dependentRegister){
				w.rs = w.rt + dependentRegisterValue;
			}else{
				w.rs = w.rt + w.rd;
			}
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("sub")){
			if(w.reg2 == dependentRegister){
				w.rs = dependentRegisterValue - w.rd;
			}else if(w.reg3 == dependentRegister){
				w.rs = w.rt - dependentRegisterValue;
			}else{
				w.rs = w.rt - w.rd;
			}
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("addi")){
			if(w.reg2 == dependentRegister){
				w.rs = dependentRegisterValue + w.imm;
			}else{
				w.rs = w.rt + w.imm;
			}
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("bne")){
			
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("beq")){
			
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("and")){
			
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("or")){
			
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("nor")){
			
		}
		//____________________________________________________________
		if(w.operation.equalsIgnoreCase("xor")){
			
		}
		//____________________________________________________________
		
		dependentRegister = w.reg1;
		dependentRegister = w.rs;
		
	}

	public int dependentRegister() {
		return dependentRegister;
	}
}
