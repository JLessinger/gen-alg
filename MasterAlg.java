/*MasterAlg indirectly optimizes NumInds by optimizing Algs which are optimizing NumInds.
Alg fitness is based on how well the Alg optimizes NumInds in 100 generations,
so overall MasterAlg performance should increase.

MasterAlg has the same variables as Alg - elitism, selection, crossover, and mutation rates.
Each generation, MasterAlg sets these according to the best-performing Alg in that generation.
This assumes that the same values for the given variables are suitable for optimizing both
Algs and NumInds, which is not necessarily true. However, notice that the population sizes within
MasterAlg and Alg are the same, so this assumption is plausible.*/
class MasterAlg{
	
	final int ALG_POP_SIZE = 30;
	
	private Alg[] algPop;
	private Alg template;//copy it many times into algPop
	private long algTotalFitness;
	private double algAvgFitness;
	private int[] Algssorted; //indices of Algs in pop in order of fitness
				//sorted[0] = best
	
	private int selection;
	private int elitism;
	private int crossover;
	private double algMutateRate;
	private double algGeneMutateRate;
	private double algGeneBitMutateRate;
	
	
	public MasterAlg() {
		
		algPop = new Alg[ALG_POP_SIZE];
		template = new Alg();
		for(int i = 0; i < ALG_POP_SIZE; i++){
			algPop[i] = new Alg(template);
		}
	}
	
	public void setAlgTotAvgFitness(){
		algTotalFitness = 0;
		for(int i = 0; i < ALG_POP_SIZE; i++){
			algTotalFitness += algPop[i].getAlgFitness();
		}
		algAvgFitness = 0;
		algAvgFitness = (double) algTotalFitness / ALG_POP_SIZE;
	}
	
	public String toString(){
		String s = "";
		s += "\nselection:" + selection + "\nelitism:" + elitism + "\ncrossover:" + crossover
		+ "\nnumIndMutateRate:" + algMutateRate + "\nnumIndGeneMutateRate:" + algGeneMutateRate
		+ "\nnumIndGeneBitMutateRate:" + algGeneBitMutateRate //+ "\nnumber of bit mutations expected"
		//+ NUMIND_POP_SIZE * NumInd.NUMIND_CHROMOSOME_SIZE * NumInd.NUMIND_GENE_SIZE 
		//* numIndMutateRate * numIndGeneMutateRate * numIndGeneBitMutateRate  
		+ "\n\n";
		for(int i = 0; i < ALG_POP_SIZE; i++){
			s += "Alg " + i + ": fitness = " + algPop[i].getAlgFitness() + "\n";
		}
		setAlgTotAvgFitness();
		s += "\nTotal Alg Fitness = " + algTotalFitness + "\n";
		s += "Average Alg Fitness = " + algAvgFitness;
		return s;
	}
	
	public static void main(String[] args){
		MasterAlg m = new MasterAlg();
		System.out.println(m);
	}
}