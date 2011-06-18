import java.util.*;

public class Alg {

	private final int NUMIND_POP_SIZE = 30;
	private final int ALG_TRUE_RATE = 10;
	private long numIndTotalFitness;
	private int[] sorted; //indices of NumInds in pop. in order of fitness
				//sorted[0] = best
	private long algFitness; 	//name generic things  like "fitness"
	private long numIndAvgFitness;	//according to the class
	private NumInd[] numIndPop;	//to avoid confusion


	/*variables to be optimized*/
	
	/*chromosome holds genes which set these instance variables*/
	Gene[] AlgChromosome;
	//all have natural limits, but will be allowed to vary freely within them 
	//just like NumInd genes have a natural limit according to their size
	
	int selection; //roulette selection on a subpopulation of what size?
		       //smaller populations are like randomized tournament selection
		       // 2²selection²NUMIND_POP_SIZE
		       //if select = 2, roulette selection on 2 randomly selected
		       //if selected = NUMIND_POP_SIZE, normal roulette

	int elitism;//how many of the best are reserved spots?
	//LIMITS: 0²elitism²NUMIND_POP_SIZE

	
	int crossover; //number of bits to take from chosen parent before filling in gene with 
			//bits from other parent
			//LIMITS: -NUMIND_GENE_SIZE --> + NUMIND_GENE_SIZE
			//if crossover<=0, it will be considered 0
	

				     
				
	//mutation rate stuff
	float numIndMutateRate; //LIMITS: 0-1
	float numIndchromosomeMutateRate; //LIMITS: 0-1
	float GeneMutateRate; //LIMITS: 0-1
	/*still have to do this stuff*/

	/*end of variables to be optimized. think of more? these cover a lot
	of steps in the process. add any missing, very important ones*/

