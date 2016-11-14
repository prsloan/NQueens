package build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {
	
	//Size of the Population
	int size;
	//The Chromosomes will be 
	ArrayList<Board> chromosomes ; 
	//sum of the fitness
	int fitnessSum;
	int maxFitness = 0;
	//solution index (-1 until one is found
	int solutionIndex = -1;
	boolean hasSolution;
	int mutations;
	
	/**
	 * Initialize a population with an initial size.  If size is zero, an empty population
	 * will be created.
	 * 
	 * @param size
	 * @param boardSize
	 */
	public Population(int size, int boardSize) {
		this.size = size;
		hasSolution = false;
		mutations =0;
		chromosomes = new ArrayList<Board>();
		fitnessSum = 0;
		//generate a random population and initialize fitness sum
		for (int i=0; i<=size;i++){
			Board temp = new Board(boardSize);
			temp.randomizeBoard();
			fitnessSum+= temp.getFitness();
			chromosomes.add(temp);
		}
	}
	
	/**
	 * Add an individual to the population
	 * 
	 * @param b
	 */
	public void addChromosome(Board b){
		chromosomes.add(b);
		int newFitness = b.getFitness();
		fitnessSum+=newFitness;
		//solution found
		if (newFitness > maxFitness)
			maxFitness = newFitness;
		if (newFitness == b.maxConflict){
			hasSolution = true;
			solutionIndex = chromosomes.indexOf(b);
		}
		size++;
	}
	
	
	/**
	 * This method will select a parent for breeding.  It will use a ranked selection technique
	 * to pick the parent.  The best fitness parent will have the highest probability of being 
	 * selected, and the lowest rank will have a probability approaching zero.
	 * 
	 * 
	 * @return A random board from the population.
	 */
	public Board randomSelect(){
		Random r = new Random();
		double prob_sum = 0.0;
		//generate probabilities
		double probabilities[] = new double[chromosomes.size()];
		for(int i =0; i<chromosomes.size();i++){
			// The Rank / (N(N+1)/2)
			double prob = (double)(((double)i)/((double)size*((double)size+1)/2));
			probabilities[i] = prob_sum + prob;
			prob_sum = probabilities[i];
		}
		//doing this ensures a selection
			probabilities[probabilities.length-1] = 1.0;
		double choice = r.nextDouble();
		for(int i=0; i<chromosomes.size();i++){
			if (choice < probabilities[i])
				return chromosomes.get(i);
		}
		
		
		return chromosomes.get(chromosomes.size()-1);//this should never be reached
		
	}
	
	/**
	 * This method will create a child Board based on the two parent boards that 
	 * are inputs.  A crossover point will be selected at random and the boards
	 * will swap values after that point to create the child.
	 * 
	 * 
	 * @param father
	 * @param mother
	 * @param crossoverPoint
	 * @return
	 */
	public Board reproduceCrossover(Board father, Board mother, int crossoverPoint){
		Board child = new Board(father.size);
		
		for(int i = 0;i<child.size;i++){
			if (i <= crossoverPoint){
				child.moveQueen(i, father.getQueenPosition(i));
			}
			else
				child.moveQueen(i, mother.getQueenPosition(i));
		}
		//update costs
		child.getSteepestHillHCost(); 
		
		return child;
		
	}
	
	/**
	 * This will mutate values in the board at a certain rate.
	 * 
	 * @param b
	 * @param mutationRate
	 * @return
	 */
	public Board mutateChromosome(Board b , double mutationRate){
		Board mutant = b;
		boolean isMutated = false;
		Random r = new Random();
		
		for (int i = 0 ; i< b.size; i++){
			double mutate = r.nextDouble();
			if (mutate < mutationRate){
				int moveTo;
				do{
				moveTo = r.nextInt(b.size);
				}while(moveTo==b.getQueenPosition(i));
				mutant.moveQueen(i, moveTo);
				isMutated=true;
				this.mutations++;
			}
			else{
				mutant.moveQueen(i, b.getQueenPosition(i));
			}
		}
		//update the costs, if it's mutated only
		if (isMutated)
			mutant.getSteepestHillHCost();
		
		return mutant;
	}
	
	/**
	 * Method to print the solution for testing purpose to ensure it actually found it.
	 */
	public void printSolution(){
		if (hasSolution)
			System.out.println(chromosomes.get(size).toString());
		else
			System.out.println("No Solution Found.");
	}

	public boolean hasSolution() {
		// TODO Auto-generated method stub
		return hasSolution;
	}
	/**
	 * This will sore the population by the fitness scores of the members.
	 */
	public void sortByFitness(){
		Collections.sort(chromosomes, new Comparator<Board>(){

			@Override
			public int compare(Board a, Board b) {
				
				if (a.getFitness()>b.getFitness())
					return 1;
				if (a.getFitness()<b.getFitness())
					return -1;
				else
					return 0;
			}
			
		});
	}

}
