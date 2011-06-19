import java.util.*;
import java.io.*;

/**
MasterAlg indirectly optimizes NumInds by optimizing Algs which are optimizing NumInds.
Alg fitness is based on how well the Alg optimizes NumInds in 100 generations,
so overall MasterAlg performance should increase.

MasterAlg has the same variables as Alg - elitism, selection, crossover, and mutation rates.
Each generation, MasterAlg sets these according to the best-performing Alg in that generation.
This assumes that the same values for the given variables are suitable for optimizing both
Algs and NumInds, which is not necessarily true. However, notice that the population sizes within
MasterAlg and Alg are the same, so this assumption is plausible.
*/

class MasterAlg{
	
	final int ALG_POP_SIZE = 30;
 	
	private Alg[] algPop;
	private Alg template;//copy it many times into algPop
	private long algTotalFitness;
	private double algAvgFitness;
	private int[] algsSorted; //indices of Algs in pop in order of fitness
				//sorted[0] = best
	
	private int selection;
	private int elitism;
	private int crossover;
	private double algMutateRate;
	private double algGeneMutateRate;
	private double algGeneBitMutateRate;
	
	
	public MasterAlg() {
		
		algsSorted = new int[ALG_POP_SIZE];
		algPop = new Alg[ALG_POP_SIZE];
		template = new Alg();
		for(int i = 0; i < ALG_POP_SIZE; i++){
			algPop[i] = new Alg(template);
			algsSorted[i] = i;
		}
	}
	
	public String toString(){
		String s = "";
		s += "\nselection:" + selection + "\nelitism:" + elitism + "\ncrossover:" + crossover
		+ "\nalgMutateRate:" + algMutateRate + "\nalgGeneMutateRate:" + algGeneMutateRate
		+ "\nalgGeneBitMutateRate:" + algGeneBitMutateRate //+ "\nnumber of bit mutations expected"
		//+ ALG_POP_SIZE * Alg.ALG_CHROMOSOME_SIZE * Alg.ALG_GENE_SIZE 
		//* algMutateRate * algGeneMutateRate * algGeneBitMutateRate  
		+ "\n\n";
		setAlgTotAvgFitness();
		for(int i = 0; i < ALG_POP_SIZE; i++){
			s += "Alg " + i + ": fitness = " + algPop[i].getAlgFitness();
			s += "\n\tNumInd mutate rate:" + algPop[i].getNumIndMutateRate() + "\n";
			s += "\tGene mutate rate:" + algPop[i].getNumIndGeneMutateRate() + "\n";
			s += "\tBit mutate rate:" + algPop[i].getNumIndGeneBitMutateRate() + "\n";
		}
	 
		s += "\nTotal Alg Fitness = " + algTotalFitness + "\n";
		s += "Average Alg Fitness = " + algAvgFitness;
		return s;	
	}
	
	/**ACCESSORS AND MUTATORS**********************************************/
	
	//assumes setAlgFitness() and algSort(1) have been called IN THAT ORDER
	public void setInstanceVars(){
		
		Alg best = algPop[algsSorted[0]];
		selection = best.getSelection();
		selection = (int)((double)selection * ALG_POP_SIZE / Alg.NUMIND_POP_SIZE);
		if(selection < 2)
			selection = 2;
		elitism = best.getElitism();
		elitism = (int)((double)elitism * ALG_POP_SIZE / Alg.NUMIND_POP_SIZE);
		crossover = best.getCrossover();
		crossover = (int)((double)crossover * Alg.ALG_CHROMOSOME_SIZE / NumInd.NUMIND_CHROMOSOME_SIZE);
		algMutateRate = best.getNumIndMutateRate();
		algGeneMutateRate = best.getNumIndGeneMutateRate();
		algGeneBitMutateRate = best.getNumIndGeneBitMutateRate();
	}
	
	public Alg getAlg(int index) {

		return algPop[index];
	}

	public int[] getAlgsSorted() {
		
		return algsSorted;
	}
	
	public void setAlgTotAvgFitness() {
		algTotalFitness = 0;
		for(int i = 0; i < ALG_POP_SIZE; i++) {
			algPop[i].setAlgFitness();//setting the instance var
			algTotalFitness += algPop[i].getAlgFitness();//returning the instance var
		}
		algAvgFitness = 0;
		algAvgFitness = (double) algTotalFitness / ALG_POP_SIZE;
	}
	
	public long setSubPopTotalFitness(int[] subPop) {
		
		long end = 0;
		for (int i = 0; i < subPop.length; i++) {
			long algFit = algPop[subPop[i]].getAlgFitness();
			end += algFit;//Assumes setAlgFitness has already been called 
			//System.out.println(algFit);
		}							/*MAKE SURE THIS HAPPENS IN MATING SEASON*/
		//what
		return end;
	}
	