	public Alg() {

		sorted = new int[NUMIND_POP_SIZE];
		numIndPop = new NumInd[NUMIND_POP_SIZE];
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			numIndPop[i] = new NumInd();
			sorted[i] = i;
		}
		
		
	}

	/**
	Takes the population of NumInds from the parameter and makes it its own.
	copies the population
	same parameter for every Alg in a population in MasterAlg
	**/
	public Alg(Alg a) {

		sorted = new int[NUMIND_POP_SIZE];
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			sorted[i] = i;
		}
		numIndPop = a.copyNumIndPop();
	}

	public String toString() {

		String s = "Alg:\n";
		for(int i = 0; i < numIndPop.length; i++){
			NumInd n = numIndPop[i];
			s += "NumInd " + (i) + ": fitness = " + n.getNumIndFitness() + "\n";
		}
		return s;
	}

	public NumInd getNumInd(int index){
		return numIndPop[index];
	}

	public void setNumIndTotalFitness() {

		numIndTotalFitness = 0;
		for(int i = 0; i < numIndPop.length; i++){
			numIndTotalFitness += numIndPop[i].getNumIndFitness();
		}
	}

	public NumInd[] copyNumIndPop() {

		NumInd[] copy = new NumInd[NUMIND_POP_SIZE];
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			copy[i] = numIndPop[i];
		}
		return copy;
	}

	public void setNumIndAvgFitness() {

		numIndAvgFitness = 0;
		setNumIndTotalFitness();
		numIndAvgFitness = numIndTotalFitness / NUMIND_POP_SIZE;
	}

	//sorts the top n NumInds in population according to fitness value
	public void sort(int n) {

		for(int i = 0; i < n; i++) {
			int best = i;
			for(int j = i; j < sorted.length; j++) {
				if(numIndPop[sorted[j]].getNumIndFitness() > numIndPop[sorted[best]].getNumIndFitness()) {
					best = j;
				}
			}
			swap(i, best);
		}
	}

	public void swap(int a, int b) {

		int tem = sorted[a];
		sorted[a] = sorted[b];
		sorted[b] = tem;
	}

	public void matingSeason() {


		NumInd[] temPop = new NumInd[NUMIND_POP_SIZE];

		//put the best elitism numinds at the front of sorted
		sort(elitism);

		//fill tempop with the sorted numinds
		for(int k = 0; k < elitism; k++){
			temPop[k] = numIndPop[sorted[k]];
		}

		//fill remaining population with offspring
		for(int m = elitism; m < NUMIND_POP_SIZE; m++) {
			int mother = selectParent(-1); //-1 doesnt exist but it has to take an int
			int father = selectParent(mother); //select a father who is not the mother 
							
			temPop[m] = mate(mother, father);//yea?
		}
		/*uncomment this!*/
		//mutate();
	}

	public NumInd mate(int motInd, int fatInd) {
		
		NumInd mother = numIndPop[motInd];
		NumInd father = numIndPop[fatInd];
		NumInd child = new NumInd();
		for(int i = 0; i < child.NUMIND_CHROMOSOME_SIZE; i++){
			if((int) (2*Math.random())==0){
				child.setGene(i, mother, crossover, father);
			}
			else{
				child.setGene(i, father, crossover, mother);
			}
		}
		return child;
	}

	/**
	must not choose same parent twice for a particular mating.
	this is not accounted for now.
	**/
	//selects a parent via hybrid roulette/tournament selection such that
	//no mating selects a parent twice
	//post-condition: does not return a NumInd in alreadyChosen
	public int selectParent(int mother) {

		//holds indices referring to numIndPop indices of NumInds
		//to be considered for mating
		int[] subPop = new int[selection];

		//holds indices of all potential parents not already chosen
		ArrayList<Integer> selectFrom = new ArrayList<Integer>();
		for(int k = 0; k < NUMIND_POP_SIZE; k++) {
			if(k!=mother) {
				selectFrom.add(k);
			}
		}
		//if selection = NUMIND_POP_SIZE, subpop = pop and it is 
		//simple roulette selection. Otherwise, weighted random tournament
		for(int i = 0; i < selection; i++) {
			subPop[i] = selectFrom.remove((int)(selectFrom.size() * Math.random()));
		}
		//pre-condition: subPop does not contain any parents already selected in this
		//mating, and does not contain the same NumInd twice
		return roulette(subPop);
	}

	//roulette selection with array of numIndPop indices
	public int roulette(int[] pop) {

		setNumIndTotalFitness();
		long ball = (long) (Math.random() * numIndTotalFitness); 
		long sum = 0;
		for(int i = 0; i < pop.length; i++){
			sum += numIndPop[pop[i]].getNumIndFitness();
			if(sum > ball) {
				return i;
			}
		}
		return -1;
	}

	//stuff for Alg as an individual	
	/*
	public void setAlgFitness(){
		for(int i 
		*/
	//}
                                     
	

	public static void main(String[] args) {                         

	/*	Alg a = new Alg();
		System.out.println(a);
		a.setNumIndAvgFitness();
		System.out.println(a.numIndAvgFitness + " " + NumInd.NUMIND_MAX_FITNESS);
		System.out.println((double)NumInd.NUMIND_MAX_FITNESS / a.numIndAvgFitness);
		System.out.println(a.numIndPop[0]); */


		/*int[] momAndDads = new int[3];
		momAndDads[0] = 0;
		momAndDads[1] = 1;
		momAndDads[2] = 2;
		NumInd kid = a.mate(momAndDads);
		System.out.println(a.getNumInd(0));
		System.out.println(a.getNumInd(1));
		System.out.println(a.getNumInd(2));
		System.out.println(kid);
		
		a.sort(3);
		for(int i = 0; i < a.sorted.length; i++){
			System.out.println(a.numIndPop[a.sorted[i]].getNumIndFitness());
		}
		*/

		/*Alg a = new Alg();
		System.out.println("Alg: \n" + a + "\n");

		System.out.println(a.numIndTotalFitness);

		System.out.println(a.numIndAvgFitness);

		System.out.println(a.sorted);*/
		Alg a = new Alg();
		System.out.println(a);

		//System.out.prtintln(
	}

}