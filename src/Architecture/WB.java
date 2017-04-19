package Architecture;

import Architecture.MEM.MEMWrapper;
import Instruction.J_Instruction;

public class WB {
	String operation;
	public void execute(MEMWrapper memWrapper, int[] registers){
		try{
			operation = memWrapper.exeWrapper.idWrapper.operation;
			if(operation.equals("bne") || operation.equals("beq") || operation.equals("nop"))//if the operation is a branch instruction do nothing as it was resolved in the ID
				return;
			
			if(operation.equals("lw")){
				System.out.println("Writing " + memWrapper.memValue+ " to register " + memWrapper.exeWrapper.idWrapper.reg1);
				registers[memWrapper.exeWrapper.idWrapper.reg1] = memWrapper.memValue; //updates reg1 to the value received from the MEM stage
				return;
			}
			if(operation.equals("sw"))
				return;
			
			if(!(memWrapper.exeWrapper.idWrapper.instruction instanceof J_Instruction)){
				System.out.println("Writing " + memWrapper.exeWrapper.exeValue + " to register " + memWrapper.exeWrapper.idWrapper.reg1);
				registers[memWrapper.exeWrapper.idWrapper.reg1] = memWrapper.exeWrapper.exeValue; //if I-Type or R-Type instruction load the calculated value into reg1
			}
		}catch(NullPointerException e){
			return; //Pipeline hasn't reached WB stage
		}
	}
	public String getOperation(){
		return operation;
	}
}
