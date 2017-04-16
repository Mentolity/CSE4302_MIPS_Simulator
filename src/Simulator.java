import Architecture.IF;
import Architecture.ID;
import Architecture.ID.IDWrapper;
import Architecture.EXE;
import Architecture.EXE.EXEWrapper;
import Architecture.MEM;
import Architecture.MEM.MEMWrapper;
import Architecture.WB;
import Instruction.Instruction;
//the PC right now is just the index to the instruction in the instructions list
//this works fine for the simulation but when outputting what the PC counter is it should be (PC)(4)+0x4000
//where 0x4000 is the beginning of instruction memory
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
	
 	
	public Simulator(String input){
		ProgramParser pp = new ProgramParser(input);
		pp.debug();
		IF = new IF(pp.getInstructions());
		ID = new ID(registers, pp.getInstructions()); //pass registers to execute for resolving branches in this stage
		EXE = new EXE(registers);
		MEM = new MEM(memory);
	}
	
	//step through the program
	public void step(){
		//execute each stage
		pc = IF.execute(pc); //IF updates pc to skip null instructions
		branchPC = ID.execute(instruction); //if a branch resolves in this stage get the new PC
		EXE.execute(idWrapper);
		MEM.execute(exeWrapper, registers);
		WB.execute(memWrapper, registers);
		
		//latch relevant results
		instruction = IF.getCurrentInstruction();
		idWrapper = ID.getIDWrapper();
		idWrapper.printIDWrapper();
		exeWrapper = EXE.getEXEWrapper();
		memWrapper = MEM.getMEMWrapper();
		
		if(branchPC != -1) //if valid branch was taken update PC
			pc = branchPC;
	
		//printRegisters();
	}
	
	private void printRegisters(){
		System.out.println("PC: " + pc);
		for(int i = 0; i<registers.length; i++){
			System.out.print(registers[i] + ", ");
		}
		System.out.println("");
	}
}
