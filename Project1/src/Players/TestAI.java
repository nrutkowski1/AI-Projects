package Players;

import java.util.ArrayList;
import java.util.Arrays;

import Utilities.Move;
import Utilities.StateTree;

public class TestAI extends Player{

	int maxD = 1;
	int next_move = -1;
	boolean has_popped = false; 
	boolean never_pop_again = false;

	int my_turn;
	int opp_turn;
	boolean  my_pop;
	boolean opp_pop;

	public TestAI(String n, int t, int l) {
		super(n, t, l);
		// TODO Auto-generated constructor stub

		if(t == 1) {
			my_turn = 1;
			opp_turn = 2;
		}
		else {
			my_turn = 2;
			opp_turn = 1;
		}
	}

	@Override
	public Move getMove(StateTree state) {
		// TODO Auto-generated method stub

		if(this.turn == 1) {
			my_pop = state.pop1;
			opp_pop = state.pop2;
		}
		else {
			my_pop = state.pop2;
			opp_pop = state.pop1;
		}

		long curr_time = System.currentTimeMillis();

		next_move = -1;

		minimax(0, my_turn, Integer.MIN_VALUE, Integer.MAX_VALUE, state, maxD, curr_time);

		if(has_popped && !never_pop_again) {
			has_popped = false;
			never_pop_again = true;
//			System.out.println("Look Mom I Popped in column " + next_move);
			return new Move(true, next_move);
		}
		else {
			return new Move(false, next_move);
		}

	}

	public int minimax(int depth, int turn, int alpha, int beta, StateTree state, int maxDepth, long stime){

		if(beta<=alpha){
			if(turn == my_turn) {
				return Integer.MAX_VALUE; 
			}
			else {
				return Integer.MIN_VALUE;
			}
		}
		int gameResult = terminal_test(state);

		if(gameResult==my_turn) {
			return Integer.MAX_VALUE/2;
		}
		else if(gameResult==opp_turn) {
			return Integer.MIN_VALUE/2;
		}
		else if(gameResult==0) {
			return 0; 
		}


		if(System.currentTimeMillis() - stime < this.timeLimit / 2) {

			maxDepth += 1;
		}
		else {
			if(depth==maxDepth) {
				return state_eval(state);
			}
		}

		int maxScore=Integer.MIN_VALUE;
		int minScore = Integer.MAX_VALUE;

		for(int j = 0; j < state.columns; j++){

			int currentScore = 0;

			if(validMove(new Move(true, j), state) && !never_pop_again) {

				if(turn == my_turn){
					state.makeMove(new Move(true, j));

					currentScore = minimax(depth+1, opp_turn, alpha, beta, state, maxDepth, stime);

					if(depth==0){
//						System.out.println("Score for location "+j+" = "+currentScore + " Popped");
						if(currentScore > maxScore) {
							next_move = j; 
							has_popped = true;
						}
						if(currentScore == Integer.MAX_VALUE/2) {
							undoPopMove(j, state);
							break;
						}
					}

					maxScore = Math.max(currentScore, maxScore);

					alpha = Math.max(currentScore, alpha); 

				} 
				else if(turn==opp_turn){
					state.makeMove(new Move(true, j));

					currentScore = minimax(depth+1, my_turn, alpha, beta, state, maxDepth, stime);

					minScore = Math.min(currentScore, minScore);

					beta = Math.min(currentScore, beta); 
				} 
				undoPopMove(j, state); 
				if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break; 
			}

			if(validMove(new Move(false, j), state)) {
				if(turn == my_turn){
					state.makeMove(new Move(false,j));

					currentScore = minimax(depth+1, opp_turn, alpha, beta, state, maxDepth, stime);

					if(depth==0){
//						System.out.println("Score for location "+j+" = "+currentScore);
						if(currentScore > maxScore) {
							next_move = j; 
							has_popped = false;
						}
						if(currentScore == Integer.MAX_VALUE/2) {
							undoMove(j, state);
							break;
						}
					}

					maxScore = Math.max(currentScore, maxScore);

					alpha = Math.max(currentScore, alpha); 
				} 
				else if(turn==opp_turn){
					state.makeMove(new Move(false, j));

					currentScore = minimax(depth+1, my_turn, alpha, beta, state, maxDepth, stime);

					minScore = Math.min(currentScore, minScore);

					beta = Math.min(currentScore, beta); 
				}  
				undoMove(j, state); 
				if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break; 
			}  
		}

		if(turn == my_turn) {
			return maxScore;
		}
		else {
			return minScore;
		}
	}

