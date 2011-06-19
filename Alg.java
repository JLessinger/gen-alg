import java.util.*;

public class Alg {


	/*Alg as a process*/
	private final int GENERATIONS_PER_TRIAL = 100;

	private final int NUMIND_POP_SIZE = 30;//although it is a valid variable, pop size 
						//cannot vary between algs within
						//a masteralg because the algs cannot be adequately
						//compared without identical populations
	private NumInd[] numIndPop;
	private long numIndTotalFitness;
	private double numIndAvgFitness;
	private int[] numIndsSorted; //indices of NumInds in pop in order of fitness
				//numIndsSorted[0] = best
				
	//according to the class
		//to avoid confusion

	private double changeInAvgNumIndFitness;
	private long changeInTotNumIndFitness;
	/*********/
	
	
	/**Alg as an individual*/
	private long algFitness; 
	
	private final int ALG_TRUE_RATE = 4;
	
	public static final int ALG_CHROMOSOME_SIZE = 6;
	/********/
	
	
	
	/*variables to be optimized*/
	
	//chromosome holds genes which set these instance variables
	Gene[] algChromosome;
	/*gene listing
	* 0 = selection
	* 1 = elitism
	* 2 = crossover
	* 3 = numIndMutateRate
	* 4 = numIndGeneMutateRate
	* 5 = numIndGeneBitMutateRate
	*/ 
	//all have natural limits, but will be allowed to vary freely within them 
	//just like NumInd genes have a natural limit according to their size
	
	private int selection;
	private final int SELECTION_GENE_SIZE = (int) Math.ceil(Math.log(NUMIND_POP_SIZE+1)/Math.log(2));
	//numIndRoulette selection on a subpopulation of what size?
		       //smaller populations are like randomized tournament selection
		       // 2²selection²NUMIND_POP_SIZE
		       //if select = 2, numIndRoulette selection on 2 randomly selected
		       //if selected = NUMIND_POP_SIZE, normal numIndRoulette

	private int elitism;
	private final int ELITISM_GENE_SIZE = (int) Math.ceil(Math.log(NUMIND_POP_SIZE+1)/Math.log(2));
	//how many of the best are reserved spots?
	//LIMITS: 0²elitism²NUMIND_POP_SIZE

	
	private int crossover; 
	private final int CROSSOVER_GENE_SIZE = (int) Math.ceil(Math.log(NumInd.NUMIND_CHROMOSOME_SIZE+1)/Math.log(2));
	//number of genes to take from chosen parent before filling in chromosome with 
			//genes from other parent
			//LIMITS: 0 <= crossover <= NUMIND_CHROMOSOME_SIZE
	
			
	//mutation rate stuff
	//each of these genes is 10 bits. value will be divided by max (1023)
	//to achieve a double between 0 and 1
	private double numIndMutateRate; //LIMITS: 0-1
	private final int NUMIND_MUTATE_RATE_GENE_SIZE = 10;
	
	private double numIndGeneMutateRate; //LIMITS: 0-1
	private final int NUMIND_GENE_MUTATE_RATE_GENE_SIZE = 10;
	
	private double numIndGeneBitMutateRate; //LIMITS: 0-1
	private final int NUMIND_GENE_BIT_MUTATE_RATE_GENE_SIZE = 10;

	/*end of variables to be optimized. think of more? these cover a lot
	of steps in the process. add any missing, very important ones*/

