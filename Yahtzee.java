/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
		scorecardArray = new int[nPlayers+1][TOTAL+1];
		checkCatArray = new boolean[nPlayers+1][TOTAL+1];
		
		for (int i = 1; i<=nPlayers && !game_end; i++){
			println(scorecardArray[1][1]);
			display.printMessage("It is player " + i + "'s turn.");
			display.waitForPlayerToClickRoll(i); //waits for the selected player (i) to click roll. 
			rollDice();
			int category;
			int categoryScore;
			category = display.waitForPlayerToSelectCategory();
			boolean check = checkCategory(category);
			if (check == true){
				categoryScore = calculateCategory(category);		
			}
			else{
				categoryScore = 0;
			}
			display.updateScorecard(category, i, categoryScore);
			updateScorecardArray(category, i, categoryScore);
			display.updateScorecard(TOTAL, i, scorecardArray[i][TOTAL]);
			game_end = checkEnd(category, i);
			if (game_end)showFinalScore();			
			else if (i == nPlayers) i = 0; //ends turn, resets playerID to 1 when in combination with for loop.
		}
		display.printMessage("GAME OVER");
	}
	private void showFinalScore(){
		for (int i=1; i<=nPlayers; i++){
			display.updateScorecard(UPPER_SCORE, i, scorecardArray[i][UPPER_SCORE]);
			display.updateScorecard(UPPER_BONUS, i, scorecardArray[i][UPPER_BONUS]);
			display.updateScorecard(LOWER_SCORE, i, scorecardArray[i][LOWER_SCORE]);
			display.updateScorecard(TOTAL, i, scorecardArray[i][TOTAL]);
		}
		
	}
	private void rollDice(){
		for (int i = 0; i<diceArrays.length; i++){
			diceArrays[i] = rgen.nextInt(1, 6);
		}
		display.displayDice(diceArrays);
		
		//enters the reroll phase
		
		for (int i = 0; i>=1; i--){
			display.printMessage("You are in reroll phase.");
			display.waitForPlayerToSelectDice();
			for (int j = 0; j<diceArrays.length; j++){
				if (display.isDieSelected(j)){
					diceArrays[j] = rgen.nextInt(1,6);
				}
			}
			display.displayDice(diceArrays);
		}
		display.printMessage("You can't roll anymore. Select a category for scoring.");
	}
	private int calculateCategory(int category){
		int score = 0;
		//calculates the category score if selected category <= 6
		if (category <= SIXES){
			for (int i = 0; i<diceArrays.length; i++){
				if (diceArrays[i] == category) score += category;
			}
			println("score: " + score);
			return score;
		}
		else if (category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND || category == CHANCE){
			for (int k = 0; k<diceArrays.length; k++){
				score += diceArrays[k];
			}
			return score;
		}
		else if (category == FULL_HOUSE){
			score = 25;
			return score;
		}
		else if (category == SMALL_STRAIGHT){
			score = 30;
			return score;
		}
		else if (category == LARGE_STRAIGHT){
			score = 40;
			return score;
		}
		else if (category == YAHTZEE){
			score = 50;
			return score;
		}
		else return 0;
	}
	
	private boolean checkCategory(int temp){
			if (temp == THREE_OF_A_KIND){
				for (int i = 0; i < 3; i++){
					int tempCounter = 0;
					for (int j = 0; j<diceArrays.length; j++){
						if (diceArrays[j] == diceArrays[i]){
							tempCounter++;
						}
					}
					if (tempCounter >= 3){
						return true;
					}
				}
				return false;
			}
			
			//for four of a kind
			else if (temp == FOUR_OF_A_KIND){
				for (int i = 0; i < 3; i++){
					int tempCounter = 0;
					for (int j = 0; j<diceArrays.length; j++){
						if (diceArrays[j] == diceArrays[i]){
							tempCounter++;
						}
					}
					if (tempCounter >= 4){
						return true;
					}
				}
				return false;
			}
			//for full house
			else if (temp == FULL_HOUSE){
				for (int i = 0; i<diceArrays.length; i++){
					int e1Counter = 0;
					for (int j = 0; j<diceArrays.length; j++){
						if (diceArrays[j] == diceArrays[i]){
							e1Counter++;
						}
					}
					if (e1Counter == 2 || e1Counter == 3){
						for (int k = 0; k<diceArrays.length; k++){
							int e2Counter = 0;
							if(diceArrays[k] != diceArrays[i]){
								for (int l = 0; l<diceArrays.length; l++){
									if(diceArrays[k] == diceArrays[l]){
										e2Counter++;
										println("e2Counter:" + e2Counter);
									}
								}
							}
							if ( (e1Counter == 2 && e2Counter == 3) || (e1Counter == 3 && e2Counter == 2)){
								return true;
							}
						}
					}
				}
				return false;
			}
			//for small straight
			else if (temp == SMALL_STRAIGHT){
				for (int i = 0; i<diceArrays.length; i++){
					int straightStart = diceArrays[i];
					int straightCounter = 1;
					for (int j = 0; j<diceArrays.length; j++){
						if (diceArrays[j] == straightStart + 1){
							straightStart++;
							straightCounter++;
							j = -1; //resets the counter if there was a straight amount. -1, because j will increment after this if statement. 
						}
					}
					if (straightCounter >= 4){
						return true;
					}
				}
				return false;
			}
			//for large straight
			else if (temp == LARGE_STRAIGHT){
				for (int i = 0; i<diceArrays.length; i++){
					int straightStart = diceArrays[i];
					int straightCounter = 1;
					for (int j = 0; j<diceArrays.length; j++){
						if (diceArrays[j] == straightStart + 1){
							straightStart++;
							straightCounter++;
							j = -1;
						}
					}
					if (straightCounter == 5){
						return true;
					}
				}
				return false;
			}
			//for yahtzee
			else if (temp == YAHTZEE){
				for (int i = 0; i < 3; i++){
					int tempCounter = 0;
					for (int j = 0; j<diceArrays.length; j++){
						if (diceArrays[j] == diceArrays[i]){
							tempCounter++;
						}
					}
					if (tempCounter == 5){
						return true;
					}
				}
				return false;
			}
			//for all others
			else{
				return true;
			}

	}
	private void updateScorecardArray(int category, int player, int categoryScore){
		
		println("scorecardArrayLength" + scorecardArray.length);
		println("scorecardArray[" + player + "][" + category + "] = " + categoryScore);
		scorecardArray[player][category] = categoryScore;
		scorecardArray[player][UPPER_SCORE] = upperScoreCalculator(player);
		if (scorecardArray[player][UPPER_SCORE] >= 63){
			scorecardArray[player][UPPER_BONUS] = 35;
		}
		scorecardArray[player][LOWER_SCORE] = lowerScoreCalculator(player);
		scorecardArray[player][TOTAL] = totalScoreCalculator(player);
		
	}
	private int lowerScoreCalculator(int player){
		int lower = 0;
		for (int i = 9; i<LOWER_SCORE; i++){
			lower += scorecardArray[player][i];
		}
		println("lower = " + lower);
		return lower;
	}
	private int upperScoreCalculator(int player){
		int upper = 0;
		for (int i = 1; i<UPPER_SCORE; i++){
			upper += scorecardArray[player][i];
		}
		println("upper = " + upper);
		return upper;
	}
	private int totalScoreCalculator(int player){
		int total = 0;
		for (int i = 1; i<(scorecardArray[0].length)-1; i++){
			if (i != UPPER_SCORE && i!=LOWER_SCORE){
				total += scorecardArray[player][i];
			}
		}
		println("total = " + total);
		return total;
	}
	private boolean checkEnd(int category, int player){
		checkCatArray[player][category] = true;
		
		boolean upper = false;
		boolean lower = false;
		for(int i = 1; i<=nPlayers; i++){
			for(int j = 1; j<=SIXES; j++){
				if (checkCatArray[i][j] == false){
					return false;
				}
				else{
					upper = true;
				}
			}
		}
		for (int i=1; i <=nPlayers; i++){
			for (int j=9; j<=CHANCE; j++){
				if (checkCatArray[i][j] == false){
					return false;
				}
				else{
					lower = true;
				}
			}
		}
		if (upper == true && lower == true){
			return true;
		}
		else{
			return false;
		}
	}
		
/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private int[] diceArrays = new int[N_DICE];
	private int[][] scorecardArray;
	private boolean[][] checkCatArray;
	private boolean game_end;

}
