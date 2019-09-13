package Players;

import java.util.Scanner;

import Utilities.Move;
import Utilities.StateTree;

public class HumanPlayer extends Player {

	
	public Scanner scan;
	
	
	public HumanPlayer(String n, int t, int l) {
		super(n, t, l);
		scan = new Scanner(System.in);
	}
	
	
	@Override
	public Move getMove(StateTree state) {
	
		System.out.println("Your move, enter column 0 - " + (state.columns - 1));
		int move = scan.nextInt();
		System.out.println("Pop? True for pop, false otherwise");
		System.out.println("Player 1 pop " + state.pop1 + "Player 2 pop " + state.pop2);
		boolean popped = scan.nextBoolean();
		
		while(!state.validMove(new Move(popped, move))) {
			
			System.out.println("Invalid Move!\n");
			System.out.println("Your move, enter column 0 - " + (state.columns - 1));
			move = scan.nextInt();
			System.out.println("Pop? True for pop, false otherwise");
			popped = scan.nextBoolean();	
		}	
		
		return new Move(popped, move);
	}
}
