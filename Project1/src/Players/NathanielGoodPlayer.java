package Players;

import java.util.ArrayList;
import java.util.Arrays;

import Utilities.Move;
import Utilities.StateTree;

public class NathanielGoodPlayer extends Player{

	int maxD = 1;
	int next_move = -1;

	boolean has_popped = false; 
	boolean never_pop_again = false;

	int my_turn;
	int opp_turn;
	boolean my_pop;
	boolean opp_pop;

	public NathanielGoodPlayer(String n, int t, int l) {
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

		long curr_time = System.currentTimeMillis();

		next_move = -1;

		minimax(0, my_turn, Integer.MIN_VALUE, Integer.MAX_VALUE, state, maxD, curr_time);

		if(has_popped && !never_pop_again) {
			has_popped = false;
			never_pop_again = true;
			System.out.println("Look Mom I Popped in column " + next_move);
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
						System.out.println("Score for location "+j+" = "+currentScore + " Popped");
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
						System.out.println("Score for location "+j+" = "+currentScore);
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

	public int state_eval(StateTree state) {

		int value = 0;

		// weight to have pieces more in middle / bottom
		int w1 = 400;
		// weight for numTokens + numEmptySpaces (up to 2) in a row for both self and opponent
		int w2 = 1000;
		int exp = 2;
		int my_fac = 2;
		// weight to guarantee win
		int w3 = 500;
		// good pop weight
		int w4 = 700;
		// X 0 0 1 1 0 X weight
		int w5 = 1000;


		if(state.getBoardMatrix()[0][(state.columns - 1) / 2] == 1) {

			value += 100000;
		}


		// getting players pop
		if(my_turn == 1) {

			my_pop = state.pop1;
			opp_pop = state.pop2;
		}
		else {
			my_pop = state.pop2;
			opp_pop = state.pop1;
		}


		// get 2 in a row with empty spaces on either side
		// X 0 0 1 1 0 X
		// X 0 1 1 0 0 X

		for(int i = 0; i < state.rows; i++) {

			int counter = 0;
			int bcount = 0;

			for(int j = 0; j < state.columns - (state.winNumber + 1); j++) {

				for(int k = 0; k < state.winNumber + 1; k++) {

					if(k == 0 && state.getBoardMatrix()[i][j + k] == 0) {
						counter++;
						bcount++;
					}
					else if(k == 1 && state.getBoardMatrix()[i][j+k] == 0) {
						counter++;
						bcount++;
					}
					else if(k == state.winNumber && state.getBoardMatrix()[i][j+k] == 0) {
						counter++;
						bcount++;
					}
					else if(k == state.winNumber - 1 && bcount == 1 && state.getBoardMatrix()[i][j+k] == 0) {
						counter++;
						bcount++;
					}
					else if(counter == 1 || counter == 2 && state.getBoardMatrix()[i][j+k] == my_turn) {
						counter++;
					}
				}
			}

			if(counter == state.winNumber + 1) {
				value += w5;
			}
		}


		// get 2 in a row with empty spaces on either side
		// X 0 0 1 1 0 X
		// X 0 1 1 0 0 X

		for(int i = 0; i < state.rows; i++) {

			int counter = 0;
			int bcount = 0;

			for(int j = 0; j < state.columns - (state.winNumber + 1); j++) {

				for(int k = 0; k < state.winNumber + 1; k++) {

					if(k == 0 && state.getBoardMatrix()[i][j + k] == 0) {
						counter++;
						bcount++;
					}
					else if(k == 1 && state.getBoardMatrix()[i][j+k] == 0) {
						counter++;
						bcount++;
					}
					else if(k == state.winNumber && state.getBoardMatrix()[i][j+k] == 0) {
						counter++;
						bcount++;
					}
					else if(k == state.winNumber - 1 && bcount == 1 && state.getBoardMatrix()[i][j+k] == 0) {
						counter++;
						bcount++;
					}
					else if(counter == 1 || counter == 2 && state.getBoardMatrix()[i][j+k] == opp_turn) {
						counter++;
					}
				}
			}

			if(counter == state.winNumber + 1) {
				value -= 3*w5;
			}
		}


		// good pop situations for my AI
		if(!my_pop) {
			// rows
			for(int i = 0; i < state.rows; i++) {

				int my_counter = 0;
				int opp_counter = 0;
				int opp_column = -1;
				int opp_row = -1;

				for(int j = 0; j < state.columns && opp_counter <= 1; j++) {

					if(state.getBoardMatrix()[i][j] == my_turn) {
						my_counter++;
					}
					else if(state.getBoardMatrix()[i][j] == opp_turn) {
						opp_counter++;
						opp_column = j;
						opp_row = i;
					}
				}

				for(int N = 0; N <= state.winNumber; N++) {

					if(opp_column != -1 && opp_row + 1 < state.rows) {
						if(my_counter == N && state.getBoardMatrix()[0][opp_column] == my_turn && state.getBoardMatrix()[opp_row + 1][opp_column] == my_turn) {

							value += w4*(int)Math.pow(2, N);

						}
					}
				}
			}

			// upper diagonal
			for(int i = 0; i < state.rows; i++) {

				int row_counter = 0;
				int my_counter = 0;
				int opp_counter = 0;
				int opp_column = -1;
				int opp_row = -1;

				for(int j = 0; j < state.columns && i + row_counter < state.rows && opp_counter <= 1; j++) {

					if(state.getBoardMatrix()[i + row_counter][j] == my_turn) {
						my_counter++;
					}
					else if(state.getBoardMatrix()[i + row_counter][j] == opp_turn) {

						opp_counter++;
						opp_row = i + row_counter;
						opp_column = j;
					}
					row_counter++;
				}
				for(int N = 0; N <= state.winNumber; N++) {

					if(opp_column != -1 && opp_row + 1 < state.rows) {
						if(my_counter == N && state.getBoardMatrix()[0][opp_column] == my_turn && state.getBoardMatrix()[opp_row + 1][opp_column] == my_turn) {

							value += w4*(int)Math.pow(2, N);

						}
					}
				}
			}

			for(int j = 1; j < state.columns; j++) {

				int column_counter = 0;
				int my_counter = 0;
				int opp_counter = 0;
				int opp_column = -1;
				int opp_row = -1;

				for(int i = 0; i < state.rows && j + column_counter < state.columns && opp_counter <= 1; i++) {

					if(state.getBoardMatrix()[i][j + column_counter] == my_turn) {
						my_counter++;
					}
					else if(state.getBoardMatrix()[i][j + column_counter] == opp_turn) {
						opp_counter++;
						opp_row = i;
						opp_column = j + column_counter;
					}
					column_counter++;
				}

				for(int N = 0; N <= state.winNumber; N++) {

					if(opp_column != -1 && opp_row + 1 < state.rows) {
						if(my_counter == N && state.getBoardMatrix()[0][opp_column] == my_turn && state.getBoardMatrix()[opp_row + 1][opp_column] == my_turn) {

							value += w4*(int)Math.pow(2, N);

						}
					}
				}			
			}
		}

		// good pop situations for opponent
		if(!opp_pop) {
			for(int i = 0; i < state.rows; i++) {

				int my_counter = 0;
				int opp_counter = 0;
				int opp_column = -1;
				int opp_row = -1;

				for(int j = 0; j < state.columns && opp_counter <= 1; j++) {

					if(state.getBoardMatrix()[i][j] == opp_turn) {
						my_counter++;
					}
					else if(state.getBoardMatrix()[i][j] == my_turn) {
						opp_counter++;
						opp_column = j;
						opp_row = i;
					}
				}

				for(int N = 0; N <= state.winNumber; N++) {

					if(opp_column != -1 && opp_row + 1 < state.rows) {
						if(my_counter == N && state.getBoardMatrix()[0][opp_column] == opp_turn && state.getBoardMatrix()[opp_row + 1][opp_column] == opp_turn) {

							value -= w4*(int)Math.pow(2, N);

						}
					}
				}
			}

			for(int i = 0; i < state.rows; i++) {

				int row_counter = 0;
				int my_counter = 0;
				int opp_counter = 0;
				int opp_column = -1;
				int opp_row = -1;

				for(int j = 0; j < state.columns && i + row_counter < state.rows && opp_counter <= 1; j++) {

					if(state.getBoardMatrix()[i + row_counter][j] == opp_turn) {
						my_counter++;
					}
					else if(state.getBoardMatrix()[i + row_counter][j] == my_turn) {

						opp_counter++;
						opp_row = i + row_counter;
						opp_column = j;
					}
					row_counter++;
				}
				for(int N = 0; N <= state.winNumber; N++) {

					if(opp_column != -1 && opp_row + 1 < state.rows) {
						if(my_counter == N && state.getBoardMatrix()[0][opp_column] == opp_turn && state.getBoardMatrix()[opp_row + 1][opp_column] == opp_turn) {

							value -= w4*(int)Math.pow(2, N);

						}
					}
				}
			}

			for(int j = 1; j < state.columns; j++) {

				int column_counter = 0;
				int my_counter = 0;
				int opp_counter = 0;
				int opp_column = -1;
				int opp_row = -1;

				for(int i = 0; i < state.rows && j + column_counter < state.columns && opp_counter <= 1; i++) {

					if(state.getBoardMatrix()[i][j + column_counter] == opp_turn) {
						my_counter++;
					}
					else if(state.getBoardMatrix()[i][j + column_counter] == my_turn) {
						opp_counter++;
						opp_row = i;
						opp_column = j + column_counter;
					}
					column_counter++;
				}

				for(int N = 0; N <= state.winNumber; N++) {

					if(opp_column != -1 && opp_row + 1 < state.rows) {
						if(my_counter == N && state.getBoardMatrix()[0][opp_column] == opp_turn && state.getBoardMatrix()[opp_row + 1][opp_column] == opp_turn) {

							value -= w4*(int)Math.pow(2, N);

						}
					}
				}			
			}



		}



		for(int i = 0; i < state.rows; i++) {

			for(int j = 0; j < state.columns; j++) {

				if(state.getBoardMatrix()[i][j] == my_turn) {


					value += (state.rows - i) * w1;
					int val = j;

					if(j >= (int)state.columns/2) {

						val = state.columns - j;
					}
					value += val*w1;
				}
			}
		}
		// check opponent center pieces
		for(int i = 0; i < state.rows; i++) {

			for(int j = 0; j < state.columns; j++) {

				if(state.getBoardMatrix()[i][j] == opp_turn) {


					value += (state.rows - i) * w1;
					int val = j;

					if(j >= (int)state.columns/2) {

						val = state.columns - j;
					}
					value -= val*w1;
				}
			}
		}

		//check if opponent is in columns this is bad
		for(int j = 0; j < state.columns; j++) {

			int counter = 0;
			int empty_space = 0; 

			for(int i = 0; i < state.rows && empty_space <= 2; i++) {

				if(state.getBoardMatrix()[i][j] == opp_turn) {
					counter++;
				}
				else if (state.getBoardMatrix()[i][j] == 0) {
					empty_space++;
				}
				else {
					break;
				}
			}

			if(counter + empty_space >= state.winNumber - 2) {

				value -= w2;
			}
			//			for(int N = 0; N <= state.winNumber + (state.winNumber - state.columns); N++) {
			//
			//				if(counter + empty_space == N) {
			//
			//					value -= w2*(int)Math.pow(N, exp);				
			//				}
			//				if(counter >= 2) {
			//					value -= w2*(int)Math.pow(N, 2*exp);	
			//				}
			//			}
		}
		//check if opponent is in rows this is bad
		for(int i = 0; i < state.rows; i++){

			int counter = 0;
			int empty_space = 0;

			for(int j = 0; j < state.columns && empty_space <= 2; j++) {

				if(state.getBoardMatrix()[i][j] == opp_turn) {

					counter++;
				}
				else if(state.getBoardMatrix()[i][j] == 0) {
					empty_space++;
				}
				else {
					break;
				}
			}
			if(counter + empty_space >= state.winNumber - 2) {

				value -= w2;
			}
			//			for(int N = 0; N <= state.winNumber + (state.winNumber - state.rows); N++) {
			//
			//				if(counter + empty_space == N) {
			//
			//					value -= w2*(int)Math.pow(N, exp);				
			//				}
			//				if(counter >= 2) {
			//					value -= w2*(int)Math.pow(N, 2*exp);	
			//				}
			//			}
		}
		//check if opponent is in upper diagonal this is bad
		for(int i = state.rows - 1; i > -1; i--) {

			for(int j = 0; j < state.columns; j++) {

				int counter = 0;
				int empty_space = 0;

				for(int k = 0; k < state.columns && (i + k < state.rows && j + k < state.columns); k++) {

					if(state.getBoardMatrix()[i + k][j + k] == opp_turn) {

						counter++;
					}
					else if(state.getBoardMatrix()[i + k][j + k] == 0) {
						empty_space++;
					}
					else {
						break;
					}
				}
				if(counter + empty_space >= state.winNumber - 2) {

					value -= w2;
				}
				//				for(int N = 0; N <= state.winNumber + (state.winNumber - state.rows); N++) {
				//
				//					if(counter + empty_space == N) {
				//
				//						value -= w2*(int)Math.pow(N, exp);				
				//					}
				//					if(counter >= 2) {
				//						value -= w2*(int)Math.pow(N, 2*exp);	
				//					}
				//				}
			}
		}

		//check if opponent is in lower diagonal this is bad
		for(int i = 0; i < state.rows; i++) {

			for(int j = 0; j < state.columns; j++) {

				int counter = 0;
				int empty_space = 0;

				for(int k = state.columns - 1; k > -1 && (i - k > - 1 && j - k > -1); k--) {

					if(state.getBoardMatrix()[i - k][j - k] == opp_turn) {

						counter++;
					}
					else if(state.getBoardMatrix()[i + k][j + k] == 0) {
						empty_space++;
					}
					else {
						break;
					}
				}
				if(counter + empty_space >= state.winNumber - 2) {

					value -= w2;
				}
				//				for(int N = 0; N <= state.winNumber + (state.winNumber - state.rows); N++) {
				//
				//					if(counter + empty_space == N) {
				//
				//						value -= w2*(int)Math.pow(N, exp);				
				//					}
				//					if(counter >= 2) {
				//						value -= w2*(int)Math.pow(N, 2*exp);	
				//					}
				//				}
			}
		}

		//check my columns
		for(int j = 0; j < state.columns; j++) {

			int counter = 0;
			int empty_space = 0; 

			for(int i = 0; i < state.rows && empty_space < 3; i++) {

				if(state.getBoardMatrix()[i][j] == my_turn) {
					counter++;
				}
				else if (state.getBoardMatrix()[i][j] == 0) {
					empty_space++;
				}
				else {
					break;
				}
			}
			for(int N = 0; N <= state.winNumber + (state.winNumber - state.columns); N++) {

				if(counter + empty_space == N) {

					value += my_fac*w2*(int)Math.pow(N, exp);				
				}
			}
		}
		//check my rows
		for(int i = 0; i < state.rows; i++){

			int counter = 0;
			int empty_space = 0;

			for(int j = 0; j < state.columns && empty_space < 3; j++) {

				if(state.getBoardMatrix()[i][j] == my_turn) {

					counter++;
				}
				else if(state.getBoardMatrix()[i][j] == 0) {
					empty_space++;
				}
				else {
					break;
				}
			}
			for(int N = 0; N <= state.winNumber + (state.winNumber - state.rows); N++) {

				if(counter + empty_space == N) {

					value += my_fac*w2*(int)Math.pow(N, exp);				
				}
			}
		}
		//check my upper diagonal
		for(int i = state.rows - 1; i > -1; i--) {

			for(int j = 0; j < state.columns; j++) {

				int counter = 0;
				int empty_space = 0;

				for(int k = 0; k < state.columns && (i + k < state.rows && j + k < state.columns); k++) {

					if(state.getBoardMatrix()[i + k][j + k] == my_turn) {

						counter++;
					}
					else if(state.getBoardMatrix()[i + k][j + k] == 0) {
						empty_space++;
					}
					else {
						break;
					}
				}
				for(int N = 0; N <= state.winNumber + (state.winNumber - state.rows); N++) {

					if(counter + empty_space == N) {

						value += my_fac*w2*(int)Math.pow(N, exp);				
					}
				}
			}
		}

		//check my lower diagonal
		for(int i = 0; i < state.rows; i++) {

			for(int j = 0; j < state.columns; j++) {

				int counter = 0;
				int empty_space = 0;

				for(int k = state.columns - 1; k > -1 && (i - k > - 1 && j - k > -1); k--) {

					if(state.getBoardMatrix()[i - k][j - k] == my_turn) {

						counter++;
					}
					else if(state.getBoardMatrix()[i + k][j + k] == 0) {
						empty_space++;
					}
					else {
						break;
					}
				}
				for(int N = 0; N <= state.winNumber + (state.winNumber - state.rows); N++) {

					if(counter + empty_space == N) {

						value += my_fac*w2*(int)Math.pow(N, exp);				
					}
				}
			}
		}

		// this more or less works how I want it to with detecting horizontal features
		// that guarantee a win if the rest of the places are filled in perfectly
		boolean has_my_f1 = false;
		int empty1 = -1;
		int my_row_feature = -1;
		// looking for, X = anything edge of board 2, 1, 0
		// X 1 1 1 0 X
		// X 0 1 1 1 X
		// X 1 1 0 1 X
		// X 1 0 1 1 X

		for(int i = 0; i < state.rows; i ++) {

			for(int j = 0; j < (state.columns - state.winNumber); j++) {

				if(state.getBoardMatrix()[i][j] == my_turn || state.getBoardMatrix()[i][j] == 0) {

					for(int n = 0; n < state.winNumber && empty1 == -1; n++) {


						my_row_feature = i + 1;

						if(state.getBoardMatrix()[i][j + n] == 0) {

							if(empty1 == -1) {
								empty1 = j + n;
							}
						}
						if(state.getBoardMatrix()[i][j + n] == opp_turn) {
							break;
						}
						if(n == (state.winNumber - 1)) {

							int blank_counter = 0;

							for(int x =  0; x < state.rows; x ++) {

								for(int y = 0; y < state.columns; y++) {

									if(y == empty1) {

										break;
									}
									else {

										if(state.getBoardMatrix()[x][y] == 0) {

											blank_counter += 1;
										}
									}	
								}
							}

							if(blank_counter%2 == 0 && my_row_feature%2 == 0) {

								has_my_f1 = true;
								value += w3;
							}
						}
					}
				}
			}
		}

		boolean has_opp_f1 = false;
		int opp_empty1 = -1;
		int opp_row_feature = -1;

		for(int i = 0; i < state.rows; i++) {

			for(int j = 0; j < (state.columns - state.winNumber); j++) {

				if(state.getBoardMatrix()[i][j] == opp_turn || state.getBoardMatrix()[i][j] == 0) {

					for(int n = 0; n < state.winNumber && opp_empty1 == -1; n++) {


						opp_row_feature = i + 1;

						if(state.getBoardMatrix()[i][j + n] == 0) {

							if(opp_empty1 == -1) {
								opp_empty1 = j + n;
							}
						}
						if(state.getBoardMatrix()[i][j + n] == my_turn) {
							break;
						}
						if(n == (state.winNumber - 1)) {

							int blank_counter = 0;

							for(int x =  0; x < state.rows; x ++) {

								for(int y = 0; y < state.columns; y++) {

									if(y == opp_empty1) {

										break;
									}
									else {

										if(state.getBoardMatrix()[x][y] == 0) {

											blank_counter += 1;
										}
									}	
								}
							}

							if(blank_counter%2 == 0 && opp_row_feature%2 == 0) {

								has_opp_f1 = true;
								value = 0;
							}
						}
					}
				}
			}
		}

		return value;
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
