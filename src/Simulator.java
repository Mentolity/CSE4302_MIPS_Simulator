import Architecture.IF;
import Architecture.ID;
import Architecture.ID.IDWrapper;
import Architecture.EXE;
import Architecture.MEM;
import Architecture.WB;
import Instruction.Instruction;

public class Simulator {
	ProgramParser pp;
	int pc = 0; //program counter
	IF IF;
	ID ID;
	EXE EXE = new EXE();
	MEM MEM = new MEM();
	WB WB = new WB();
	int[] registers = new int[32];
	
	Instruction i = null;
	IDWrapper w = null;
	
 	
	public Simulator(String input){
		ProgramParser pp = new ProgramParser(input);
		pp.debug();
		IF = new IF(pp.getInstructions());
		ID = new ID(registers, pp.getInstructions());
	}
	
	//step through the program
	public void step(){
		//IF stage
		IF.execute(pc);
		
		//ID stage
		ID.execute(i);
		
		//EXE stage
		EXE.execute(w);
		MEM.execute();
		WB.execute();
		
		i = IF.getCurrentInstruction();
		w = ID.getIDWrapper();
		w.printIDWrapper();
		
		
		
		pc++;
	}
}