	public double getAlgMutateRate() {
		
		return algMutateRate;
	}
	
	public double getAlgGeneMutateRate() {
		
		return algGeneMutateRate;
	}
		
	public double getAlgGeneBitMutateRate() {

		return algGeneBitMutateRate;
	}
	
	/**********************************************************************/

	/**SORTING THE POPULATION**********************************************/
	
	//numIndSorts the top n NumInds in population according to fitness value
	public void algSort(int n) {
	
		for(int i = 0; i < n; i++) {
			int best = i;
			for(int j = i; j < algsSorted.length; j++) {
				if(algPop[algsSorted[j]].getAlgFitness() > algPop[algsSorted[best]].getAlgFitness()) {/*CALL SETALGFITNESS*/
					best = j;
				}
			}
			swap(i, best);
		}
			//System.out.println(algsSorted);
	}

	public void swap(int a, int b) {
		
		//System.out.println(algsSorted.length);
		int tem = algsSorted[a];
		algsSorted[a] = algsSorted[b];
		algsSorted[b] = tem;
	}
	
	/**********************************************************************/
	
	/**MATING**************************************************************/
	
	public void algMatingSeason() {
		
		setAlgTotAvgFitness();//calls setAlgFitness 
		
		if(elitism > 0){
			//put the best elitism Algs at the front of algsSorted
			algSort(elitism);
		}
		else{
			algSort(1);//puts index of best alg at position 0 in algsSorted
		}
		setInstanceVars();
		
		Alg[] temPop = new Alg[ALG_POP_SIZE];



		//fill temPop with the algsSorted numinds
		for(int k = 0; k < elitism; k++){
			temPop[k] = algPop[algsSorted[k]];
		}

		//fill remaining population with offspring
		for(int m = elitism; m < ALG_POP_SIZE; m++) {
			int mother = algSelectParent(-1);
			int father = algSelectParent(mother); //select a father who is not the mother 
							
			temPop[m] = algMate(mother, father);
		}
		algPop = temPop;

		masterAlgMutateAlg();
		
		/**printing out data*/
		//data
	}  
	
	//mate Algs
	public Alg algMate(int motInd, int fatInd) {
		
		Alg mother = algPop[motInd];
		Alg father = algPop[fatInd]; 
		Alg child = new Alg(template);
		int i = 0;
		for(; i < crossover; i++){
			child.setAlgGene(i, mother);
		}
		for(; i < Alg.ALG_CHROMOSOME_SIZE; i++){
			child.setAlgGene(i, father);
		}
		return child;
	}
	
	//selects a parent via hybrid algRoulette/tournament selection such that
	//no mating selects a parent twice
	public int algSelectParent(int mother) {

		//holds indices referring to algPop indices of Algs
		//to be considered for mating
		int[] subPop = new int[selection];

		//holds indices of all potential parents not the parameter
		ArrayList<Integer> selectFrom = new ArrayList<Integer>();
		for(int k = 0; k < ALG_POP_SIZE; k++) {
			if(k != mother) {
				selectFrom.add(k);
			}
		}
		//System.out.println("selectfrom" + selectFrom);
		//if selection = NUMIND_POP_SIZE, subpop = pop and it is 
		//simple algRoulette selection. Otherwise, weighted random tournament

		for(int i = 0; i < selection; i++) {
			if(selectFrom.size()>0){
				subPop[i] = selectFrom.remove((int)(selectFrom.size() * Math.random()));
			}
		}
		//pre-condition: subPop does not contain any parents already selected in this
		//mating, and does not contain the same NumInd twice
		return algRoulette(subPop);
	}
	
	//algRoulette selection with array of algPop indices
	public int algRoulette(int[] pop) {
		System.out.println("roulette");
		long subPopTot = setSubPopTotalFitness(pop);
		long ball = (long) (Math.random() * subPopTot);
		long sum = 0;
		for(int i = 0; i < pop.length; i++){
			sum += algPop[pop[i]].getAlgFitness();
			System.out.println("sum" + sum);
			if(sum >= ball) {
				System.out.println(pop.length);
				System.out.println("sum " + sum + " ball " + ball);
				return i;
			}
		}
		return -1;
	}
	
	/**********************************************************************/
	
	/**MUTATING************************************************************/
	
	public void masterAlgMutateAlg() {
		
		for(int i = 0; i < ALG_POP_SIZE; i++){		
			if(Math.random() < algMutateRate){
				algPop[i].algMutateSelf(algGeneMutateRate, algGeneBitMutateRate);
			}
		}
	}
	
	/**********************************************************************/
	
	public static void main(String[] args){
		
		MasterAlg m = new MasterAlg();
		System.out.println(m);
		System.out.println("\n\n");
		m.algMatingSeason();
		System.out.println(m);
	}
}