	public Alg() {
		
		setVariables();
		numIndsSorted = new int[NUMIND_POP_SIZE];
		numIndPop = new NumInd[NUMIND_POP_SIZE];
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			numIndPop[i] = new NumInd();
			numIndsSorted[i] = i;
		}
	}

	/**
	Takes the population of NumInds from the parameter and makes it its own.
	copies the population
	same parameter for every Alg in a population in MasterAlg
	**/
	public Alg(Alg a) {

		setVariables();
		numIndsSorted = new int[NUMIND_POP_SIZE];
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			numIndsSorted[i] = i;
		}
		numIndPop = a.copyNumIndPop();
	}
	
	public String toString() {

		String s = "\nAlg:\n";
		s += "Selection:" + selection + "\nElitism:" + elitism + "\nCrossover:" + crossover;
		s += "\nnumIndMutateRate:" + numIndMutateRate + "\nnumIndGeneMutateRate:" + numIndGeneMutateRate;
		s += "\nnumIndGeneBitMutateRate:" + numIndGeneBitMutateRate //+ "\nnumber of bit mutations expected"
		//+ NUMIND_POP_SIZE * NumInd.NUMIND_CHROMOSOME_SIZE * NumInd.NUMIND_GENE_SIZE 
		//* numIndMutateRate * numIndGeneMutateRate * numIndGeneBitMutateRate  
		+ "\n\n";
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			NumInd n = numIndPop[i];
			s += "NumInd " + (i) + ": fitness = " + n.getNumIndFitness() + "\n";
		}
		setNumIndTotAvgFitness();
		s += "\nTotal NumInd Fitness = " + numIndTotalFitness + "\n";
		s += "Average NumInd Fitness = " + numIndAvgFitness;
		return s;
	}
	
	
	
	/**ACCESSORS AND MUTATORS**********************************************/

	public void setVariables(){
		/**Set Genes in Alg's chromosome*/
		algChromosome = new Gene[ALG_CHROMOSOME_SIZE];
		algChromosome[0] = new Gene(SELECTION_GENE_SIZE, ALG_TRUE_RATE);
		algChromosome[1] = new Gene(ELITISM_GENE_SIZE, ALG_TRUE_RATE);
		algChromosome[2] = new Gene(CROSSOVER_GENE_SIZE, ALG_TRUE_RATE);
		algChromosome[3] = new Gene(NUMIND_MUTATE_RATE_GENE_SIZE, ALG_TRUE_RATE);
		algChromosome[4] = new Gene(NUMIND_GENE_MUTATE_RATE_GENE_SIZE, ALG_TRUE_RATE);
		algChromosome[5] = new Gene(NUMIND_GENE_BIT_MUTATE_RATE_GENE_SIZE, ALG_TRUE_RATE);
		
		

		for(Gene g: algChromosome){
			g.setValue();
		}

		/**set instance variables to gene values. Scale them so they are not outside natural limits*/

		selection = (int) ((double) algChromosome[0].getValue() / algChromosome[0].getMaxValue() * (NUMIND_POP_SIZE-2)) + 2;

		elitism = (int) ((double) algChromosome[1].getValue() / algChromosome[1].getMaxValue() * NUMIND_POP_SIZE);
		
		crossover = (int) ((double) algChromosome[2].getValue() / algChromosome[2].getMaxValue() * NumInd.NUMIND_CHROMOSOME_SIZE);

		numIndMutateRate = (double) algChromosome[3].getValue() / 1023;

		numIndGeneMutateRate = (double) algChromosome[4].getValue() / 1023;

		numIndGeneBitMutateRate = (double) algChromosome[5].getValue() / 1023;

	}

	public NumInd getNumInd(int index) {

		return numIndPop[index];
	}

	public int[] getNumIndsSorted() {
		
		return numIndsSorted;
	}
	
	public NumInd[] copyNumIndPop() {

		NumInd[] copy = new NumInd[NUMIND_POP_SIZE];
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			copy[i] = new NumInd(numIndPop[i]);
		}
		return copy;
	}

	public long getAlgFitness() {
		//setAlgFitness();//that calls numIndMatingSeason?
		return algFitness;//
	}
	
	public void setAlgFitness() {
		algFitness = 0;
		for(int i = 0; i < GENERATIONS_PER_TRIAL; i++) {
			numIndMatingSeason();
			//System.out.print("chang" + changeInAvgNumIndFitness + " ");
			algFitness += changeInAvgNumIndFitness;//set algFitness instance var
		}
	}				
	
	public void setNumIndTotAvgFitness() {

		numIndTotalFitness = 0;
		for(int i = 0; i < numIndPop.length; i++) {
			numIndTotalFitness += numIndPop[i].getNumIndFitness();
		}
		
		numIndAvgFitness = 0;
		numIndAvgFitness =  (double)numIndTotalFitness / NUMIND_POP_SIZE;
	}
	
	public void setChangeInTotAvgNumIndFitness(NumInd[] oldPop) {
		
		long oldFit = 0;
		for(int i = 0; i < oldPop.length; i++) {
			oldFit += oldPop[i].getNumIndFitness();
		}
		
		long newFit = 0;
		for(int i = 0; i < numIndPop.length; i++) {
			newFit += this.numIndPop[i].getNumIndFitness();
		}
		
		changeInTotNumIndFitness = newFit - oldFit;
		
		double totalChange = newFit - oldFit;
		
		changeInAvgNumIndFitness =  totalChange / ((double) NUMIND_POP_SIZE);
	}
	
	public long setSubPopTotalFitness(int[] subPop) {
		
		long end = 0;
		for (int i = 0; i < subPop.length; i++) {
			end += numIndPop[subPop[i]].getNumIndFitness();
		}
		return end;
	}
	
	public int getSelection() {
		
		return selection;
	}
	
	public int getElitism() {
		
		return elitism;
	}
	
	public int getCrossover() {
		
		return crossover;
	}
	
	public double getNumIndMutateRate() {
		
		return numIndMutateRate;
	}
	
	public double getNumIndGeneMutateRate() {
		
		return numIndGeneMutateRate;
	}
		
	public double getNumIndGeneBitMutateRate() {
		
		return numIndGeneBitMutateRate;
	}
	
	/**********************************************************************/

	/**SORTING THE POPULATION**********************************************/
	
	//numIndSorts the top n NumInds in population according to fitness value
	public void numIndSort(int n) {
	
		for(int i = 0; i < n; i++) {
			int best = i;
			for(int j = i; j < numIndsSorted.length; j++) {
				if(numIndPop[numIndsSorted[j]].getNumIndFitness() > numIndPop[numIndsSorted[best]].getNumIndFitness()) {
					best = j;
				}
			}
			swap(i, best);
		}
			//System.out.println(numIndsSorted);
	}

	public void swap(int a, int b) {

		int tem = numIndsSorted[a];
		numIndsSorted[a] = numIndsSorted[b];
		numIndsSorted[b] = tem;
	}
	
	/**********************************************************************/

	/**MATING**************************************************************/
	
	public void numIndMatingSeason() {

		setNumIndTotAvgFitness();
		double oldAvg = numIndAvgFitness;
		long oldTot = numIndTotalFitness;
		NumInd[] oldPop = numIndPop;
		NumInd[] temPop = new NumInd[NUMIND_POP_SIZE];

		//put the best elitism numinds at the front of numIndsSorted
		numIndSort(elitism);

		//fill tempop with the numIndsSorted numinds
		for(int k = 0; k < elitism; k++){
			temPop[k] = numIndPop[numIndsSorted[k]];
		}

		//fill remaining population with offspring
		for(int m = elitism; m < NUMIND_POP_SIZE; m++) {
			int mother = numIndSelectParent(-1);
			int father = numIndSelectParent(mother); //select a father who is not the mother 
							
			temPop[m] = numIndMate(mother, father);
		}
		numIndPop = temPop;

		algMutateNumInds();
	
		setNumIndTotAvgFitness();
		setChangeInTotAvgNumIndFitness(oldPop);
		/*printing out data
		setNumIndTotAvgFitness();
		setChangeInTotAvgNumIndFitness(oldPop);
		System.out.println("matingseason");
		System.out.println("oldpop average fitness: " + oldAvg);
		System.out.println("newpop average fitness: " + numIndAvgFitness);
		System.out.println("change in average fitness: " + changeInAvgNumIndFitness);
		System.out.println("oldpop total fitness: " + oldTot);
		System.out.println("newpop total fitness: " + numIndTotalFitness);
		System.out.println("change in total fitness: " + changeInTotNumIndFitness);
		*/
		
	}

	public NumInd numIndMate(int motInd, int fatInd) {
		
		NumInd mother = numIndPop[motInd];
		NumInd father = numIndPop[fatInd];
		NumInd child = new NumInd();
		int i = 0;
		for(; i < crossover; i++){
			child.setNumIndGene(i, mother);
		}
		for(; i < NumInd.NUMIND_CHROMOSOME_SIZE; i++){
			child.setNumIndGene(i, father);
		}
		return child;
	}

	//selects a parent via hybrid numIndRoulette/tournament selection such that
	//no mating selects a parent twice	
	public int numIndSelectParent(int mother) {

		//holds indices referring to numIndPop indices of NumInds
		//to be considered for mating
		int[] subPop = new int[selection];

		//holds indices of all potential parents not the parameter
		ArrayList<Integer> selectFrom = new ArrayList<Integer>();
		for(int k = 0; k < NUMIND_POP_SIZE; k++) {
			if(k!=mother) {
				selectFrom.add(k);
			}
		}
		//System.out.println("selectfrom" + selectFrom);
		//if selection = NUMIND_POP_SIZE, subpop = pop and it is 
		//simple numIndRoulette selection. Otherwise, weighted random tournament

		for(int i = 0; i < selection; i++) {
			if(selectFrom.size()>0){
				subPop[i] = selectFrom.remove((int)(selectFrom.size() * Math.random()));
			}
		}
		//pre-condition: subPop does not contain any parents already selected in this
		//mating, and does not contain the same NumInd twice
		return numIndRoulette(subPop);
	}
	
	//numIndRoulette selection with array of numIndPop indices
	public int numIndRoulette(int[] pop) {
		
		long ball = (long) (Math.random() * setSubPopTotalFitness(pop));
		long sum = 0;
		for(int i = 0; i < pop.length; i++){
			sum += numIndPop[pop[i]].getNumIndFitness();
			if(sum > ball) {
				return i;
			}
		}
		return -1;
	}
	
	/**********************************************************************/
	
	/**MUTATING************************************************************/
	
	//Calls numIndMutate(). Doesn't mutate the Alg
	public void algMutateNumInds() {
		
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			if(Math.random() < numIndMutateRate){
				numIndPop[i].numIndMutate(numIndGeneMutateRate, numIndGeneBitMutateRate);
			}
		}
	}
	
	/**********************************************************************/
	
	/**ALG AS AN INDIVIDUAL************************************************/
	
	public void setAlgGene(int index, Alg parent) {
		
		algChromosome[index] = new Gene(parent.algChromosome[index]);
	}
	
	public void algMutateSelf(double algGeneMutateRate, double algGeneBitMutateRate) {
		for(int i = 0; i < ALG_CHROMOSOME_SIZE; i++){
			if(Math.random() < algGeneMutateRate){
				algChromosome[i].geneMutate(algGeneBitMutateRate);
			}					
		}
	}
	
	/**********************************************************************/
	
                                     


	public static void main(String[] args) {                         

		/*	
		Alg a = new Alg();
		System.out.println(a);
		a.setNumIndTotAvgFitness();
		System.out.println(a.numIndAvgFitness + " " + NumInd.NUMIND_MAX_FITNESS);
		System.out.println((double)NumInd.NUMIND_MAX_FITNESS / a.numIndAvgFitness);
		System.out.println(a.numIndPop[0]); 
		*/


		/*
		int[] momAndDads = new int[3];
		momAndDads[0] = 0;
		momAndDads[1] = 1;
		momAndDads[2] = 2;
		NumInd kid = a.numIndMate(momAndDads);
		System.out.println(a.getNumInd(0));
		System.out.println(a.getNumInd(1));
		System.out.println(a.getNumInd(2));
		System.out.println(kid);
		
		a.numIndSort(3);
		for(int i = 0; i < a.numIndsSorted.length; i++){
			System.out.println(a.numIndPop[a.numIndsSorted[i]].getNumIndFitness());
		}
		*/

		/*
		Alg a = new Alg();
		System.out.println("Alg: \n" + a + "\n");

		System.out.println(a.numIndTotalFitness);

		System.out.println(a.numIndAvgFitness);

		System.out.println(a.numIndsSorted);
		*/
		
		/*
		Alg a = new Alg();
		System.out.println(a); 

		System.out.println("one parent: " + a.numIndPop[4]);
		
		a.selection = 14;//subpop = 14
		int x = a.numIndSelectParent(4);
		a.crossover = 8;
		System.out.println("second one: " + x);
		System.out.println("parent2: " + a.numIndPop[x]);
		
		System.out.println(a.numIndMate(4,x));
		*/
		
		/*
		Alg a = new Alg();
		System.out.println("old population\n\n");
		System.out.println(a);
		
		a.setNumIndTotAvgFitness();
		//System.out.println("main");
		//System.out.println("Average Fitness: " + a.numIndAvgFitness);
		a.numIndMatingSeason();
		System.out.println("new population\n\n");
		System.out.println(a);
		*/
		
		Alg a = new Alg();
		System.out.println(a);
		a.setAlgFitness();//does 100 mating seasons, changing alg's population
		System.out.println(a);
		System.out.println(a.algFitness);
	}

}