	// code to evaluate if the board is good for me or opponent

	public boolean validMove(Move move, StateTree state)
	{
		if(move.getColumn() >= state.columns || move.getColumn()  < 0)
		{
			return false;
		}
		if(!move.getPop() && state.getBoardMatrix()[state.rows-1][move.getColumn()] != 0)
		{
			return false;
		}
		if(move.getPop())
		{
			if(state.getBoardMatrix()[0][move.getColumn()] != state.turn)
			{
				return false;
			}
			if((state.turn == 1 && state.pop1) || (state.turn == 2 && state.pop2))
			{
				return false;
			}
		}
		return true;
	}


	int calculateScore(int aiScore, int moreMoves){   
		int moveScore = 4 - moreMoves;
		if(aiScore==0)return 0;
		else if(aiScore==1)return 1*moveScore;
		else if(aiScore==2)return 10*moveScore;
		else if(aiScore==3)return 100*moveScore;
		else return 1000;
	}


	public int state_eval(StateTree b) {

		int aiScore=1;
		int score=0;
		int blanks = 0;
		int k=0, moreMoves=0;
		for(int i=b.rows - 1;i>=0;--i){
			for(int j=0;j<b.columns;++j){

				if(b.getBoardMatrix()[i][j]==0 || b.getBoardMatrix()[i][j]==opp_turn) continue; 

				if(j<=3){ 
					for(k=1;k<4;++k){
						if(b.getBoardMatrix()[i][j+k]==my_turn)aiScore++;
						else if(b.getBoardMatrix()[i][j+k]==opp_turn){aiScore=0;blanks = 0;break;}
						else blanks++;
					}

					moreMoves = 0; 
					if(blanks>0) 
						for(int c=1;c<4;++c){
							int column = j+c;
							for(int m=i; m< b.rows;m++){
								if(b.getBoardMatrix()[m][column]==0)moreMoves++;
								else break;
							} 
						} 

					if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
					aiScore=1;   
					blanks = 0;
				} 

				if(i>=3){
					for(k=1;k<4;++k){
						if(b.getBoardMatrix()[i-k][j]==my_turn)aiScore++;
						else if(b.getBoardMatrix()[i-k][j]==opp_turn){aiScore=0;break;} 
					} 
					moreMoves = 0; 

					if(aiScore>0){
						int column = j;
						for(int m=i-k+1; m<=i-1;m++){
							if(b.getBoardMatrix()[m][column]==0)moreMoves++;
							else break;
						}  
					}
					if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
					aiScore=1;  
					blanks = 0;
				}

				if(j>=3){
					for(k=1;k<4;++k){
						if(b.getBoardMatrix()[i][j-k]==my_turn)aiScore++;
						else if(b.getBoardMatrix()[i][j-k]==opp_turn){aiScore=0; blanks=0;break;}
						else blanks++;
					}
					moreMoves=0;
					if(blanks>0) 
						for(int c=1;c<4;++c){
							int column = j- c;
							for(int m=i; m< b.rows;m++){
								if(b.getBoardMatrix()[m][column]==0)moreMoves++;
								else break;
							} 
						} 

					if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
					aiScore=1; 
					blanks = 0;
				}

				if(j<=3 && i>=3){
					for(k=1;k<4;++k){
						if(b.getBoardMatrix()[i-k][j+k]==my_turn)aiScore++;
						else if(b.getBoardMatrix()[i-k][j+k]==opp_turn){aiScore=0;blanks=0;break;}
						else blanks++;                        
					}
					moreMoves=0;
					if(blanks>0){
						for(int c=1;c<4;++c){
							int column = j+c, row = i-c;
							for(int m=row;m<b.rows;++m){
								if(b.getBoardMatrix()[m][column]==0)moreMoves++;
								else if(b.getBoardMatrix()[m][column]==my_turn);
								else break;
							}
						} 
						if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
						aiScore=1;
						blanks = 0;
					}
				}

				if(i>=3 && j>=3){
					for(k=1;k<4;++k){
						if(b.getBoardMatrix()[i-k][j-k]==my_turn)aiScore++;
						else if(b.getBoardMatrix()[i-k][j-k]==opp_turn){aiScore=0;blanks=0;break;}
						else blanks++;                        
					}
					moreMoves=0;
					if(blanks>0){
						for(int c=1;c<4;++c){
							int column = j-c, row = i-c;
							for(int m=row;m<b.rows;++m){
								if(b.getBoardMatrix()[m][column]==0)moreMoves++;
								else if(b.getBoardMatrix()[m][column]==my_turn);
								else break;
							}
						} 
						if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
						aiScore=1;
						blanks = 0;
					}
				} 
			}
		}
		return score;
	}

