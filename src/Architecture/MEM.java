package Architecture;

import Architecture.EXE.EXEWrapper;

public class MEM {
	int[] memory;
	EXEWrapper exeWrapper;
	int memValue = 0;
	
	public MEM(int[] mem){
		memory = mem;
	}

	public void execute(EXEWrapper exeW, MEMWrapper memWrapper, int[] registers){
		exeWrapper = exeW;
		try{
			memValue = exeWrapper.exeValue; //if operation isn't LW/SW then return the exeValue
			if(exeWrapper.idWrapper.operation.equals("lw")){
				memValue = memory[exeWrapper.exeValue]; //load the value at the index in memory calculated in the EXE stage to memValue
				System.out.println("Loaded Value: " + memValue + " From Location: " + memoryLocationToString(exeWrapper.exeValue));
			}
			
			if(exeWrapper.idWrapper.operation.equals("sw")){		
				if(exeWrapper.idWrapper.reg1 != memWrapper.exeWrapper.idWrapper.reg1){//if the two registers arn't equal then no dependence
					memory[exeWrapper.exeValue] = registers[exeWrapper.idWrapper.reg1];
					System.out.println("Saved Value " + registers[exeWrapper.idWrapper.reg1] + " To Memory Location " + memoryLocationToString(exeWrapper.exeValue));
				}else if(memWrapper.exeWrapper.idWrapper.operation.equals("lw")){ //the registers are equal so there is a dependence if its a LW instruction do the following
					memory[exeWrapper.exeValue] = memWrapper.memValue;
					System.out.println("Saved Value " + memWrapper.memValue + " To Memory Location " + memoryLocationToString(exeWrapper.exeValue));
				}else{ //else the dependence isn't a LW instruction so do the following
					memory[exeWrapper.exeValue] = memWrapper.exeWrapper.exeValue;
					System.out.println("Saved Value " + memWrapper.exeWrapper.exeValue + " To Memory Location " + memoryLocationToString(exeWrapper.exeValue));
				}
			}
		}catch(Exception e){
			return; //if the operation isn't lw/sw then finish
		}
	}
	
	private String memoryLocationToString(int location){
		location = (location*4) + 0x1000;
		return "0x" + Integer.toHexString(location);
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
