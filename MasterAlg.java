import java.util.*;
import java.io.*;
/*STUFF LEFT TO DO
1. DISPLAY DATA
	user output options:
	list avg numind fitnesses (for a given alg) over generations
	list fitnesses of algs over generations
	show instance variables of algs
	show status of masteralg
2. CONTROL GROUP
	really big alg with arbitrary instance var values (we want data output for this 
	alongside that of the masteralg for comparison)
3. README 
	most of it will come from comments in code
4. RENAME RETARDED STUFF
	
*/
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
	
	static final int ALG_POP_SIZE = 30;
 	
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
	
	private double numIndAvgFitness;
	
	private double[] numIndAvgFitnessData;
	private double[] controlNumIndFitnessData;
	
	private long[][] algFitnessData;
	private double[] algAvgFitnessData;
	/****/
	private int[] selectionData;
	private int[] elitismData;
	private int[] crossoverData;
	private double[] algMutateRateData;
	private double[] algGeneMutateRateData;
	private double[] algGeneBitMutateRateData; 

	/**/
	private int[] selectionDataIndices;
	private int[] elitismDataIndices;
	private int[] crossoverDataIndices;
	private int[] algMutateRateDataIndices;
	private int[] algGeneMutateRateDataIndices;
	private int[] algGeneBitMutateRateDataIndices;
	/****/

	
	int[][] algSelectionData;
	int[][] algElitismData;
	int[][] algCrossoverData;
	double[][] numIndMutateRateData;
	double[][] numIndGeneMutateRateData;
	double[][] numIndGeneBitMutateRateData;
	
	

	private int generations;
	
 
	
	public MasterAlg() {
		
		algsSorted = new int[ALG_POP_SIZE];
		algPop = new Alg[ALG_POP_SIZE];
		template = new Alg();
		for(int i = 0; i < ALG_POP_SIZE; i++){
			algPop[i] = new Alg(template);
			algsSorted[i] = i;
		}
	}
	
	public MasterAlg(int genNumber) {           //parameter for user imput
		
		algsSorted = new int[ALG_POP_SIZE];
		algPop = new Alg[ALG_POP_SIZE];
		template = new Alg();
		for(int i = 0; i < ALG_POP_SIZE; i++){
			algPop[i] = new Alg(template);
			algsSorted[i] = i;
		}
		generations = genNumber;
		
		numIndAvgFitnessData = new double[generations + 1];
		algFitnessData = new long[generations + 1][ALG_POP_SIZE];
		algAvgFitnessData = new double[generations + 1];
		selectionData = new int[generations + 1];
		elitismData = new int[generations + 1];
		crossoverData = new int[generations + 1];
		algMutateRateData = new double[generations + 1];
		algGeneMutateRateData = new double[generations + 1];
		algGeneBitMutateRateData = new double[generations + 1];
		numIndAvgFitnessData = new double[generations + 1];
		controlNumIndFitnessData = new double[generations + 1];
		setData(0);

		selectionDataIndices = new int[generations+1];
		elitismDataIndices = new int[generations+1];
		crossoverDataIndices = new int[generations+1];
		algMutateRateDataIndices = new int[generations+1];
		algGeneMutateRateDataIndices = new int[generations+1];
		algGeneBitMutateRateDataIndices = new int[generations+1];
		for(int j = 0; j < generations+1; j++){
			selectionDataIndices[j] = j;
			elitismDataIndices[j] = j;
			crossoverDataIndices[j] = j;
			algMutateRateDataIndices[j] = j;
			algGeneMutateRateDataIndices[j] = j;
			algGeneBitMutateRateDataIndices[j] = j;
		}


		setNumIndAvgFitnessData(0);
		
		algSelectionData = new int[generations + 1][ALG_POP_SIZE];
		algElitismData = new int[generations + 1][ALG_POP_SIZE];
		algCrossoverData = new int[generations + 1][ALG_POP_SIZE];
		numIndMutateRateData = new double[generations + 1][ALG_POP_SIZE];
		numIndGeneMutateRateData = new double[generations + 1][ALG_POP_SIZE];
		numIndGeneBitMutateRateData = new double[generations + 1][ALG_POP_SIZE];
		setAlgData(0);

	}
	
	public String toString() {
		
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
			s += "\n\tSelection: " + algPop[i].getSelection() + "\n";
			s += "\tElitism: " + algPop[i].getElitism() + "\n";
			s += "\tCrossover: " + algPop[i].getCrossover() + "\n";
			s += "\tNumInd mutate rate:" + algPop[i].getNumIndMutateRate() + "\n";
			s += "\tGene mutate rate:" + algPop[i].getNumIndGeneMutateRate() + "\n";
			s += "\tBit mutate rate:" + algPop[i].getNumIndGeneBitMutateRate() + "\n";
		}
	 
		s += "\nTotal Alg Fitness = " + algTotalFitness + "\n";
		s += "Average Alg Fitness = " + algAvgFitness;
		return s;
	}
	
	/**ACCESSORS AND MUTATORS**********************************************/
	
	//assumes setAlgFitness() and algSort(1) have been called IN THAT ORDER
	public void setInstanceVars() {
		
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
			if (algFit < 0)
				algFit = 0;
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
	
	public void setAlgFitnessData(int genNumber) {
	
		for (int i = 0; i < ALG_POP_SIZE; i++) {
			algFitnessData[genNumber][i] = algPop[i].getAlgFitness();
		}
	}
	
	public void setData(int genNumber) {
		algAvgFitnessData[genNumber] = algAvgFitness;
		selectionData[genNumber] = selection;
		elitismData[genNumber] = elitism;
		crossoverData[genNumber] = crossover;
		algMutateRateData[genNumber] = algMutateRate;
		algGeneMutateRateData[genNumber] = algGeneMutateRate;
		algGeneBitMutateRateData[genNumber] = algGeneBitMutateRate;
	}
	
	public void setAlgData(int genNumber) {
		
		for (int i = 0; i < ALG_POP_SIZE; i++) {
			algSelectionData[genNumber][i] = algPop[i].getSelection();
			algElitismData[genNumber][i] = algPop[i].getElitism();
			algCrossoverData[genNumber][i] = algPop[i].getCrossover();
			numIndMutateRateData[genNumber][i] = algPop[i].getNumIndMutateRate();
			numIndGeneMutateRateData[genNumber][i] = algPop[i].getNumIndGeneMutateRate();
			numIndGeneBitMutateRateData[genNumber][i] = algPop[i].getNumIndGeneBitMutateRate();
		}
	}
	
	public void setNumIndAvgFitness() {
		
		double x = 0;
		for (int i = 0; i < ALG_POP_SIZE; i++) {
			x += algPop[i].getNumIndAvgFitness();
		}
		numIndAvgFitness = x / ALG_POP_SIZE;
	}
	
	public void setNumIndAvgFitnessData(int genNumber) {
		
		numIndAvgFitnessData[genNumber] = numIndAvgFitness;
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
			swap(i, best, algsSorted);
		}
			//System.out.println(algsSorted);
	}

	public void swap(int a, int b, int[] array) {
		
		//System.out.println(algsSorted.length);
		int tem = array[a];
		array[a] = array[b];
		array[b] = tem;
	}
	
	/**********************************************************************/
	
	/**MATING**************************************************************/
	
	public void runAlgMatingSeason(Alg control) {
		
		controlNumIndFitnessData[0] = control.getNumIndAvgFitness();
		
		for(int i = 0; i < generations; i++) {
			algMatingSeason(i + 1);
			for (int j = 0; j < 100; j++) {
				control.numIndMatingSeason(j + 1);
			}
			controlNumIndFitnessData[i + 1] = control.getNumIndAvgFitness();
		}
		
		setAlgTotAvgFitness();
		
		setAlgFitnessData(generations);
	}
	
	public void algMatingSeason(int genNumber) {
		
		setAlgTotAvgFitness();
		
		setAlgFitnessData(genNumber - 1);
		
		if(elitism > 0){
			//put the best elitism Algs at the front of algsSorted
			algSort(elitism);
		}
		else{
			algSort(1);//puts index of best alg at position 0 in algsSorted
		}
		//System.out.println("best" + algPop[algsSorted[0]]);
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
		
		setData(genNumber);
		setNumIndAvgFitnessData(genNumber);
		
		setAlgData(genNumber);
		
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
		//System.out.println("roulette");
		long subPopTot = setSubPopTotalFitness(pop);
		long ball = (long) (Math.random() * subPopTot);
		System.out.println("ball" + ball);
		long sum = 0;
		for(int i = 0; i < pop.length; i++){

			sum += algPop[pop[i]].getAlgFitness();
			//System.out.println("sum" + sum);
			if(sum >= ball) {
				//System.out.println(pop.length);
				//System.out.println("sum " + sum + " ball " + ball);

				return i;
			}
		}
		System.out.println("here");
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
	
	/**PRINT DATA**********************************************************/
	
	public void print() {
		
		
	}
	
	//compares control and masteralg average numind fitnesses over every generation
	public void printCompareNumIndAvgFitness() {
		
		System.out.println("Comparing the average NumInd fitnesses");
		for (int i = 0; i < generations + 1; i++) {
			if (i == 0) {
				System.out.println("Original population");
			}
			else {
				int x = i + 1;
				System.out.println("Generation " + x);
			}
			System.out.println("\tMasterAlg: " + numIndAvgFitnessData[i]);
			System.out.println("\tControl: " + controlNumIndFitnessData[i]);
		}
	}
	
	//lists variables with fitness for each generation
	public void printCompareVariablesFitness() {
		
		System.out.println("Comparing the variables of each Alg with its fitness");
		for (int i = 0; i < generations + 1; i++) {
			if (i == 0) {
				System.out.println("Original population");
			}
			else {
				int x = i + 1;
				System.out.println("Generation " + x);
			}
			for (int j = 0; j < ALG_POP_SIZE; j++) {
				System.out.println("Alg number: " + j);
				System.out.println("\tFitness: " + algFitnessData[i][j]);
				System.out.println("\tSelection: " + algSelectionData[i][j]);
				System.out.println("\tElitism: " + algElitismData[i][j]);
				System.out.println("\tCrossover: " + algCrossoverData[i][j]);
				System.out.println("\tNumIndMutateRate: " + numIndMutateRateData[i][j]);
				System.out.println("\tNumIndGeneMutateRate: " + numIndGeneMutateRateData[i][j]);
				System.out.println("\tNumIndGeneBitMutateRate: " + numIndGeneBitMutateRateData[i][j]);
			}
		}
	}
			
	
	/*public void printAlgFitnessData() {
		
		for (int i = 0; i < algFitnessData.length; i++) {
			if (i == 0) {
				System.out.println("Original population of Algs");
			}
			else {
				int x = i + 1;
				System.out.println("Generation " + x);
			}
			for (int j = 0; j < numIndAvgFitnessData.length; j++) {
				int y = j + 1;
				System.out.println("NumInd " + y);
				System.out.println("\tFitness: " + numIndAvgFitnessData[i][j]);
			}
		}
	}
	
	public void printAlgAvgFitnessData() {
		
		for (int i = 0; i < algAvgFitnessData.length; i++) {
			if (i == 0) {
				System.out.println("Original population of Algs");
				System.out.println("\tAlg average fitness: " + algAvgFitnessData[i]);
			}
			else {
				int x = i + 1;
				System.out.println("Generation " + x);
				System.out.println("\tAlg average fitness: " + algAvgFitnessData[i]);
			}
		}
	}
	*/
	
		
	
	/**********************************************************************/
	

	/**USER DATA STUFF*****************************************************/
	
	public static boolean isParsableToInt(String i){
		try{
			Integer.parseInt(i);
			return true;
		}
		catch(NumberFormatException nfe){
			return false;
		}
	}
	
	public static boolean isParsableToDouble(String i){
		try{
			Double.parseDouble(i);
			return true;
		}
		catch(NumberFormatException nfe){
			return false;
		}
	}
	
	/**********************************************************************/

	/****TEMPORARY PRINT DATA***********************/
	public void sortVariables(){
		sortSelection();
		sortElitism();
		sortCrossover();
		sortAlgMutateRate();
		sortAlgGeneMutateRate();
		sortAlgGeneBitMutateRate();
	}
	
	public void sortSelection(){
		for(int i = 0; i < selectionDataIndices.length; i++){
			int best = i;
			for(int j = i; j < selectionDataIndices.length; j++){
				if(selectionData[selectionDataIndices[j]] > selectionData[selectionDataIndices[best]]){
					best = j;
				}
			}
			swap(i, best, selectionDataIndices);
		}
	}
	
	public void sortElitism(){
		for(int i = 0; i < elitismDataIndices.length; i++){
			int best = i;
			for(int j = i; j < elitismDataIndices.length; j++){
				if(elitismData[elitismDataIndices[j]] > elitismData[selectionDataIndices[best]]){
					best = j;
				}
			}
			swap(i, best, elitismDataIndices);
		}
	}
	
	public void sortCrossover(){
		for(int i = 0; i < crossoverDataIndices.length; i++){
			int best = i;
			for(int j = i; j < crossoverDataIndices.length; j++){
				if(crossoverData[crossoverDataIndices[j]] > crossoverData[crossoverDataIndices[best]]){
					best = j;
				}
			}
			swap(i, best, crossoverDataIndices);
		}
	}
	
	public void sortAlgMutateRate(){
		for(int i = 0; i < algMutateRateDataIndices.length; i++){
			int best = i;
			for(int j = i; j < algMutateRateDataIndices.length; j++){
				if(algMutateRateData[algMutateRateDataIndices[j]] > algMutateRateData[algMutateRateDataIndices[best]]){
					best = j;
				}
			}
			swap(i, best, algMutateRateDataIndices);
		}
	}
	
	public void sortAlgGeneMutateRate(){
		for(int i = 0; i < algGeneMutateRateDataIndices.length; i++){
			int best = i;
			for(int j = i; j < algGeneMutateRateDataIndices.length; j++){
				if(algGeneMutateRateData[algGeneMutateRateDataIndices[j]] > algGeneMutateRateData[algGeneMutateRateDataIndices[best]]){
					best = j;
				}
			}
			swap(i, best, algGeneMutateRateDataIndices);
		}
	}
	
	public void sortAlgGeneBitMutateRate(){
		for(int i = 0; i < algGeneBitMutateRateDataIndices.length; i++){
			int best = i;
			for(int j = i; j < algGeneBitMutateRateDataIndices.length; j++){
				if(algGeneBitMutateRateData[algGeneBitMutateRateDataIndices[j]] > algGeneBitMutateRateData[algGeneBitMutateRateDataIndices[best]]){
					best = j;
				}
			}
			swap(i, best, algGeneBitMutateRateDataIndices);
		}
	}
	
	/********************************************/
	public static void main(String[] args){

		System.out.println(isParsableToDouble(".3"));
		//System.out.println(Integer.parseInt("f"));
		System.out.println("\n\n\n\n\nWelcome to the Master Genetic Algorithm.");
		System.out.println("Here, you can optimize a population of number-individuals using Master Alg or Control Alg.");
		System.out.println("They have the same population size. Master Alg will run automatically, but");
		System.out.println("Control Alg needs user-predetermined values for its variables.");
		System.out.println("\n\t*Note: after you enter this information, you will have the option\n\tto display various data about the processes.\n");
		
		Scanner sc = new Scanner(System.in);

		
		/**CONTROL ALG INPUT*/
		System.out.println("Enter the Alg's variables: selection, elitism, crossover,\nindividual mutation rate, gene mutation rate, bit mutation rate.");
		
		System.out.println("Selection: 2 to " + Alg.NUMIND_POP_SIZE);
		String sels = sc.next();
		while(!isParsableToInt(sels) ||(Integer.parseInt(sels) < 2 || Integer.parseInt(sels) > Alg.NUMIND_POP_SIZE)){
			System.out.println("Integer between 2 and " + Alg.NUMIND_POP_SIZE + ", inclusive, please.");
			sels = sc.next();
		}
		int sel = Integer.parseInt(sels);
		
		System.out.println("Elitism: 0 to " + Alg.NUMIND_POP_SIZE);
		String els = sc.next();
		while(!isParsableToInt(els) || (Integer.parseInt(els) < 0 || Integer.parseInt(els) > Alg.NUMIND_POP_SIZE)){
			System.out.println("Integer between 0 and " + Alg.NUMIND_POP_SIZE + ", inclusive, please.");
			els = sc.next();
		}
		int el = Integer.parseInt(els);
		
		System.out.println("Crossover: 0 to " + NumInd.NUMIND_CHROMOSOME_SIZE);
		String crs = sc.next();
		while(!isParsableToInt(crs) || (Integer.parseInt(crs) < 0 || Integer.parseInt(crs) > NumInd.NUMIND_CHROMOSOME_SIZE)){
			System.out.println("Decimal between 0 and " + NumInd.NUMIND_CHROMOSOME_SIZE + ", inclusive, please.");
			crs = sc.next();
		}
		int cr = Integer.parseInt(crs);
		
		System.out.println("Individual mutation rate: 0 to 1, inclusive, please.");
		String r1s = sc.next();
		while(!isParsableToDouble(r1s) || (Double.parseDouble(r1s) < 0 || Double.parseDouble(r1s) > 1)){
			System.out.println("Decimal between 0 and 1, inclusive, please.");
			r1s = sc.next();
		}
		double r1 = Double.parseDouble(r1s);
		
		System.out.println("Gene mutation rate: 0 to 1.");
		String r2s = sc.next();
		while(!isParsableToDouble(r2s) || (Double.parseDouble(r2s) < 0 || Double.parseDouble(r2s) > 1)){
			System.out.println("Decimal between 0 and 1, inclusive, please.");
			r2s = sc.next();
		}
		double r2 = Double.parseDouble(r2s);
		
		System.out.println("Bit mutation rate: 0 to 1.");
		String r3s = sc.next();
		while(!isParsableToDouble(r3s) || (Double.parseDouble(r3s) < 0 || Double.parseDouble(r3s) > 1)){
			System.out.println("Decimal between 0 and 1, inclusive, please.");
			r3s = sc.next();
		}
		double r3 = Double.parseDouble(r3s);
		

		//population = total number of NumInds in m
		Alg control = new Alg(ALG_POP_SIZE * Alg.NUMIND_POP_SIZE, sel, el, cr, r1, r2, r3);
		/**********************/
		
		System.out.println("Through how many generations should the genetic algorithms run?");
		String gens = sc.next();
		while(!isParsableToInt(gens) || Integer.parseInt(gens) < 0){
			gens = sc.next();
		}
		
		int gen = Integer.parseInt(gens);
		MasterAlg m = new MasterAlg(gen);
		
		System.out.println("Which data do you want to see?");
		System.out.println("");
		m.runAlgMatingSeason(control);//runs a matingseason on control and master, each of which
				       //sets all data
		
		m.printCompareNumIndAvgFitness();
		m.printCompareVariablesFitness();
				       
		//System.out.println(m);
		//System.out.println("\n\n");
		//m.algMatingSeason();
		//System.out.println(m);
	}

}