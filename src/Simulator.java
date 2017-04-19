import Architecture.IF;
import Architecture.ID;
import Architecture.ID.IDWrapper;

import java.util.ArrayList;

import Architecture.EXE;
import Architecture.EXE.EXEWrapper;
import Architecture.MEM;
import Architecture.MEM.MEMWrapper;
import Architecture.WB;
import Instruction.Instruction;

public class Simulator {
	ProgramParser pp;
	int pc = 0; //program counter
	IF IF;
	ID ID;
	EXE EXE;
	MEM MEM;
	WB WB = new WB();
	int[] registers = new int[32];
	int[] memory = new int[1024]; //Valid memory addresses between 0x1000 to 0x2000 = 1024 integers
	
	Instruction instruction = null;
	IDWrapper idWrapper = null;
	EXEWrapper exeWrapper = null;
	MEMWrapper memWrapper = null;
	
	int branchPC = -1;
	int numberOfNOPs = 0;
	
 	
	public Simulator(String input){
		pp = new ProgramParser(input);
		pp.debug();
		IF = new IF(pp.getInstructions());
		ID = new ID(registers, pp.getInstructions()); //pass registers to execute for resolving branches in this stage
		EXE = new EXE(registers);
		MEM = new MEM(memory);
	}
	
	//step through the program
	public boolean step(){ //returns true while there are still more steps
		//execute each stage
		if(numberOfNOPs == 0){//if there arn't any NOPs continue normally
			pc = IF.execute(pc); //IF updates pc to skip null instructions
			numberOfNOPs = ID.execute(instruction, idWrapper, exeWrapper, memWrapper); //if a dependency is found set numberOfNOPs to the number of pipeline stalls
			EXE.execute(idWrapper, exeWrapper, memWrapper);
			MEM.execute(exeWrapper, memWrapper, registers);
			WB.execute(memWrapper, registers);		
			
			//latch relevant results
			instruction = IF.getCurrentInstruction();
			idWrapper = ID.getIDWrapper();
			exeWrapper = EXE.getEXEWrapper();
			memWrapper = MEM.getMEMWrapper();
		}else{
			ID.execute(idWrapper.instruction, idWrapper, exeWrapper, memWrapper); //updates branchPC if after NOPs we decide to either take/not-take the branch
			EXE.execute(idWrapper, exeWrapper, memWrapper);
			MEM.execute(exeWrapper, memWrapper, registers);
			WB.execute(memWrapper, registers);
			
			//latch relevant results
			if(numberOfNOPs >= 1){
				//instruction = IF.getCurrentInstruction();
				//idWrapper = ID.getIDWrapper();
				exeWrapper = EXE.getNOPEXEWrapper(); //insert NOP into exe stage
				memWrapper = MEM.getMEMWrapper();
			}
			
			numberOfNOPs--;
			if(numberOfNOPs == 0)
				ID.execute(instruction, idWrapper, exeWrapper, memWrapper);
				
		}
		
		if(numberOfNOPs == 0){
			branchPC = ID.getBranchAddress();
			if(branchPC != -1){ //if valid branch was taken update PC
				pc = branchPC;
				instruction = IF.getNOPInstruction(); //since we assume the branch isn't taken but it was taken flush the IF stage in pipeline
			}
		}

		System.out.println("PC: " + pcToHexString()); //print the PC
		printInstructionsAtStages();
		printRegisters();
		
		if(instruction.getInstructionName().equals("nop") 
				&& idWrapper.instruction.getInstructionName().equals("nop")
				&& exeWrapper.idWrapper.instruction.getInstructionName().equals("nop")
				&& memWrapper.exeWrapper.idWrapper.instruction.getInstructionName().equals("nop")
				&& WB.getOperation().equals("nop")
				&& pc >= pp.getInstructions().size()){
			return false;
		}else{
			return true;
		}
	}
	
	public void runUntilCompletion(){
		while(step());
	}
	
	private void printInstructionsAtStages(){
		try{
			System.out.println("IF:  " + instruction.getInstructionName());
			System.out.println("ID:  " + idWrapper.operation + " " + idWrapper.reg1 + " " + idWrapper.reg2 + " " + idWrapper.reg3 + " " + idWrapper.imm);
			System.out.println("EXE: " + exeWrapper.idWrapper.operation);
			System.out.println("MEM: " + memWrapper.exeWrapper.idWrapper.operation);
			System.out.println("WB:  " + WB.getOperation());
			System.out.println("________________________________");
		}catch(NullPointerException e){
			System.out.println("________________________________");
			//if one of the above fails then we havn't entered that stage yet so continue
		}
	}
	private String pcToHexString(){
		ArrayList<Instruction> instructions = pp.getInstructions();
		int tempPC = pc;
		int pcHEX = 0x4000;
		
		//if the program counter is calling NOPs return the PC of the last valid instruction
		if(pc >= instructions.size())
			tempPC = instructions.size()-1;
		else
			tempPC = pc;
		
		for(int i=0; i<tempPC; i++){
			Instruction instruction = instructions.get(i);
			if(instruction.getLabel() != null && instruction.getInstructionName() == null)
				continue;
			pcHEX+=4;
		}
		return "0x" + Integer.toHexString(pcHEX);
	}
	
	private void printRegisters(){
		for(int i = 0; i<registers.length; i++){
			System.out.print(registers[i] + ", ");
		}
		System.out.println("");
	}
}
