import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Instruction.I_Instruction;
import Instruction.Instruction;
import Instruction.J_Instruction;
import Instruction.R_Instruction;

public class ProgramParser {
	ArrayList<Instruction> instructions = new ArrayList<Instruction>();
	Scanner sc;
	String currentLine;
	
	public ProgramParser(String inputFile){
		try{
			sc = new Scanner(new File(inputFile));
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		while(sc.hasNext()){
			currentLine = sc.nextLine();
			String label = null;
			String instructionName = null;
			String rs = null;
			String rt = null;
			String rd = null;
			String IMM = null;
			
			String[] tabs = currentLine.split("	");//Split line based on tab
			for(String s : tabs){
				String[] spaces = s.split(" ");//split line based on space
				for(String p : spaces){
					if(p.contains("#"))
						break;
					p = p.replace(",", "");//remove all commas
					
					if(p.contains(":")){//If p contains a : then its a label
						label = p.replace(":", "");
					}else if(instructionName == null && !p.equals("")){//else the first non empty p will be the instructionName
						instructionName = p;
					}else if(!p.equals("")){//make sure p isn't empty so we can index it with charAt
						if(p.charAt(0) == '$' && rs == null){//if p contains a $ then its a register and goes in either rs/rt/rd based on arrival
							rs = p;
						}else if(p.charAt(0) == '$' && rt == null){
							rt = p;
						}else if(p.charAt(0) == '$' && rd == null){
							rd = p;
						}else{//if it's not rs/rt/rd it must be an immediate
							IMM = p;
						}
					}
					//System.out.println(p);
				}
			}
			//add instruction to list
			if(rs != null && rt != null && rd != null){
				instructions.add(new R_Instruction(label, instructionName, rs, rt, rd));
			}else if(IMM != null){
				instructions.add(new I_Instruction(label, instructionName, rs, rt, IMM));
			}else if(rs == null && rt == null && rd == null){
				//Note if a line contains only a label it will become a J instruction w/ instructionName and IMM as null
				instructions.add(new J_Instruction(label, instructionName, IMM));
			}
			
			//System.out.println("_____________________");
		}
		cleanInstructionList(); //remove null OPs from instructions
	}
	
	public void cleanInstructionList(){
		for(int i=0; i<instructions.size(); i++){
			if(instructions.get(i).getInstructionName() == null && instructions.get(i).getLabel() == null){
				instructions.remove(i);
				i--; //since size of list has decreased by one go back a position as to not miss it
			}
		}
	}
	
	public Instruction getInstructionAt(int i){
		return instructions.get(i);
	}
	
	public ArrayList<Instruction> getInstructions(){
		return instructions;
	}
	
	//prints contents of the instruction list
	public void debug(){
		for(Instruction i : instructions){
			printInstruction(i);
		}
	}
	public void printInstruction(Instruction i){
		if(i instanceof R_Instruction){
			System.out.println("R-TYPE: " + i.getLabel() + " " + i.getInstructionName() + " " + ((R_Instruction)i).getRS() + " " + ((R_Instruction)i).getRT() + " " + ((R_Instruction)i).getRD());
		}
		
		if(i instanceof I_Instruction){
			System.out.println("I-TYPE: " + i.getLabel() + " " + i.getInstructionName() + " " + ((I_Instruction)i).getRS() + " " + ((I_Instruction)i).getRT() + " " + ((I_Instruction)i).getIMM());
		}
		
		if(i instanceof J_Instruction){
			System.out.println("J-TYPE: " + i.getLabel() + " " + i.getInstructionName() + " " + ((J_Instruction)i).getTarget());
		}
	}
}
