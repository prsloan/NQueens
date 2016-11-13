package build;

import java.util.ArrayList;
import java.util.Random;

public class Board {
	int size;
	//Using a one-dimensional array.  The value of each column will be the row in which the queen resides
	//this cuts down on space and the need to iterate needlessly
	//For printing the puzzle a separate array will be made in the to String method, so that each instance
	// of the class doesn't make a lot of unnecessary stuff (since a lot of these will get created)
	int [] board;
	int currentCost;
	//this is a grid that will contain the h cost of all the possible queen moves, given the state of the board
	int [][] neighborCostGrid;
	
	//Some things for the genetic algorithm
	int fitness; //this will be the number of non attacking pairs (max - hScore basically)
	int maxConflict;
	
	
	
	
	
	public Board(int size) {
		this.size = size;
		board = new int[size];
		maxConflict = (size*size+size)/2; 
	}	
	
	public void moveQueen(int column, int newPosition){
		board[column]= newPosition;
	}
	
	public int getQueenPosition(int column){
		return board[column];
	}
	
	public int getFitness(){
		return fitness;
	}
	/**
	 * This method will randomly populate the board with queens, so to speak
	 */
	public void randomizeBoard(){
		Random r = new Random();	
		for (int i = 0; i<board.length;i++){
			board[i] = r.nextInt(board.length);
		}
		//initialize cost and fitness
		this.getSteepestHillHCost();
	}
	
	/**
	 *  Since we move the queens only in the column they are, we can generate a grid
	 *  that represents the costs at each potential move. This will be used to decide
	 *  which move to make.  Time complexity on this is um, not fantastic since each time the
	 *  cost is calculated for a square it is O(n^2), so this is O(n^4). 
	 */
	public void updateCostGridSteepestHill(){
		neighborCostGrid = new int[size][size];
		
		//for each Queen (column) 
		for (int i = 0; i< board.length; i++){
			int currentPosition = board[i]; //store this so we can restore the state
			for (int j = 0; j<board.length;j++){
				//put a queen in the current square
				this.moveQueen(i, j);
				neighborCostGrid[i][j] = this.getSteepestHillHCost();
			}
			//put the queen back
			this.moveQueen(i, currentPosition);
		}
		
		
		
	}
	
	
	
	/**
	 * This Method will calculate the steepest-hill cost
	 * as the number of pairs of queens attacking each other.
	 * 
	 * Time complexity of this is O(n^2).
	 * 
	 * @return heuristic cost of the current board state. Higher values mean more
	 * queens attacking each other.  A cost of 0 is the goal state.
	 * 
	 */
	public int getSteepestHillHCost(){
		int cost = 0;
		
		//check each columns queen to see what queens it threatens
		for(int i=0; i<board.length; i++){
			for (int j = i+1; j< board.length;j++){
				if (board[i] == board[j])
					cost++;
				//now check diagonals , if the column has a value equal 
				//to the current column +/- the offset then it is being attacked
				// on the diagonal
				int offset = j-i;
				if ((board[i] == board[j]-offset)||(board[i]== board[j]+offset))
					cost++;
			}
		}
		this.currentCost=cost;
		this.fitness = maxConflict - cost;
		return cost;
	}
	
	/**
	 * This method will print the current cost grid. Used for testing.
	 */
	public void printCostGrid(){
		if (neighborCostGrid == null){
			System.out.println("Cost Grid Not Initialized.");
		}
		else{
			for (int i=0;i<size;i++){
				System.out.print(" | ");
				for (int j =0 ; j< size; j++){
					System.out.print(neighborCostGrid[j][i]+" | ");
				}
				System.out.print("\n");
			}
		}
			
	}
	
	/**
	 * Returns the cost at a particular place on the grid.
	 * 
	 * @param col
	 * @param row
	 * @return the value at that column and row.
	 */
	public int getCost(int col, int row){
		return neighborCostGrid[col][row];
	}
	
	/**
	 * This will get the lowest cost from the grid.
	 * NOTE: Call this only once the cost grid has been instantiated.
	 * 
	 * @return
	 */
	public int getLowestCost(){
		int lowest = Integer.MAX_VALUE;
		for (int i=0;i<board.length;i++){
			for (int j = 0; j<board.length;j++){
				if (this.getCost(i, j)< lowest)
					lowest = this.getCost(i, j);
			}
		}
		
		return lowest;
	}
	
	/**
	 * 
	 * 
	 * NOTE: Call this only once the cost grid has been initialized.
	 * 
	 * @return
	 */
	public ArrayList<Move> generateLowestMoves(){
		ArrayList<Move> moves = new ArrayList<Move>();
		
		int lowest = this.getLowestCost();
		for (int i=0;i<board.length;i++){
			for (int j = 0; j<board.length;j++){
				if (this.getCost(i, j) == lowest){
					moves.add(new Move(i,j));
				}
			}
		}
		
		
		return moves;
	}
	
	
	
	
	public String toString(){
		StringBuilder s = new StringBuilder();
		
		for (int i=0;i<board.length;i++){
			for (int j = 0; j<board.length;j++){
				if (board[j] == i)
					s.append("Q");
				else
					s.append("*");
			}
			s.append("\n");
		}
		
		return s.toString();
		
	}
	
}
