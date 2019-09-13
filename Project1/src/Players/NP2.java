package Players;

import java.util.ArrayList;
import java.util.Arrays;

import Utilities.Move;
import Utilities.StateTree;

public class NP2 extends Player{

	int maxD = 7;
	int next_move = -1;

	int my_turn;
	int opp_turn;

	public NP2(String n, int t, int l) {
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
		//return null;
		//		return heval(state);

		long curr_time = System.currentTimeMillis();

		next_move = -1;

		minimax(0, my_turn, Integer.MIN_VALUE, Integer.MAX_VALUE, state, maxD, curr_time);

		return new Move(false, next_move);
		//
		//		for(int j=0; j<state.columns; j++) {
		//
		//			for(int i=0; i<state.rows; i++) {
		//
		//				if(state.getBoardMatrix()[i][j] == 0) {
		////					heval(state);
		//					return new Move(false, j);
		//
		//				}
		//
		//				//				try{Thread.sleep(15000);}
		//				//				catch(InterruptedException ex){Thread.currentThread().interrupt();}
		//
		//				//				if(this.turn == 1)
		//				//					return new Move(false, 0);
		//				//				if(this.turn == 2)
		//				//					return new Move(false, 1);	
		//			}
		//
		//			//			if((this.turn == 1 && !state.pop1) || (this.turn == 2 && !state.pop2))
		//			//			{
		//			//				return new Move(true, 0);	
		//			//			}
		//		}
		//		return new Move(false, 100);
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

		if(gameResult==1) {
			return Integer.MAX_VALUE/2;
		}
		else if(gameResult==2) {
			return Integer.MIN_VALUE/2;
		}
		else if(gameResult==0) {
			return 0; 
		}

		if(depth==maxDepth) {
			return state_eval(state);
		}

		int maxScore=Integer.MIN_VALUE;
		int minScore = Integer.MAX_VALUE;

		for(int j = 0; j < state.columns; j++){

			int currentScore = 0;

			if(!validMove(new Move(false, j), state)) continue; 

			if(turn==my_turn){
				state.makeMove(new Move(false,j));

				long curr_time = System.currentTimeMillis();
				//
				//				if((curr_time - stime) <= (this.timeLimit / 2)) {
				//
				//					currentScore = minimax(depth+1, opp_turn, alpha, beta, state, maxDepth + 1, curr_time);
				//				}
				//				else {
				currentScore = minimax(depth+1, opp_turn, alpha, beta, state, maxDepth, curr_time);
				//				}

				if(depth==0){
					System.out.println("Score for location "+j+" = "+currentScore);
					if(currentScore > maxScore) {
						next_move = j; 
					}
					if(currentScore == Integer.MAX_VALUE/2) {
						undoMove(j, state);
						break;
					}
				}

				maxScore = Math.max(currentScore, maxScore);

				alpha = Math.max(currentScore, alpha); 
				//				System.out.println("Alpha = " + alpha);
			} 
			else if(turn==opp_turn){
				state.makeMove(new Move(false, j));
				long curr_time = System.currentTimeMillis();

				//				if((curr_time - stime) <= (this.timeLimit / 2)) {
				//
				//					currentScore = minimax(depth+1, opp_turn, alpha, beta, state, maxDepth + 1, curr_time);
				//				}
				//				else {
				currentScore = minimax(depth+1, opp_turn, alpha, beta, state, maxDepth, curr_time);
				//				}
				minScore = Math.min(currentScore, minScore);

				beta = Math.min(currentScore, beta); 
				//				System.out.println("beta = " + beta);
			}  
			undoMove(j, state); 
			if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break; 
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

	//Evaluate board favorableness for AI
	public int state_eval(StateTree b){

		int aiScore=1;
		int score=0;
		int blanks = 0;
		int k=0, moreMoves=0;
		for(int i=b.rows - 1;i>0;--i){
			for(int j=0;j<b.columns;++j){

				if(b.getBoardMatrix()[i][j]==0 || b.getBoardMatrix()[i][j]==2) continue; 

				if(j<=(b.winNumber -1)){ 
					for(k=1;k<b.winNumber;++k){
						if(b.getBoardMatrix()[i][j+k]==1)aiScore++;
						else if(b.getBoardMatrix()[i][j+k]==2){aiScore=0;blanks = 0;break;}
						else blanks++;
					}

					moreMoves = 0; 
					if(blanks>0) 
						for(int c=1;c<b.winNumber;++c){
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

				if(i>=(b.winNumber -1)){
					for(k=1;k<b.winNumber;++k){
						if(b.getBoardMatrix()[i-k][j]==1)aiScore++;
						else if(b.getBoardMatrix()[i-k][j]==2){aiScore=0;break;} 
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

				if(j>=(b.winNumber - 1)){
					for(k=1;k<b.winNumber;++k){
						if(b.getBoardMatrix()[i][j-k]==1)aiScore++;
						else if(b.getBoardMatrix()[i][j-k]==2){aiScore=0; blanks=0;break;}
						else blanks++;
					}
					moreMoves=0;
					if(blanks>0) 
						for(int c=1;c<b.winNumber;++c){
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

				if(j<=(b.winNumber - 1) && i>=(b.winNumber - 1)){
					for(k=1;k<b.winNumber;++k){
						if(b.getBoardMatrix()[i-k][j+k]==1)aiScore++;
						else if(b.getBoardMatrix()[i-k][j+k]==2){aiScore=0;blanks=0;break;}
						else blanks++;                        
					}
					moreMoves=0;
					if(blanks>0){
						for(int c=1;c<b.winNumber;++c){
							int column = j+c, row = i-c;
							for(int m=row;m<b.rows;++m){
								if(b.getBoardMatrix()[m][column]==0)moreMoves++;
								else if(b.getBoardMatrix()[m][column]==1);
								else break;
							}
						} 
						if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
						aiScore=1;
						blanks = 0;
					}
				}

				if(i>=(b.winNumber - 1) && j>=(b.winNumber - 1)){
					for(k=1;k<b.winNumber;++k){
						if(b.getBoardMatrix()[i-k][j-k]==1)aiScore++;
						else if(b.getBoardMatrix()[i-k][j-k]==2){aiScore=0;blanks=0;break;}
						else blanks++;                        
					}
					moreMoves=0;
					if(blanks>0){
						for(int c=1;c<b.winNumber;++c){
							int column = j-c, row = i-c;
							for(int m=row;m<b.rows;++m){
								if(b.getBoardMatrix()[m][column]==0)moreMoves++;
								else if(b.getBoardMatrix()[m][column]==1);
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










	//	public int state_eval(StateTree state) {
	//
	//		int value = 0;
	//
	//		// weight to have pieces more in middle / bottom
	//		int w1 = 10;
	//		// weight to be close to a win
	//		int w2 = 5;
	//		// weight to guarantee win
	//		int w3 = 500;
	//
	//		// Hi future Nathaniel I think that having a lot of pieces in the bottom row 
	//		// will definitely be good especially for popping
	//		// tokens near the middle probs better than the outside?
	//		for(int i = 0; i < state.rows; i++) {
	//
	//			for(int j = 0; j < state.columns; j++) {
	//
	//				if(state.getBoardMatrix()[i][j] == my_turn) {
	//
	//
	//					value += (state.rows - i) * w1;
	//					int val = j;
	//
	//					if(j >= (int)state.columns/2) {
	//
	//						val = state.columns - j;
	//					}
	//					value += val*w1;
	//				}
	//			}
	//		}
	//
	//
	//
	////		//check if opponent is in columns this is bad
	////		for(int j = 0; j < state.columns; j++) {
	////
	////			int counter = 0;
	////
	////			for(int i = 0; i < state.rows; i++) {
	////
	////				if(state.getBoardMatrix()[i][j] == opp_turn) {
	////
	////					counter += 1;
	////				}
	////			}
	////			if(counter == state.winNumber - 2) {
	////
	////				value  = 0;
	////			}
	////		}
	////		//check if opponent is in rows this is bad
	////		for(int i = 0; i < state.rows; i++){
	////
	////			int counter = 0;
	////
	////			for(int j = 0; j < state.columns; j++) {
	////
	////				if(state.getBoardMatrix()[i][j] == opp_turn) {
	////
	////					counter += 1;
	////				}
	////			}
	////			if(counter == state.winNumber - 2) {
	////
	////				value = 0;
	////			}
	////		}
	//
	//
	//		int counter = 1;
	//		int empty_space = 0;
	//		int moves = 0;
	//
	//		for(int i = 0; i < state.rows; i++) {
	//
	//			for(int j = 0; j < state.columns; j++) {
	//
	//
	//				if(state.getBoardMatrix()[i][j] == 0 || state.getBoardMatrix()[i][j] == opp_turn) {
	//					continue;
	//				}
	//
	//				if(j <= (state.winNumber-1)) {
	//
	//					for(int k = 1; k < state.winNumber; k++) {
	//
	//
	//						if(state.getBoardMatrix()[i][j+k] == my_turn) {
	//
	//							counter++;
	//						}
	//						else if(state.getBoardMatrix()[i][j+k] == opp_turn) {
	//
	//							counter = 0;
	//							empty_space = 0;
	//							break;
	//						}
	//						else {
	//							empty_space++;
	//						}
	//					}
	//					moves = 0;
	//
	//					if(empty_space > 0) {
	//
	//						for(int k = 1; k < state.winNumber; k++) {
	//							int column = j + k;
	//
	//							for(int m = i; m < state.winNumber; m++) {
	//
	//								if(state.getBoardMatrix()[m][column] == 0) {
	//
	//									moves++;
	//								}
	//								else {
	//									break;
	//								}
	//							}
	//						}
	//					}
	//
	//					if(moves != 0) {
	//
	//						int score = state.winNumber - moves;
	//
	//						for(int N = 0; N < state.winNumber; N++) {
	//
	//							if(counter == N) {
	//
	//								value += score*(int)Math.pow(10,(N - 1))*w2;
	//							}
	//						}
	//					}
	//					empty_space = 0;
	//					counter = 1;
	//				}
	//
	//				if(i >= (state.winNumber - 1)) {
	//
	//
	//					for(int k = 1; k < state.winNumber; k++) {
	//
	//
	//						if(state.getBoardMatrix()[i - k][j] == my_turn) {
	//
	//							counter++;
	//						}
	//						else if(state.getBoardMatrix()[i - k][j] == opp_turn) {
	//
	//							counter = 0;
	//							empty_space = 0;
	//							break;
	//						}
	//						else {
	//							empty_space++;
	//						}
	//					}
	//					moves = 0;
	//
	//					if(empty_space > 0) {
	//
	//						for(int k = 1; k < state.winNumber; k++) {
	//							int column = j - k;
	//
	//							for(int m = i; m < state.winNumber; m++) {
	//
	//								if(state.getBoardMatrix()[m][column] == 0) {
	//
	//									moves++;
	//								}
	//								else {
	//									break;
	//								}
	//							}
	//						}
	//					}
	//
	//					if(moves != 0) {
	//
	//						int score = state.winNumber - moves;
	//
	//						for(int N = 0; N < state.winNumber; N++) {
	//
	//							if(counter == N) {
	//
	//								value += score*(int)Math.pow(10,(N - 1))*w2;
	//							}
	//						}
	//					}
	//					empty_space = 0;
	//					counter = 1;
	//
	//				}
	//				
	//				if(j <= (state.winNumber - 1) && i >=(state.winNumber - 1)) {
	//					
	//					for(int k = 0; k < state.winNumber; k++) {
	//						
	//						
	//						if(state.getBoardMatrix()[i - k][j + k] == my_turn) {
	//							
	//							counter += 1;
	//						}
	//						else if(state.getBoardMatrix()[i - k][j + k] == opp_turn) {
	//							
	//							counter = 0;
	//							empty_space = 0;
	//							break;
	//						}
	//						else {
	//							empty_space++;
	//						}
	//						
	//					}
	//					moves = 0;
	//					if(empty_space > 0) {
	//						
	//						for(int k = 1; k < state.winNumber; k++) {
	//							
	//							int column = j + k;
	//							int row = i - k;
	//							
	//							for(int m = row; m < state.rows; m++) {
	//								
	//								if(state.getBoardMatrix()[m][column] == 0) {
	//									moves++;
	//								}
	//								else {
	//									break;
	//								}
	//							}
	//						}
	//					}
	//					if(moves != 0) {
	//
	//						int score = state.winNumber - moves;
	//
	//						for(int N = 0; N < state.winNumber; N++) {
	//
	//							if(counter == N) {
	//
	//								value += score*(int)Math.pow(10,(N - 1))*w2;
	//							}
	//						}
	//					}
	//					empty_space = 0;
	//					counter = 1;
	//					
	//				}
	//				
	//				if(i >= 3 && j >=3) {
	//					
	//					for(int k = 1; k < state.winNumber; k++) {
	//						
	//						if(state.getBoardMatrix()[i-k][j-k] == my_turn) {
	//							counter++;
	//						}
	//						else if(state.getBoardMatrix()[i-k][j-k] == opp_turn) {
	//							counter = 0;
	//							empty_space = 0;
	//							break;
	//						}
	//						else {
	//							empty_space++;
	//						}
	//					}
	//					moves = 0;
	//					if(empty_space > 0) {
	//						
	//						for(int k = 1; k < state.winNumber; k++) {
	//							
	//							for(int m = i - k; m < state.rows; m++) {
	//								
	//								if(state.getBoardMatrix()[m][j - k] == 0) {
	//									
	//									moves++;
	//								}
	//								else {
	//									break;
	//								}
	//							}
	//						}
	//					}
	//					
	//					if(moves != 0) {
	//
	//						int score = state.winNumber - moves;
	//
	//						for(int N = 0; N < state.winNumber; N++) {
	//
	//							if(counter == N) {
	//
	//								value += score*(int)Math.pow(10,(N - 1))*w2;
	//							}
	//						}
	//					}
	//					empty_space = 0;
	//					counter = 1;
	//				}
	//			}	
	//		}
	//
	//
	//
	//		// this more or less works how I want it to with detecting horizontal features
	//		// that guarantee a win if the rest of the places are filled in perfectly
	//		boolean has_my_f1 = false;
	//		int empty1 = -1;
	//		int my_row_feature = -1;
	//		// looking for, X = anything edge of board 2, 1, 0
	//		// X 1 1 1 0 X
	//		// X 0 1 1 1 X
	//		// X 1 1 0 1 X
	//		// X 1 0 1 1 X
	//
	//		for(int i = 0; i < state.rows; i ++) {
	//
	//			for(int j = 0; j < (state.columns - state.winNumber); j++) {
	//
	//				if(state.getBoardMatrix()[i][j] == my_turn || state.getBoardMatrix()[i][j] == 0) {
	//
	//					for(int n = 0; n < state.winNumber && empty1 == -1; n++) {
	//
	//
	//						my_row_feature = i + 1;
	//
	//						if(state.getBoardMatrix()[i][j + n] == 0) {
	//
	//							if(empty1 == -1) {
	//								empty1 = j + n;
	//							}
	//						}
	//						if(state.getBoardMatrix()[i][j + n] == opp_turn) {
	//							break;
	//						}
	//						if(n == (state.winNumber - 1)) {
	//
	//							int blank_counter = 0;
	//
	//							for(int x =  0; x < state.rows; x ++) {
	//
	//								for(int y = 0; y < state.columns; y++) {
	//
	//									if(y == empty1) {
	//
	//										break;
	//									}
	//									else {
	//
	//										if(state.getBoardMatrix()[x][y] == 0) {
	//
	//											blank_counter += 1;
	//										}
	//									}	
	//								}
	//							}
	//
	//							if(blank_counter%2 == 0 && my_row_feature%2 == 0) {
	//
	//								has_my_f1 = true;
	//								value += w3;
	//							}
	//						}
	//					}
	//				}
	//			}
	//		}
	//
	//
	//		boolean has_opp_f1 = false;
	//		int opp_empty1 = -1;
	//		int opp_row_feature = -1;
	//
	//		for(int i = 0; i < state.rows; i++) {
	//
	//			for(int j = 0; j < (state.columns - state.winNumber); j++) {
	//
	//				if(state.getBoardMatrix()[i][j] == opp_turn || state.getBoardMatrix()[i][j] == 0) {
	//
	//					for(int n = 0; n < state.winNumber && opp_empty1 == -1; n++) {
	//
	//
	//						opp_row_feature = i + 1;
	//
	//						if(state.getBoardMatrix()[i][j + n] == 0) {
	//
	//							if(opp_empty1 == -1) {
	//								opp_empty1 = j + n;
	//							}
	//						}
	//						if(state.getBoardMatrix()[i][j + n] == my_turn) {
	//							break;
	//						}
	//						if(n == (state.winNumber - 1)) {
	//
	//							int blank_counter = 0;
	//
	//							for(int x =  0; x < state.rows; x ++) {
	//
	//								for(int y = 0; y < state.columns; y++) {
	//
	//									if(y == opp_empty1) {
	//
	//										break;
	//									}
	//									else {
	//
	//										if(state.getBoardMatrix()[x][y] == 0) {
	//
	//											blank_counter += 1;
	//										}
	//									}	
	//								}
	//							}
	//
	//							if(blank_counter%2 == 0 && opp_row_feature%2 == 0) {
	//
	//								has_opp_f1 = true;
	//								value = 0;
	//							}
	//						}
	//					}
	//				}
	//			}
	//		}
	//
	//		//		System.out.println("Evaluated State");
	//		//		state.display();
	//		//		System.out.println("My Feature = " + has_my_f1);
	//		//		System.out.println("My Row = " + my_row_feature);
	//		//		System.out.println("Opp Feature = " + has_opp_f1);
	//		//		System.out.println("Opp Row = " + opp_row_feature);
	//		//		System.out.println("Value of State = " + value);
	//
	//		return value;
	//	}
	//
	//	
	public int terminal_test(StateTree board)
	{
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
	public int checkConnect(StateTree board)
	{
		int winner = 0;
		int[] count = new int[board.winNumber];
		int winTotal = 0;
		for(int i=0; i<board.rows; i++)
		{
			for(int j=0; j<board.columns; j++)
			{
				if(board.getBoardMatrix()[i][j] == 0)
				{
					winner = 0;
					for(int x=0; x<board.winNumber; x++)
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
				for(int x=0; x<board.winNumber; x++)
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

	public int heval(StateTree state) {

		int[][] points = new int[3][state.columns]; 

		for(int j = 0; j < state.columns; j++) {

			for(int i = 0; i < state.rows; i++) {

				points[0][j] = -1;
				points[1][j] = -1;
				points[2][j] = -1;
			}
		}

		for(int j = 0; j < state.columns; j++) {

			boolean fuckJava = true;

			for(int i = 0; i < state.rows; i++) {

				if(state.getBoardMatrix()[i][j] == 0 && fuckJava) {

					points[0][j] = i;
					points[1][j] = j;
					fuckJava = false;
				}
			}
		}

		for (int n = 0; n < points[0].length; n++) {				
			//		points[0][n] <- height
			//      points[1][n] <- column

			int my_hCount = 0;
			int my_vCount = 0;
			int my_uDiagCount = 0;
			int my_dDiagCount = 0;
			int my_moveVal = 0;

			int op_hCount = 0;
			int op_vCount = 0;
			int op_uDiagCount = 0;
			int op_dDiagCount = 0;
			int op_moveVal = 0;

			if(points[0][n] != -1 && points[1][n] != -1) {

				//my token count for each move
				for(int w = points[0][n] + 1; w < state.rows; w++) {

					if(state.getBoardMatrix()[w][points[1][n]] == this.turn) {

						my_vCount += 1;
					}
					else {
						break;
					}
				}
				for(int w = points[0][n] - 1; w > -1; w--) {

					if(state.getBoardMatrix()[w][points[1][n]] == this.turn) {

						my_vCount += 1;
					}
					else {
						break;
					}
				}
				for(int x = points[1][n] + 1; x < state.columns; x++) {

					if(state.getBoardMatrix()[points[0][n]][x] == this.turn) {

						my_hCount += 1;
					}
					else {
						break;
					}
				}
				for(int x = points[1][n] - 1; x > -1; x--) {

					if(state.getBoardMatrix()[points[0][n]][x] == this.turn) {

						my_hCount += 1;
					} 
					else {
						break;
					}
				}
				for(int y = points[1][n] + 1; y < state.columns; y++) {

					int row_counter = 1;

					if(points[0][n] + row_counter < state.rows) {

						if(state.getBoardMatrix()[points[0][n] + row_counter][y] == this.turn) {

							my_uDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					} 
					else {
						break;
					}
				}
				for(int y = points[1][n] - 1; y > -1; y--) {

					int row_counter = 1;

					if(points[0][n] - row_counter > -1) {
						if(state.getBoardMatrix()[points[0][n] - row_counter][y] == this.turn) {

							my_uDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					}
					else {
						break;
					}
				}
				for(int z = points[1][n] + 1; z < state.columns; z++) {

					int row_counter = 1;

					if(points[0][n] - row_counter > -1) {
						if(state.getBoardMatrix()[points[0][n] - row_counter][z] == this.turn) {

							my_dDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					}
					else {
						break;
					}
				}
				for(int z = points[1][n] - 1; z > -1; z--) {

					int row_counter = 1;

					if(points[0][n] + row_counter < state.rows) {
						if(state.getBoardMatrix()[points[0][n] + row_counter][z] == this.turn) {

							my_dDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					}
					else {
						break;
					}
				}

				// ----------------------------- opponents token count for each move ---------------------

				for(int w = points[0][n] + 1; w < state.rows; w++) {

					if(state.getBoardMatrix()[w][points[1][n]] != this.turn && state.getBoardMatrix()[w][points[1][n]] > 0) {

						op_vCount += 1;
					}
					else {
						break;
					}
				}
				for(int w = points[0][n] - 1; w > -1; w--) {

					if(state.getBoardMatrix()[w][points[1][n]] != this.turn && state.getBoardMatrix()[w][points[1][n]] > 0) {

						op_vCount += 1;
					}
					else {
						break;
					}
				}
				for(int x = points[1][n] + 1; x < state.columns; x++) {

					if(state.getBoardMatrix()[points[0][n]][x] != this.turn && state.getBoardMatrix()[points[0][n]][x] > 0) {

						op_hCount += 1;
					}
					else {
						break;
					}
				}
				for(int x = points[1][n] - 1; x > -1; x--) {

					if(state.getBoardMatrix()[points[0][n]][x] != this.turn && state.getBoardMatrix()[points[0][n]][x] > 0) {

						op_hCount += 1;
					} 
					else {
						break;
					}
				}
				for(int y = points[1][n] + 1; y < state.columns; y++) {

					int row_counter = 1;

					if(points[0][n] + row_counter < state.rows) {

						if(state.getBoardMatrix()[points[0][n] + row_counter][y] != this.turn && state.getBoardMatrix()[points[0][n] + row_counter][y] > 0) {

							op_uDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					} 
					else {
						break;
					}
				}
				for(int y = points[1][n] - 1; y > -1; y--) {

					int row_counter = 1;

					if(points[0][n] - row_counter > -1) {
						if(state.getBoardMatrix()[points[0][n] - row_counter][y] != this.turn && state.getBoardMatrix()[points[0][n] - row_counter][y] > 0) {

							op_uDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					}
					else {
						break;
					}
				}
				for(int z = points[1][n] + 1; z < state.columns; z++) {

					int row_counter = 1;

					if(points[0][n] - row_counter > -1) {
						if(state.getBoardMatrix()[points[0][n] - row_counter][z] != this.turn && state.getBoardMatrix()[points[0][n] - row_counter][z] > 0) {

							op_dDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					}
					else {
						break;
					}
				}
				for(int z = points[1][n] - 1; z > -1; z--) {

					int row_counter = 1;

					if(points[0][n] + row_counter < state.rows) {
						if(state.getBoardMatrix()[points[0][n] + row_counter][z] != this.turn && state.getBoardMatrix()[points[0][n] + row_counter][z] > 0) {

							op_dDiagCount += 1;

						}
						else {
							break;
						}
						row_counter += 1;
					}
					else {
						break;
					}
				}


				// calculate value of move based on lengths of hCount, vCount, dDiagCount, uDiagCount
				for(int N = 1; N < state.winNumber; N++) {

					int my_weight = (int) Math.pow(2, (state.winNumber - N));
					int op_weight = (int) Math.pow(((state.winNumber - N) + 1), 2);

					if(my_hCount == (state.winNumber - N)) {

						my_moveVal += (my_weight * my_hCount);
					}
					if(my_vCount == (state.winNumber - N)) {

						my_moveVal += (my_weight * my_vCount);
					}
					if(my_uDiagCount == (state.winNumber - N)) {

						my_moveVal += (my_weight * my_uDiagCount);
					}
					if(my_dDiagCount == (state.winNumber - N)) {

						my_moveVal += (my_weight * my_dDiagCount);
					}
					// ------------------ Opponents value ---------------------------------					
					if(op_hCount == (state.winNumber - N)) {

						op_moveVal += (op_weight * op_hCount);
					}
					if(op_vCount == (state.winNumber - N)) {

						op_moveVal += (op_weight * op_vCount);
					}
					if(op_uDiagCount == (state.winNumber - N)) {

						op_moveVal += (op_weight * op_uDiagCount);
					}
					if(op_dDiagCount == (state.winNumber - N)) {

						op_moveVal += (op_weight * op_dDiagCount);
					}
				}
			}
			//			System.out.println("@Point: " + points[1][n] + " , " + points[0][n] + 
			//					"\nHorizontal: " + op_hCount + 
			//					"\nVertical: " + op_vCount + 
			//					"\nUp Diag: " + op_uDiagCount + 
			//					"\nDown Diag: " + op_dDiagCount + 
			//					"\nMove Value: " + op_moveVal);
			//			System.out.println("my: " + my_moveVal + " op: " + op_moveVal);
			points[2][n] = my_moveVal + op_moveVal;
		}

		//		System.out.println("points: " + Arrays.toString(points[0]) + "\n\t" + 
		//										Arrays.toString(points[1]) + "\n\t" + 
		//										Arrays.toString(points[2]));

		int max = 0;
		//		int maxJ = 0;

		for(int m = 0; m < points[0].length; m++) {

			max = max + points[2][m];
		}

		return max;

		//		if(max == 0) {
		//			
		//			return new Move(false, (int) (state.columns*Math.random()));
		//		}
		////		System.out.println("Move Value : " + max + " Column: " + maxJ);
		//		else {
		//			
		//			return new Move(false, maxJ);
		//		}
	}
}
