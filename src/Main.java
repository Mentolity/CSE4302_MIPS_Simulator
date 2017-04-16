
public class Main {
	
	public static void main(String[] args){
		Simulator s = new Simulator("./res/input.txt");
		for(int i=0; i<35; i++){
			s.step();
		}
	}
	
}