	public int terminal_test(StateTree board) {

		int points = checkConnect(board); // see how many each player has in a row
		if(points > 0) { // if player 1 has more in a row they win
			return 1;
		}
		else if(points < 0) { // if player 2 has more in a row they win
			return 2;
		}
		else if(checkFull(board)) {// if the board is full than it's a tie
			return 0;
		}
		return -1;
	}

	// This counts how many n-in-a-rows each player has
	public static int checkConnect(StateTree board)
	{
		int winner = 0;
		int[] count = new int[4];
		int winTotal = 0;
		for(int i=0; i<board.rows; i++)
		{
			for(int j=0; j<board.columns; j++)
			{
				if(board.getBoardMatrix()[i][j] == 0)
				{
					winner = 0;
					for(int x=0; x<4; x++)
					{
						count[x] = 0;
					}
				}
				else
				{
					winner = board.getBoardMatrix()[i][j];
					for(int x=0; x<board.winNumber; x++)
					{
						if((j+x < board.columns) && (board.getBoardMatrix()[i][j+x] == winner))
							count[0]++;
						else
							count[0] = 0;
						if((i+x < board.rows) && (board.getBoardMatrix()[i+x][j] == winner))
							count[1]++;
						else
							count[1] = 0;
						if((i+x < board.rows) && (j+x < board.columns) && (board.getBoardMatrix()[i+x][j+x] == winner))
							count[2]++;
						else
							count[2] = 0;
						if((i-x >= 0) && (j+x < board.columns) && (board.getBoardMatrix()[i-x][j+x] == winner))
							count[3]++;
						else
							count[3] = 0;
					}
				}
				for(int x=0; x<4; x++)
				{
					if(count[x] == board.winNumber)
					{
						if(winner == 1)
							winTotal++;
						else if(winner == 2)
							winTotal--;
					}
					count[x] = 0;
				}
				winner = 0;
			}
		}
		return winTotal;
	}

	public boolean checkFull(StateTree board) 
	{

		for(int i=0; i<board.rows; i++) {

			for(int j=0; j<board.columns; j++) {

				if(board.getBoardMatrix()[i][j] == 0) {

					return false;
				}
			}
		}
		return true;
	}

	public void undoMove(int column, StateTree state) {

		for(int i = state.rows - 1; i > -1; i--) {

			if(state.getBoardMatrix()[i][column] != 0) {

				state.getBoardMatrix()[i][column] = 0;
				state.turn = Math.abs(state.turn-3);
				break;
			}
		}
	}

	public void undoPopMove(int column, StateTree state) {

		state.turn = Math.abs(state.turn-3);
		if(state.turn == 1) {
			state.pop1 = false;
		}
		else if(state.turn == 2) {
			state.pop2 = false;
		}

		for(int i = state.rows - 1; i > -1; i--) {

			if(i != 0) {

				state.getBoardMatrix()[i][column] = state.getBoardMatrix()[i - 1][column];
			}
			else {
				state.getBoardMatrix()[i][column] = state.turn;
			}
		}
	}

}
