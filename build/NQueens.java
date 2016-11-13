package build;

import java.util.ArrayList;
import java.util.Random;

public class NQueens {
	
	public NQueens() {
		
	}

	public static boolean solveBoardSteepestClimb(Board b){
		int currentCost = b.getSteepestHillHCost(); //initialize what 
		int lowestCost = b.getLowestCost();// initialize this
		int moves = 0;
		ArrayList<Move> successors;
		Move nextMove;
		Random r = new Random();
		//testing print
		System.out.println(b.toString());
		//base case, it's already solved
		if (currentCost == 0){
			return true;
		}
		//check if
		while (currentCost != 0){
			//Check if current cost is the lowest
			if (currentCost == lowestCost){
				System.out.println("Puzzle Not Solved." + moves +" moves made.");
				System.out.println(b.toString());
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
		
		System.out.println(" Puzzle Solved in "+moves+ " ! ");
		System.out.println(b.toString());
		
		return true;
	}
	
	
	public static void solveWithGeneticAlgorithm(int populationSize, int boardSize, double mutationRate, double crossoverRate){
		
		Random r = new Random();
		//initialize a population of 50 boards
		Population population = new Population(populationSize, boardSize);
		population.sortByFitness();
		double start, stop, length;
		
		start = System.currentTimeMillis();
		while (!population.hasSolution()){
			Population newPopulation = new Population(0, boardSize); //create empty population
			for(int i=0;i< population.size;i++){
				Board father = population.randomSelect();
				Board mother = population.randomSelect();
				//check to crossover
				if( r.nextDouble() < crossoverRate){
					int crossoverPoint = r.nextInt(father.size);
					//crossover and make two children 
					Board child = population.reproduceCrossover(father, mother, crossoverPoint);
					child = population.mutateChromosome(child, mutationRate);
					newPopulation.addChromosome(child);
					
				}
				else{
					//no crossover pick a parent to put back in the population
					if(r.nextDouble()<.5)
						newPopulation.addChromosome(father);
					else
						newPopulation.addChromosome(mother);
				}
				
			}
			population = newPopulation;
			population.sortByFitness();
		}	
		stop = System.currentTimeMillis();
		length = stop-start;
		//population.printSolution();
		//System.out.println("Solved in " + length + " ms ");
		System.out.println(populationSize+" "+boardSize+ " "+ length);
	}
	
	
	
	public static void main(String[] args) {
		/**
		ArrayList<Board> boards = new ArrayList<Board>();
		int solved =0;
		float percentSolved;
		int numberOfBoards = 1000;
		for (int i=0; i<numberOfBoards;i++){
			Board temp = new Board(19);
			temp.randomizeBoard();
			temp.updateCostGridSteepestHill();
			boards.add(temp);
		}
		//now that all the boards are there, solve them and print stats
		for(Board b: boards){
			if (solveBoardSteepestClimb(b)){
				solved++;
			}
		}
		percentSolved = (float) solved/numberOfBoards*100;
		System.out.println(percentSolved + "% of Boards Solved.");
		**/
		//check how population size effects solution
		for(int i = 19; i <100;i++){
				solveWithGeneticAlgorithm(2*i, i, .02, .72);
		}
		
	}

}
