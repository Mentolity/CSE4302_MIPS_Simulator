package Architecture;

import Architecture.EXE.EXEWrapper;

public class MEM {
	int[] memory;
	EXEWrapper exeWrapper;
	int memValue = 0;
	
	public MEM(int[] mem){
		memory = mem;
	}

	public void execute(EXEWrapper exeW, int[] registers){
		exeWrapper = exeW;
		try{
			if(exeWrapper.idWrapper.operation.equals("lw")){
				memValue = memory[exeWrapper.exeValue]; //load the value at the index in memory calculated in the EXE stage to memValue
			}
			if(exeWrapper.idWrapper.operation.equals("sw")){
				memory[exeWrapper.exeValue] = registers[exeWrapper.idWrapper.reg1]; //load the value of reg1 into the index in memory calculated in the EXE stage
			}
		}catch(Exception e){
			return; //if the operation isn't lw/sw then finish
		}
	}
	
	public MEMWrapper getMEMWrapper(){
		return new MEMWrapper(memValue, exeWrapper);
	}
	
	public class MEMWrapper{
		public int memValue;
		public EXEWrapper exeWrapper;
		
		public MEMWrapper(int memV, EXEWrapper exeW){
			memValue = memV;
			exeWrapper = exeW;
		}
	}
}
