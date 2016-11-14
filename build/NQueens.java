package build;

import java.util.ArrayList;
import java.util.Random;

/**
 * Project Description : The purpose of this project is to examine two local search techniques.  
 * Steepest climb and the Genetic Algorithm will be used to solve the N-queens problem. 
 * 
 * Class : CS 420 - Artificial Intelligence
 * 
 * 
 * @author Philip Sloan
 * Date : November 13, 2016
 */
public class NQueens {
	double  steepTimeSum = 0;
	int moveSum = 0;
	
	public void resetSums(){
		steepTimeSum = 0;
		moveSum = 0;
	}
	
	/**
	 * This method will attempt to solve the board using the
	 * Steepest Climb algorithm.
	 * 
	 * 
	 * @param b
	 * @return Returns true if the board was solved
	 */
	public boolean solveBoardSteepestClimb(Board b){
		int currentCost = b.getSteepestHillHCost(); //initialize what 
		int lowestCost = b.getLowestCost();// initialize this
		int moves = 0;
		ArrayList<Move> successors;
		Move nextMove;
		Random r = new Random();
		
		double start, stop, length;
		
		start = System.currentTimeMillis();
		//base case, it's already solved
		if (currentCost == 0){
			return true;
		}
		//check if
		while (currentCost != 0){
			//Check if current cost is the lowest
			if (currentCost == lowestCost){
				//System.out.println(b.toString());
				moveSum+=moves;
				stop = System.currentTimeMillis();
				length = stop-start;
				steepTimeSum+=length;
				return false;
			}
			//update neighbors
			successors = b.generateLowestMoves();
			//pick a random neighbor that is lowest
			nextMove = successors.get(r.nextInt(successors.size()));
			//move the queen
			b.moveQueen(nextMove.getCol(), nextMove.getRow());
			moves++;
			//update costs
			b.updateCostGridSteepestHill();
			lowestCost = b.getLowestCost();
			currentCost = b.getSteepestHillHCost();
			
		}
		stop = System.currentTimeMillis();
		length = stop-start;
		steepTimeSum+=length;
		moveSum+=moves;
		
		//System.out.println(b.toString());
		
		return true;
	}
	
	/**
	 * This method solves the NQueens problem using the genetic algorithm.  It will breed selections that have
	 * higher fitness on average to converge towards the solution.
	 * 
	 * 
	 * @param populationSize The inital population size
	 * @param boardSize size of the board to be solved
	 * @param mutationRate the rate at which new children will be mutated.
	 * @param crossoverRate the rate at which selected parents will be bred.
	 */
	public void solveWithGeneticAlgorithm(int populationSize, int boardSize, double mutationRate, double crossoverRate){
		double generations =0;
		int crossovers = 0;
		int mutations = 0;
		Random r = new Random();
		//initialize a population of 50 boards
		Population population = new Population(populationSize, boardSize);
		population.sortByFitness();
		double start, stop, length;
		
		start = System.currentTimeMillis();
		while (!population.hasSolution()){
			generations++;
			Population newPopulation = new Population(0, boardSize); //create empty population
			for(int i=0;i< population.size;i++){
				//select new parents
				Board father = population.randomSelect();
				Board mother = population.randomSelect();
				//check to crossover, if the probability threshold is met
				if( r.nextDouble() < crossoverRate){
					int crossoverPoint = r.nextInt(father.size);
					//crossover and make a new child
					Board child = population.reproduceCrossover(father, mother, crossoverPoint);
					child = population.mutateChromosome(child, mutationRate);
					newPopulation.addChromosome(child);
					crossovers++;
				}
				else{
					//no crossover pick better parent to put back in the population
					if(father.getFitness()>=mother.getFitness())
						newPopulation.addChromosome(father);
					else
						newPopulation.addChromosome(mother);
				}
				
			}
			mutations += population.mutations;
			population = newPopulation;
			population.sortByFitness();
		}	
		stop = System.currentTimeMillis();
		length = stop-start;
		population.printSolution();
		System.out.println("\nFor Board Size : "+boardSize+ " Solution found in : " + length + " ms \n Number of generations : "+generations);
		System.out.println("Number of offspring generated : " + crossovers + "  # of Mutations : " +mutations);
	}
	
	
	
	public static void main(String[] args) {
		NQueens solver = new NQueens();
		System.out.println(" Testing Steepest Hill Algorithm from n=8 to n=19 for 1000 random boards (Times in ms)\n ----------------------------------------------------------------------------\n");
		for (int i=8; i<20;i++){
			ArrayList<Board> boards = new ArrayList<Board>();
			int solved =0;
			float percentSolved;
			int numberOfBoards = 1000;
			for (int j=0; j<numberOfBoards;j++){
				Board temp = new Board(i);
				temp.randomizeBoard();
				temp.updateCostGridSteepestHill();
				boards.add(temp);
			}
			//now that all the boards are there, solve them and print stats
			for(Board b: boards){
				if (solver.solveBoardSteepestClimb(b)){
					solved++;
				}
			}
			
			percentSolved = (float) solved/numberOfBoards*100;
			System.out.println("For "+ i + " X "+ i + " Board :");
			System.out.println(percentSolved + "% of Boards Solved. Average Time : " + solver.steepTimeSum/numberOfBoards+ " Average Moves : " + solver.moveSum/numberOfBoards+ "\n");
			solver.resetSums();
		}
		//check how population size effects solution
		System.out.println("\n Testing Genetic Algorithm from n=8 to n=50\n ------------------------------------------------------------");
		for(int i = 8; i <51;i++){
			
				solver.solveWithGeneticAlgorithm(2*i, i, .019, .72);
		}
		
	}

}
