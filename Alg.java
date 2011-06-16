public class Alg{
	
	final int NUMIND_POP_SIZE = 30;
	
	private long numIndTotalFitness;
	private int[] sorted; //indices of NumInds in pop. in order of fitness
				//sorted[0] = best
	private long algFitness; 	//name generic things  like "fitness"
	private long numIndAvgFitness;	//according to the class
	private NumInd[] numIndPop;	//to avoid confusion
	
	
	/*variables to be optimized*/
	
	int selection; //roulette selection on a subpopulation of what size?
		       //smaller populations are like randomized tournament selection
		     
	int elitism;//how many of the best are reserved spots?
	
	//Polynomial interpretFitness; //for the purpose of roulette selection
				     //Alg first feeds NumInd fitnesses
				     //into a polynomial function
				     
	int reproductionType; //asexual, sexual, menage a trois, etc.
	
	//mutation rate stuff
	
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
	
	//why does this exist?
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
	
	public NumInd[] copyNumIndPop(){
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
	
	//find best n NumInds for elitism - reserved spots
	public void sort(int n){
		for(int i = 0; i < n; i++){
			int best = i;
			for(int j = i; j < sorted.length; j++){
				if(numIndPop[sorted[j]].getNumIndFitness() > numIndPop[sorted[best]].getNumIndFitness()){
					best = j;
				}
			}
			swap(i, best);
		}
	}
	
	public void swap(int a, int b){
		int tem = sorted[a];
		sorted[a] = sorted[b];
		sorted[b] = tem;
	}
	
	public void matingSeason() {
		NumInd[] temPop = new NumInd[NUMIND_POP_SIZE];
		
		//put the best (int) elitism numinds at the front of sorted
		sort(elitism);
		
		//fill tempop with the sorted numinds
		for(int k = 0; k < elitism; k++){
			temPop[k] = numIndPop[sorted[k]];
		}
		
		//fill remaining population with offspring
		for(int m = elitism; m < NUMIND_POP_SIZE; m++){
			
			//choose a certain number of parents
			int[] parents = new int[reproductionType];
			for(int i = 0; i < parents.length; i++){
				/*uncomment this!*/
				//parents[i] = selectParent();
			}
			
			temPop[m] = mate(parents);
		}
		/*uncomment this!*/
		//mutate();
	}
	
	public NumInd mate(int[] parents) {
		//parents is the list of numIndPop indices of the parents
		NumInd child = new NumInd();
		for(int i = 0; i < child.CHROMOSOME_SIZE; i++){
			NumInd parent = numIndPop[parents[(int)(parents.length*Math.random())]];
			child.setNumIndGene(i, parent);
		}
		return child;
	}
	
	/*must not choose same parent twice for a particular mating.
	this is not accounted for now.*/
	public NumInd selectParent(){
		
		//holds indices referring to numIndPop indices of NumInds
		//to be considered for mating
		int[] subPop = new int[selection];
		//if selection = NUMIND_POP_SIZE, subpop = pop and it is 
		//simple roulette selection. Otherwise, weighted random tournament
		for(int i = 0; i < selection; i++){
			//also, should not choose the same numind twice
			//to be part of the subpopulation. not accounted for.
			subPop[i] = (int)(NUMIND_POP_SIZE * Math.random());
		}
		return roulette(subPop);
	}
	
	//roulette selection with array of numIndPop indices
	public NumInd roulette(int[] pop){
		return null;
	}
	
	public static void main(String[] args) {
		
		Alg a = new Alg();
		System.out.println(a);
	/*	a.setNumIndAvgFitness();
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
		System.out.println(kid);*/
		
		a.sort(3);
		for(int i = 0; i < a.sorted.length; i++){
			System.out.println(a.numIndPop[a.sorted[i]].getNumIndFitness());
		}
	}
	
}