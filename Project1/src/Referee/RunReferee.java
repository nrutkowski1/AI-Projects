package Referee;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import Players.NathanielGoodPlayer;
import Players.Player;
import Players.SimplePlayer1;
import Players.SimplePlayer2;
import Players.TestAI;
import Players.HumanPlayer;
import Players.NP2;

public class RunReferee {

	public static void main(String[] args) {
		
		// Match parameter
		// You can modify them
		int timeLimit = 1000;
		int boardRows = 6;
		int boardColumns = 7;
		int winNumber = 4;
		int battleDurationLimit = 3600;
		// End of modifications
		
		Player player1 = (Player) new NathanielGoodPlayer("My AI", 1, timeLimit);
		Player player2 = (Player) new HumanPlayer("Test AI", 2, timeLimit);

		Referee referee = new Referee();
		referee.setOut(System.out);
		referee.initMatch(boardRows, boardColumns, winNumber, timeLimit, player1, player2);
		Callable<Object> judge1 = new Callable<Object>() {
		 	public Object call() {
			  return referee.judge();
		 	}
		};			
		ExecutorService service = Executors.newSingleThreadExecutor();
		
		final Future<Object> future1 = service.submit(judge1);
		int result = -1;
		try {
			result = (int) future1.get(battleDurationLimit, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}
}