package Architecture;

import Architecture.MEM.MEMWrapper;
import Instruction.I_Instruction;
import Instruction.R_Instruction;

public class WB {
	public void execute(MEMWrapper memWrapper, int[] registers){
		try{
			if(memWrapper.exeWrapper.idWrapper.operation.equals("lw")){
				registers[memWrapper.exeWrapper.idWrapper.reg1] = memWrapper.memValue; //updates reg1 to the value received from the MEM stage
				return;
			}
			if(memWrapper.exeWrapper.idWrapper.operation.equals("sw"))
				return;
			
			if(memWrapper.exeWrapper.idWrapper.instruction instanceof I_Instruction){
				registers[memWrapper.exeWrapper.idWrapper.reg1] = memWrapper.exeWrapper.exeValue; //if I-Type instruction load the calculated value into reg1
			}
		}catch(NullPointerException e){
			return; //Pipeline hasn't reached WB stage
		}
	}

}
