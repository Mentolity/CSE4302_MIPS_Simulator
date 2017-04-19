import java.util.Scanner;

public class Main {
	static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args){
		String input;
		int numberOfSteps;
		Simulator s = new Simulator("./res/input.txt");
		
		while(true){
			numberOfSteps = 0;
			System.out.print("\nEnter the number of cycles: ");
			input = sc.nextLine();
			if(input.equals("")){
				numberOfSteps = 1;
			}else if(input.equals("0")){
				s.runUntilCompletion();
				sc.close();
				return;
			}else{
				try{
					numberOfSteps = Integer.parseInt(input);
				}catch(NumberFormatException e){
					System.out.println("\nInvalid Input");
				}
			}
			for(int i=0; i<numberOfSteps; i++){
				if(!s.step())
					return;
			}
		}
	}
	
}
